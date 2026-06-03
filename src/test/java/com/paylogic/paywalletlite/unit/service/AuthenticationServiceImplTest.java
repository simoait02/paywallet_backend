package com.paylogic.paywalletlite.unit.service;

import com.paylogic.paywalletlite.domain.identity.Device;
import com.paylogic.paywalletlite.domain.identity.User;
import com.paylogic.paywalletlite.domain.identity.enums.AccountStatus;
import com.paylogic.paywalletlite.domain.identity.enums.DeviceStatus;
import com.paylogic.paywalletlite.domain.identity.enums.RoleType;
import com.paylogic.paywalletlite.dto.request.LoginRequestDto;
import com.paylogic.paywalletlite.dto.response.AuthResponseDto;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.repository.identity.DeviceRepository;
import com.paylogic.paywalletlite.repository.identity.DeviceSessionRepository;
import com.paylogic.paywalletlite.repository.identity.UserRepository;
import com.paylogic.paywalletlite.config.security.JwtTokenProvider;
import com.paylogic.paywalletlite.service.identity.AuthenticationServiceImpl;
import com.paylogic.paywalletlite.service.identity.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private DeviceRepository deviceRepository;
    @Mock
    private DeviceSessionRepository sessionRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserService userService;

    @InjectMocks
    private AuthenticationServiceImpl authService;

    private User testUser;
    private Device testDevice;
    private LoginRequestDto loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(UUID.randomUUID());
        testUser.setPhoneNumber("+212600000000");
        testUser.setPasswordHash("encoded_password");
        testUser.setStatus(AccountStatus.ACTIVE);
        testUser.setRole(RoleType.CUSTOMER);
        testUser.setFailedLoginAttempts(0);

        testDevice = new Device();
        testDevice.setDeviceId(UUID.randomUUID());
        testDevice.setUserId(testUser.getUserId());
        testDevice.setHardwareId("HW123456");
        testDevice.setStatus(DeviceStatus.ACTIVE);

        loginRequest = new LoginRequestDto();
        loginRequest.setPhoneNumber("+212600000000");
        loginRequest.setPassword("password123");
        loginRequest.setHardwareId("HW123456");
    }

    @Test
    void authenticate_Success() {
        when(userRepository.findByPhoneNumber("+212600000000")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encoded_password")).thenReturn(true);
        when(deviceRepository.findByUserIdAndHardwareId(any(), any())).thenReturn(Optional.of(testDevice));
        when(jwtTokenProvider.generateAccessToken(any(), any())).thenReturn("access_token");
        when(jwtTokenProvider.generateRefreshToken(any())).thenReturn("refresh_token");
        when(sessionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        AuthResponseDto response = authService.authenticate(loginRequest);

        assertNotNull(response);
        assertEquals("access_token", response.getAccessToken());
        assertEquals("refresh_token", response.getRefreshToken());
        assertEquals(testUser.getUserId(), response.getUserId());
        verify(userService).recordSuccessfulLogin(testUser.getUserId());
    }

    @Test
    void authenticate_InvalidPassword_ThrowsException() {
        when(userRepository.findByPhoneNumber("+212600000000")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encoded_password")).thenReturn(false);

        assertThrows(BusinessException.class, () -> authService.authenticate(loginRequest));
        verify(userService).recordFailedLogin(testUser.getUserId());
    }

    @Test
    void authenticate_LockedAccount_ThrowsException() {
        when(userRepository.findByPhoneNumber("+212600000000")).thenReturn(Optional.of(testUser));
        when(userService.isAccountLocked(testUser.getUserId())).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.authenticate(loginRequest));

        assertTrue(exception.getMessage().contains("locked"));
    }

    @Test
    void authenticate_InactiveDevice_ThrowsException() {
        testDevice.setStatus(DeviceStatus.REVOKED);
        when(userRepository.findByPhoneNumber("+212600000000")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(deviceRepository.findByUserIdAndHardwareId(any(), any())).thenReturn(Optional.of(testDevice));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.authenticate(loginRequest));

        assertTrue(exception.getMessage().contains("Device is not active"));
    }

    @Test
    void validateToken_Success() {
        when(jwtTokenProvider.validateToken("valid_token")).thenReturn(true);

        assertTrue(authService.validateToken("valid_token"));
    }
}