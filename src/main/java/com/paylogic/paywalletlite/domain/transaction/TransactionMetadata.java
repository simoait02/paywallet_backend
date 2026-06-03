package com.paylogic.paywalletlite.domain.transaction;

import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "transaction_metadata", schema = "pwl_app")
public class TransactionMetadata {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "metadata_id", updatable = false, nullable = false)
    private UUID metadataId;

    @Column(name = "transaction_id", nullable = false, unique = true)
    private UUID transactionId;

    @Column(name = "device_id_sender", length = 100)
    private String deviceIdSender;

    @Column(name = "device_id_receiver", length = 100)
    private String deviceIdReceiver;

    @Column(name = "location_lat", length = 50)
    private String locationLat;

    @Column(name = "location_lon", length = 50)
    private String locationLon;

    @Column(name = "network_type", length = 50)
    private String networkType;

    @Column(name = "offline_duration_hours")
    private Integer offlineDurationHours;

    @Column(name = "qr_payload_hash", length = 255)
    private String qrPayloadHash;

    @Column(name = "nfc_session_id", length = 255)
    private String nfcSessionId;

    public TransactionMetadata() {}

    // Getters et Setters
    public UUID getMetadataId() { return metadataId; }
    public void setMetadataId(UUID metadataId) { this.metadataId = metadataId; }

    public UUID getTransactionId() { return transactionId; }
    public void setTransactionId(UUID transactionId) { this.transactionId = transactionId; }

    public String getDeviceIdSender() { return deviceIdSender; }
    public void setDeviceIdSender(String deviceIdSender) { this.deviceIdSender = deviceIdSender; }

    public String getDeviceIdReceiver() { return deviceIdReceiver; }
    public void setDeviceIdReceiver(String deviceIdReceiver) { this.deviceIdReceiver = deviceIdReceiver; }

    public String getLocationLat() { return locationLat; }
    public void setLocationLat(String locationLat) { this.locationLat = locationLat; }

    public String getLocationLon() { return locationLon; }
    public void setLocationLon(String locationLon) { this.locationLon = locationLon; }

    public String getNetworkType() { return networkType; }
    public void setNetworkType(String networkType) { this.networkType = networkType; }

    public Integer getOfflineDurationHours() { return offlineDurationHours; }
    public void setOfflineDurationHours(Integer offlineDurationHours) { this.offlineDurationHours = offlineDurationHours; }

    public String getQrPayloadHash() { return qrPayloadHash; }
    public void setQrPayloadHash(String qrPayloadHash) { this.qrPayloadHash = qrPayloadHash; }

    public String getNfcSessionId() { return nfcSessionId; }
    public void setNfcSessionId(String nfcSessionId) { this.nfcSessionId = nfcSessionId; }
}