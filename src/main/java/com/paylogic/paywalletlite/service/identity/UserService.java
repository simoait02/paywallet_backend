package com.paylogic.paywalletlite.service.identity;

import com.paylogic.paywalletlite.domain.identity.User;
import com.paylogic.paywalletlite.domain.identity.enums.AccountStatus;
import com.paylogic.paywalletlite.domain.identity.enums.RoleType;
import com.paylogic.paywalletlite.dto.request.RegisterRequestDto;
import com.paylogic.paywalletlite.exception.BusinessException;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User registerUser(RegisterRequestDto request) throws BusinessException;
    User findById(UUID userId);
    User findByPhoneNumber(String phoneNumber);
    User findByEmail(String email);
    List<User> findAll();
    List<User> findByStatus(AccountStatus status);
    List<User> findByRole(RoleType role);
    void updateStatus(UUID userId, AccountStatus status);
    void lockAccount(UUID userId, int minutes);
    void unlockAccount(UUID userId);
    void deleteUser(UUID userId);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
    void recordSuccessfulLogin(UUID userId);
    void recordFailedLogin(UUID userId);
    boolean isAccountLocked(UUID userId);
}