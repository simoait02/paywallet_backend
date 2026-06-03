package com.paylogic.paywalletlite.repository.identity;

import com.paylogic.paywalletlite.domain.identity.DeviceSession;
import com.paylogic.paywalletlite.domain.identity.enums.SessionStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceSessionRepository {
    DeviceSession save(DeviceSession session);
    Optional<DeviceSession> findById(UUID sessionId);
    Optional<DeviceSession> findByTokenHash(String jwtTokenHash);
    List<DeviceSession> findByDeviceId(UUID deviceId);
    List<DeviceSession> findByUserId(UUID userId);
    List<DeviceSession> findActiveByUserId(UUID userId);
    List<DeviceSession> findExpiredSessions();
    void delete(DeviceSession session);
    void revokeSession(UUID sessionId);
    void revokeAllUserSessions(UUID userId);
    void revokeAllDeviceSessions(UUID deviceId);
    void updateLastActivity(UUID sessionId);
    long countActiveSessionsByUser(UUID userId);
}