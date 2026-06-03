package com.paylogic.paywalletlite.unit.service;

import com.paylogic.paywalletlite.domain.identity.User;
import com.paylogic.paywalletlite.domain.identity.enums.AccountStatus;
import com.paylogic.paywalletlite.domain.identity.enums.RoleType;
import com.paylogic.paywalletlite.dto.request.RegisterRequestDto;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.repository.identity.UserRepository;
import com.paylogic.paywalletlite.service.identity.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private RegisterRequestDto registerRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(UUID.randomUUID());
        testUser.setPhoneNumber("+212600000000");
        testUser.setEmail("ibringo@paylogic.ma");
        testUser.setFirstName("Boureima");
        testUser.setLastName("IBRINGO");
        testUser.setStatus(AccountStatus.ACTIVE);
        testUser.setRole(RoleType.CUSTOMER);
        testUser.setFailedLoginAttempts(0);

        registerRequest = new RegisterRequestDto();
        registerRequest.setPhoneNumber("+212611111111");
        registerRequest.setEmail("ibringo@paylogic.ma");
        registerRequest.setFirstName("Boureima");
        registerRequest.setLastName("IBRINGO");
        registerRequest.setPassword("password123");
        registerRequest.setPin("1234");
    }

    @Test
    void registerUser_Success() {
        when(userRepository.existsByPhoneNumber(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.registerUser(registerRequest);

        assertNotNull(result);
        assertEquals("+212611111111", result.getPhoneNumber());
        assertEquals(RoleType.CUSTOMER, result.getRole());
        assertNotNull(result.getUserId());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_DuplicatePhone_ThrowsException() {
        when(userRepository.existsByPhoneNumber(any())).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.registerUser(registerRequest));

        assertTrue(exception.getMessage().contains("Phone number already registered"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void findById_Success() {
        when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.of(testUser));

        User result = userService.findById(testUser.getUserId());

        assertEquals(testUser.getUserId(), result.getUserId());
    }

    @Test
    void findById_NotFound_ThrowsException() {
        UUID randomId = UUID.randomUUID();
        when(userRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> userService.findById(randomId));
    }

    @Test
    void recordFailedLogin_LocksAccountAfter5Attempts() {
        // Configuration : user avec 4 attempts → après increment = 5
        User userWith5Attempts = new User();
        userWith5Attempts.setUserId(testUser.getUserId());
        userWith5Attempts.setFailedLoginAttempts(5);  // ← 5 attempts
        userWith5Attempts.setStatus(AccountStatus.ACTIVE);

        // Premier findById (dans recordFailedLogin) retourne userWith5Attempts
        when(userRepository.findById(testUser.getUserId()))
                .thenReturn(Optional.of(userWith5Attempts));

        userService.recordFailedLogin(testUser.getUserId());

        verify(userRepository).incrementFailedAttempts(testUser.getUserId());
        verify(userRepository).lockAccount(eq(testUser.getUserId()), any(LocalDateTime.class));
    }

    @Test
    void isAccountLocked_True() {
        testUser.setLockedUntil(LocalDateTime.now().plusMinutes(30));
        when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.of(testUser));

        assertTrue(userService.isAccountLocked(testUser.getUserId()));
    }

    @Test
    void isAccountLocked_False() {
        testUser.setLockedUntil(null);
        when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.of(testUser));

        assertFalse(userService.isAccountLocked(testUser.getUserId()));
    }
}