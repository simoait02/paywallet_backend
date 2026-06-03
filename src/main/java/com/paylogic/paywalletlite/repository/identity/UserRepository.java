package com.paylogic.paywalletlite.repository.identity;

import com.paylogic.paywalletlite.domain.identity.User;
import com.paylogic.paywalletlite.domain.identity.enums.AccountStatus;
import com.paylogic.paywalletlite.domain.identity.enums.RoleType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID userId);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByEmail(String email);
    Optional<User> findByNationalIdNumber(String nationalIdNumber);
    List<User> findAll();
    List<User> findByStatus(AccountStatus status);
    List<User> findByRole(RoleType role);
    void delete(User user);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
    void updateLastLogin(UUID userId);
    void incrementFailedAttempts(UUID userId);
    void resetFailedAttempts(UUID userId);
    void lockAccount(UUID userId, java.time.LocalDateTime lockedUntil);
    void updateStatus(UUID userId, AccountStatus status);
}