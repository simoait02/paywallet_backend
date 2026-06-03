package com.paylogic.paywalletlite.service.identity;

import com.paylogic.paywalletlite.domain.identity.Device;
import com.paylogic.paywalletlite.domain.identity.User;
import com.paylogic.paywalletlite.domain.identity.enums.*;
import com.paylogic.paywalletlite.dto.request.RegisterRequestDto;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.repository.identity.DeviceRepository;
import com.paylogic.paywalletlite.repository.identity.UserRepository;
import com.paylogic.paywalletlite.security.crypto.HashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, DeviceRepository deviceRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.deviceRepository = deviceRepository;

    }

    @Override
    @Transactional
    public User registerUser(RegisterRequestDto request) throws BusinessException {
        // Vérifications existantes
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BusinessException("Phone number already registered: " + request.getPhoneNumber());
        }
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already registered: " + request.getEmail());
        }

        // 1. Créer l'utilisateur
        User user = new User();
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setNationalIdNumber(request.getNationalIdNumber());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPinHash(request.getPin() != null ? passwordEncoder.encode(request.getPin()) : null);
        user.setRole(request.getRole() != null ? request.getRole() : RoleType.CUSTOMER);
        user.setStatus(AccountStatus.ACTIVE);
        user.setRegistrationTimestamp(LocalDateTime.now());
        user.setFailedLoginAttempts(0);
        user.setKycVerificationStatus(KYCStatus.PENDING);

        user = userRepository.save(user);

        // 2. Créer et associer le device automatiquement
        Device device = new Device();
        device.setUser(user);
        device.setDeviceName(request.getDeviceName());
        device.setHardwareId(request.getHardwareId());
        device.setPlatform(DevicePlatform.valueOf(request.getPlatform()));
        device.setRegisteredAt(LocalDateTime.now());
        device.setLastSeen(LocalDateTime.now());
        device.setStatus(DeviceStatus.ACTIVE);
        device.setIsPrimary(true); // Premier device = device principal

        deviceRepository.save(device);

        return user;
    }

    @Override
    public User findById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found: " + userId));
    }

    @Override
    public User findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new BusinessException("User not found with phone: " + phoneNumber));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found with email: " + email));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public List<User> findByStatus(AccountStatus status) {
        return userRepository.findByStatus(status);
    }

    @Override
    public List<User> findByRole(RoleType role) {
        return userRepository.findByRole(role);
    }

    @Override
    @Transactional
    public void updateStatus(UUID userId, AccountStatus status) {
        findById(userId); // validation
        userRepository.updateStatus(userId, status);
    }

    @Override
    @Transactional
    public void lockAccount(UUID userId, int minutes) {
        findById(userId);
        LocalDateTime lockedUntil = LocalDateTime.now().plusMinutes(minutes);
        userRepository.lockAccount(userId, lockedUntil);
    }

    @Override
    @Transactional
    public void unlockAccount(UUID userId) {
        findById(userId);
        userRepository.resetFailedAttempts(userId);
        userRepository.updateStatus(userId, AccountStatus.ACTIVE);
    }

    @Override
    @Transactional
    public void deleteUser(UUID userId) {
        User user = findById(userId);
        userRepository.delete(user);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public void recordSuccessfulLogin(UUID userId) {
        userRepository.updateLastLogin(userId);
        userRepository.resetFailedAttempts(userId);
    }

    @Override
    @Transactional
    public void recordFailedLogin(UUID userId) {
        userRepository.incrementFailedAttempts(userId);
        User user = findById(userId);
        if (user.getFailedLoginAttempts() >= 5) {
            lockAccount(userId, 30);
        }
    }

    @Override
    public boolean isAccountLocked(UUID userId) {
        User user = findById(userId);
        return user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now());
    }
}