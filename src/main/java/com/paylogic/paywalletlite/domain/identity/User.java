package com.paylogic.paywalletlite.domain.identity;

import com.paylogic.paywalletlite.domain.identity.enums.AccountStatus;
import com.paylogic.paywalletlite.domain.identity.enums.KYCStatus;
import com.paylogic.paywalletlite.domain.identity.enums.RoleType;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entité représentant un utilisateur du système PayWallet Lite.
 * Conforme au diagramme de classe UML.
 */
@Entity
@Table(name = "users", schema = "pwl_app")
public class User {

    // --- Identifiant principal ---
    @Id
    @GeneratedValue
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID userId;

    // --- Informations personnelles ---
    @Column(name = "first_name", length = 100, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 100, nullable = false)
    private String lastName;

    @Column(name = "phone_number", unique = true, length = 20)
    private String phoneNumber;

    @Column(name = "email", length = 200)
    private String email;

    @Column(name = "national_id_number", length = 50)
    private String nationalIdNumber;

    // --- Sécurité ---
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "pin_hash")
    private String pinHash;

    // --- Statuts et métadonnées ---
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private RoleType role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AccountStatus status;

    @Column(name = "registration_timestamp", updatable = false)
    private LocalDateTime registrationTimestamp;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Column(name = "kyc_verification_status")
    private KYCStatus kycVerificationStatus;

    public List<DeviceSession> getSessions() {
        return sessions;
    }

    public void setSessions(List<DeviceSession> sessions) {
        this.sessions = sessions;
    }

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<DeviceSession> sessions = new ArrayList<>();
    // --- Relations ---
    //@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    //private KYCProfile kycProfile;

    //@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    //private List<Device> devices = new ArrayList<>();

    // --- Hooks ---
    @PrePersist
    protected void onCreate() {
        registrationTimestamp = LocalDateTime.now();
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNationalIdNumber() {
        return nationalIdNumber;
    }

    public void setNationalIdNumber(String nationalIdNumber) {
        this.nationalIdNumber = nationalIdNumber;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPinHash() {
        return pinHash;
    }

    public void setPinHash(String pinHash) {
        this.pinHash = pinHash;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public LocalDateTime getRegistrationTimestamp() {
        return registrationTimestamp;
    }

    public void setRegistrationTimestamp(LocalDateTime registrationTimestamp) {
        this.registrationTimestamp = registrationTimestamp;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Integer getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(Integer failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public LocalDateTime getLockedUntil() {
        return lockedUntil;
    }

    public void setLockedUntil(LocalDateTime lockedUntil) {
        this.lockedUntil = lockedUntil;
    }

    public KYCStatus getKycVerificationStatus() {
        return kycVerificationStatus;
    }

    public void setKycVerificationStatus(KYCStatus kycVerificationStatus) {
        this.kycVerificationStatus = kycVerificationStatus;
    }


}

/**
 *     public KYCProfile getKycProfile() {
 *         return kycProfile;
 *     }
 *
 *     public void setKycProfile(KYCProfile kycProfile) {
 *         this.kycProfile = kycProfile;
 *     }
 *
 *     public List<Device> getDevices() {
 *         return devices;
 *     }
 *
 *     public void setDevices(List<Device> devices) {
 *         this.devices = devices;
 *     }
 *
 *
 * */