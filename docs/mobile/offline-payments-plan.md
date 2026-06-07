# PayWallet Lite — Offline Payments Feature

Two PayWallet Lite users can pay each other **with no internet connection on either
device**. The transfer is performed peer-to-peer over **NFC and Bluetooth**: NFC is the
proximity gesture that initiates a session, Bluetooth carries the encrypted exchange of
the tokens, their custody chain, and the new signatures. The transfer is later reconciled
with the central ledger when either device next comes online. The feature spans both
phones and the backend, and is described here from the perspective of what it does, not
how it is built.

| # | Property | Mechanism |
|---|---|---|
| 1 | Each user's wallet has a long-lived signing key | Generated on the device, lives in hardware secure storage, never leaves it |
| 2 | The local store of payment tokens is opaque on disk | Encrypted database whose key is itself wrapped by the device's secure element |
| 3 | A tap between two phones is confidential and authenticated | Per-tap ephemeral key agreement, signed by both wallet keys, mutual certificate check, AES-GCM thereafter |
| 4 | The transfer uses NFC and Bluetooth together | NFC is the proximity tap that hands one device a pairing token plus the other device's Bluetooth address; Bluetooth carries the encrypted session that actually moves the tokens. A QR scan substitutes for the NFC tap only when the payer is on iOS (since iOS apps cannot emit NFC). |
| 5 | The backend never sees the wallet's private key | At registration the device sends a certificate signing request; the backend's certificate authority signs the public key |
| 6 | The ledger eventually catches up | Each device queues completed transfers and posts them when connectivity returns |

---

## 1. Threat Model

The feature is designed against the following risks. ✅ = covered; ⚠️ = partially mitigated;
❌ = accepted residual risk.

| # | Asset | Threat | How the feature defends it |
|---|---|---|---|
| T1 | Wallet private key | Extraction from the device | ✅ Key generated inside the hardware secure element; the OS exposes only signing, not export |
| T2 | Wallet private key | Use after the device is stolen unlocked | ⚠️ Every signing operation requires a fresh biometric or PIN prompt |
| T3 | Local token store | Disclosure on a rooted or imaged device | ✅ The token database is encrypted; its passphrase is wrapped by the secure element |
| T4 | Peer payload | Eavesdropping by a nearby radio sniffer | ✅ Application-level encryption with a key derived per tap; link-layer encryption is not relied on |
| T5 | Peer identity | Impersonation or man-in-the-middle | ✅ Both sides verify each other's wallet certificate against a pinned CA before any payload is exchanged |
| T6 | Token | Double-spend by a dishonest payer (tapping multiple recipients before sync) | ⚠️ Per-token transfer cap and locally-seen-nonces narrow the window; backend reconciliation is authoritative and emits a fraud alert |
| T7 | Token | Replay of a captured bundle | ✅ Each token has a unique nonce that the device records on first sight and refuses thereafter |
| T8 | Backend signing key | Rotation or compromise | ✅ Devices cache multiple trusted server keys and refresh on every sync; tokens carry the public key under which they were issued |
| T9 | Device integrity | Rooted or jailbroken phone | ⚠️ Detected; per-transaction limit is lowered |
| T10 | Backup / restore | Wallet keys leaking via cloud backup | ✅ Hardware-bound keys are not backed up; a restored database is unusable without them |
| T11 | Bystander attack | Tricking the user into paying the wrong recipient | ✅ Counterparty's identity (from the verified certificate) is shown to the user before they confirm |
| T12 | Transport security | TLS interception on backend calls | ✅ Backend hosts are certificate-pinned |
| T13 | Lost device | Unrecoverable wallet | ❌ Accepted in v1: any tokens not yet synced are lost; redeemed value survives in the server-side balance |

---

## 2. Architecture at a Glance

```
   ┌──────────────────┐    NFC tap (bootstrap) + Bluetooth (session)    ┌──────────────────┐
   │   PAYER PHONE    │  ◄────────────────────────────────────────►    │   PAYEE PHONE    │
   │                  │                                               │                  │
   │  hardware key    │                                               │  hardware key    │
   │  encrypted DB    │                                               │  encrypted DB    │
   │  token chain     │                                               │  token chain     │
   │  pending sync    │                                               │  pending sync    │
   └────────┬─────────┘                                               └────────┬─────────┘
            │                                                                  │
            │           (each phone, independently, when online)               │
            ▼                                                                  ▼
                              ┌──────────────────────┐
                              │     BACKEND          │
                              │  authoritative ledger│
                              │  signing CA          │
                              │  sync endpoint       │
                              └──────────────────────┘
```

Two phones talk directly. The backend is only involved at three points:

1. **Onboarding** — the device sends its newly generated public key in a CSR; the backend's CA signs and returns a wallet certificate.
2. **Provisioning refresh** — the device fetches the CA trust anchor and the list of currently-trusted server signing keys; this is repeated periodically.
3. **Sync** — the device posts the chain of offline transfers it accumulated.

Between (1) and (3) the phones can transact for hours or days entirely offline.

---

## 3. Key Management

Every wallet has three pieces of cryptographic material on its device:

| Material | Where it lives | Used for |
|---|---|---|
| **Wallet signing key** | Hardware secure element, non-exportable, biometric-gated for use | Signs every outgoing transfer; signs the ephemeral key during the peer handshake |
| **Encrypted-DB passphrase** | Generated on first launch, wrapped by a secure-element-resident AES key | Unlocks the local token database at app startup |
| **Cached trust material** | Inside the encrypted DB: CA public key + list of trusted server signing keys + the wallet's own certificate | Verifies peer certificates and token signatures while offline |

The wallet signing key is generated **on the device** at registration. The device builds a
proof-of-possession (a signature over its userId, public key, and a fresh timestamp) and
sends it together with the public key to the backend. The backend verifies the
proof and, after operator approval, issues a certificate binding the wallet to that
public key. The private key never crosses the network.

### Biometric enrollment policy

If the user adds or removes a fingerprint, the platform invalidates any key bound to
the previous biometric set. The feature handles this with a two-key design:

- A **signing key** is biometric-gated and would be invalidated by enrollment changes.
- A **recovery key** is bound to the device PIN only and survives biometric changes.

On a detected enrollment change, the recovery key signs a key-rotation record that
re-binds the wallet to a freshly generated signing key. The user is asked to verify
their identity once during this flow; no funds are lost.

---

## 4. Local Token Store

Each device keeps a small encrypted database with the following content. Schemas are
described as concepts, not statements.

| Store | What it holds | Why |
|---|---|---|
| **Provisioning record** | The wallet's certificate, the CA's public key, the list of currently-trusted server signing keys | The trust anchors needed to verify any peer or any token while offline |
| **Tokens** | One row per payment token the device currently holds, with its value, nonce, expiry, transfer cap, current status, the backend signature stamped at issuance, and the issuer's public key at that moment | The spendable balance, plus the data needed to prove each token's authenticity to a peer |
| **Transfer chain** | One row per hop a token has taken, including who signed it, the timestamp, the amount, and the hash of the previous link | The chain of custody that any subsequent receiver verifies before accepting |
| **Pending sync queue** | One row per completed transfer (sent or received) that has not yet been confirmed by the backend | Drives the eventual reconciliation; never emptied until the backend acknowledges |
| **Seen nonces** | One row per nonce the device has ever processed, with an expiry | Refuses to accept a token bundle twice, even if a malicious peer replays it |
| **Audit log** | One append-only row per security-relevant event, chained with an HMAC so any tampering invalidates the chain | Tamper-evident history of what the wallet has done; consumable by a daily integrity check and by support flows |

The database is encrypted at rest. Its passphrase is itself sealed by a key that lives
in the device's secure element, so a hostile copy of the file alone is useless.

---

## 5. The Transfer: NFC and Bluetooth

A payment is a single user-perceived gesture — phones held together briefly — that
the device implements as a two-stage transport. **NFC** carries the very first
moment of the session (proximity tap); **Bluetooth** carries everything that
follows (the actual transfer of tokens, chain, signatures, and receipt).

### 5.1 The NFC tap

When the user initiates a payment, the payer's phone emits a short payload that
the payee's phone reads in passing. The payload is small and not secret:

- a one-shot **pairing token** (32 random bytes),
- the payer's **Bluetooth advertising address**,
- the role of each side (who is paying whom),
- an expiration timestamp a few seconds away.

The tap completes in well under a second; the user feels it as the touch itself.
The pairing token's value is that it will be mixed into the derivation of the
Bluetooth session key, cryptographically binding the imminent session to the
specific phones that just touched. A Bluetooth eavesdropper that did not witness
the tap cannot derive the session key.

A QR code substitutes for the NFC tap **only** when the payer is on iOS, since
iOS apps cannot emit NFC tags. The payload is identical; the user sees a QR on
the payer's screen (rotating every fifteen seconds) which the payee scans. The
rest of the session is unchanged.

### 5.2 The Bluetooth session

Immediately after the tap, the payee opens a Bluetooth connection to the
advertised address. All real payment data — tokens, chains of past hops, new
signatures, the receipt — flows over this connection, which is encrypted at the
application layer with a session key derived in the next four phases:

### Phase A — Hello

Each side sends its wallet certificate. Each side verifies the peer's certificate
against the CA public key it has cached. The pairing token from the bootstrap is
included; if it doesn't match what was just received via NFC or QR, the session
aborts (a peer that doesn't know the token wasn't part of the bootstrap gesture).

### Phase B — Key Agreement

Each side generates a fresh ephemeral key pair just for this session, and signs its
ephemeral public key with its long-lived wallet key. Each side verifies the peer's
signature against the certificate it just accepted in Phase A. Both sides combine
their ephemeral private key with the peer's ephemeral public key to derive the same
shared secret — neither having sent the secret over the air. A symmetric session
key is derived from this shared secret together with the pairing token from the
bootstrap, binding the session cryptographically to the specific gesture.

After this phase, all further messages are encrypted with the session key under
AES-GCM, with fresh nonces per direction. Ephemeral keys are discarded immediately
so even a future compromise of the long-lived wallet key does not reveal the
contents of past sessions (forward secrecy).

### Phase C — Transfer

The payer selects which token(s) to spend, builds for each one a new chain link
containing the payer's wallet, the payee's wallet, the amount, and a timestamp, and
signs each link with the wallet's hardware-resident signing key (which prompts the
user's biometric). The payer sends the payee the tokens, their existing chains of
hops, and the new links.

The payee verifies, for each token, every check below before accepting it. The
checks are ordered cheap to expensive:

| Check | What it confirms |
|---|---|
| Structural integrity | The token data isn't corrupted or malformed |
| Not expired, transfer cap not reached | The token is still usable |
| Nonce not in seen-nonces | This is not a replay |
| Token hash matches its content | The token wasn't tampered after issuance |
| Backend signature verifies against one of the trusted server keys | The token was really issued by the backend |
| First chain link's payer is the backend (genesis) | The chain starts where it should |
| Every hop's signature verifies against the previous holder's public key | The custody chain is intact |
| Final hop's payee equals "me" | The token really is being given to this device |
| The final payer's certificate matches the one shown in Phase A | The peer signing the transfer is the peer this device just authenticated |

If every check passes, the payee atomically: appends the new link to its local
chain, records the new token as received, queues a sync record, and sends back an
encrypted receipt.

### Phase D — Close

The payer reads the receipt, marks the token unspendable in its local store,
queues its own sync record, and tears down the Bluetooth connection. Both sides
zero the ephemeral keys and the session key.

A complete tap typically takes under two seconds on Android-to-Android and under
three seconds across platforms.

---

## 6. Token Lifecycle on the Device

A token on a device progresses through a small state machine. Spendability is
determined purely by status:

```
   from backend issuance               from a peer
            │                               │
            ▼                               ▼
       ALLOCATED ──── selected to pay ────► IN_FLIGHT ────► SPENT_LOCALLY ────► REDEEMED
                                                  ▲                ▲
       RECEIVED  ──── selected to pay ─────────── ┘                │
            │                                                      │
            │                                                      │
            └──────── eventually synced ─────────────────────────► REDEEMED

   any status, when the token's expiry passes, transitions to EXPIRED.
   a token rejected at sync as a double-spend transitions to FRAUD_FLAGGED.
```

The intermediate **IN_FLIGHT** status is the device's safeguard against
ambiguous-outcome taps: the token is marked unusable *before* the transfer message
goes over the radio, so a crash mid-transfer cannot leave the wallet thinking the
token is still available. If the peer's receipt arrives, the transition completes
to SPENT_LOCALLY. If the session is cleanly aborted without a receipt, the device
rolls back to ALLOCATED. If the session is dropped abruptly, the device assumes the
worst case (peer may have committed), keeps the token in SPENT_LOCALLY, and shows
the user a "status uncertain until sync" indicator.

The token selection routine that picks tokens for the next payment never sees
IN_FLIGHT or SPENT_LOCALLY tokens — this is how the local frontend prevents
double-spend by construction.

---

## 7. Sync Behavior

Each device maintains a pending-sync queue. Every successful transfer — sent or
received — appends a row to it. The queue drains automatically:

- Immediately after a successful tap.
- Whenever the device transitions from offline to online.
- Periodically in the background while connectivity is available.
- When the user opens the app, if the queue is non-empty and the last successful
  drain was more than a few minutes ago.

For each queued row the device posts the offline transaction (the token, its full
chain, the new hops, the involved certificates) to the backend. The backend
authoritatively verifies every signature and every chain step, applies the
transfer to the ledger, and replies.

Three outcomes are possible:

- **Accepted.** The backend acknowledges. The device deletes the queue row and
  promotes the involved tokens to REDEEMED. The local audit log records success.
- **Rejected.** The backend returns a 4xx with a reason (e.g., double-spend
  detected, signature invalid). The device marks the row as failed, marks the
  involved tokens FRAUD_FLAGGED, and surfaces a notification asking the user to
  review the discrepancy in their history.
- **Transient failure.** Network timeout, 5xx server response. The device leaves
  the row in the queue and retries with exponential backoff.

The post is idempotent: the same local transaction identifier always yields the
same backend outcome, so a retry of an already-applied transfer is harmless.

If the backend detects a double-spend — two valid chains for the same token — only
one wins. The winner is determined deterministically (by the chain that lands at
the backend first, with a tie-breaker on chain-hash ordering). The loser's device
sees its tokens flagged and offline payments are locked until the user re-onboards
or contacts support.

---

## 8. Failure Handling

Every failure path produces an audit log entry, and most are surfaced to the user
with an actionable message. The visible behaviors are:

| When this happens | What the user sees |
|---|---|
| The tap was too brief or the phones moved apart | "Hold phones together longer — try again." Nothing changed; no token left ALLOCATED. |
| Bluetooth failed to connect | "Move closer and try again." |
| The peer's certificate doesn't check out against the cached CA | Red banner with the failure reason; no retry offered. |
| The pairing token doesn't match (the peer didn't actually do the bootstrap) | Aborted with a "Possible interception detected" indicator; logged at high severity. |
| The token failed offline verification (expired, wrong signature, replayed nonce) | Specific message per reason; for replays, the most likely cause is an honest re-tap that the receiver had already absorbed. |
| The user has no spendable tokens for the requested amount | "Not enough offline balance." Balance breakdown shown. |
| The tap completed but the device couldn't confirm a receipt | Token marked SPENT_LOCALLY with "status uncertain — will be confirmed on next sync." |
| The user's biometrics changed since registration | Blocking dialog: "Sync your wallet now before continuing — your offline funds will become unspendable if you don't." After sync OK, signing key is regenerated transparently. |
| Sync reports a double-spend | Wallet locked for offline payments; user directed to support flow. |
| The device is rooted or jailbroken | App still works but with a reduced per-transaction limit. |

Across all failure paths, no token ever ends in a state where it might be double-spent
locally: a transfer either completes fully (status flipped, chain extended, sync row
queued, all in one transaction) or is rolled back to ALLOCATED.

---

## 9. Observability

The feature emits two kinds of records:

**Local audit log.** Every security-relevant event (provisioning, tap initiated,
handshake outcome, transfer sent, transfer received, sync outcome, biometric
change, integrity failure) is appended to a chain inside the encrypted database.
Each entry is HMAC-linked to the previous one, so any modification of past records
invalidates the rest of the chain. A daily integrity check catches tampering. The
log is the user's source of truth for their own activity; on request, a signed
diagnostic bundle can be exported and shared with support.

**Backend telemetry.** Aggregate counters and latencies are sent up at sync time —
counts of taps, breakdowns of rejection reasons, handshake latencies, queue depth.
No token identifiers, signatures, certificates, or wallet identifiers travel with
this telemetry; the wallet's pseudonymous identifier is the only piece of
correlation data.

---

## 10. End-to-End Walkthroughs

### Onboarding (online)

```
User                Device                              Backend
────                ──────                              ───────
register     ────►
                    create account
                    sign in, receive token
                    generate wallet key inside secure element
                    build proof-of-possession
                    submit { public key, PoP, attestation }
                                                ────►  verify PoP and attestation
                                                       create wallet (pending approval)
                                                ◄───
                    poll for approval
                                                ────►  operator approves; CA issues certificate
                                                ◄───
                    fetch provisioning bundle
                                                ────►  return { wallet certificate,
                                                                CA public key,
                                                                trusted server keys }
                                                ◄───
                    store provisioning in encrypted DB
                    wallet is now ready
```

### Offline payment

```
PAYER                                                             PAYEE
─────                                                             ─────
deliver pairing token (NFC tap or QR display)             ────►   read pairing token
                                                                  initiate Bluetooth connection
both sides exchange certificates, verify them
both sides agree on a fresh session key bound to the pairing token

confirm amount with biometric                                     verify token bundle:
sign each transfer link                                              - structural integrity
mark each spending token IN_FLIGHT                                   - expiry / transfer cap
send tokens + chains + new links            ─encrypted──────►        - nonce not seen
                                                                     - backend signature
                                                                     - chain-of-custody intact
                                                                     - final payee = me
                                                                     - peer cert matches handshake
                                                                  accept atomically (status,
                                                                  chain row, sync row, audit row)
                                                                  send encrypted receipt
mark tokens SPENT_LOCALLY                  ◄─encrypted─────
queue sync row
close session, zero ephemeral keys                                close session, zero ephemeral keys
```

### Sync (when online again)

```
DEVICE                                                            BACKEND
──────                                                            ───────
queue drainer triggered by connectivity
for each pending row:
    post offline transaction                ────►                 verify all signatures
                                                                  walk and validate each chain
                                                                  detect double-spend if any
                                                                  apply to ledger
                                                                  emit settlement event
                                            ◄────
    on accepted:
        delete queue row
        mark tokens REDEEMED
        audit: SYNC_OK
    on rejected:
        mark queue row failed
        mark involved tokens FRAUD_FLAGGED
        notify user
        audit: SYNC_REJECTED
    on transient failure:
        leave queue row, retry with backoff
```
