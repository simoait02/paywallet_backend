package com.paylogic.paywalletlite.service.identity;

import com.paylogic.paywalletlite.dto.request.LoginRequestDto;
import com.paylogic.paywalletlite.dto.response.AuthResponseDto;
import com.paylogic.paywalletlite.exception.BusinessException;

public interface AuthenticationService {
    AuthResponseDto authenticate(LoginRequestDto request) throws BusinessException;
    AuthResponseDto refreshToken(String refreshToken) throws BusinessException;
    void logout(String token);
    void logoutAllDevices(String userId);
    boolean validateToken(String token);
}