package com.paylogic.paywalletlite.service.identity;

import com.paylogic.paywalletlite.domain.identity.Device;
import com.paylogic.paywalletlite.domain.identity.enums.DeviceStatus;

import java.util.List;
import java.util.UUID;

public interface DeviceService {
    Device registerDevice(UUID userId, String deviceName, String platform, String hardwareId, boolean isPrimary);
    Device findById(UUID deviceId);
    Device findByHardwareId(String hardwareId);
    List<Device> findByUserId(UUID userId);
    List<Device> findByStatus(DeviceStatus status);
    void updateStatus(UUID deviceId, DeviceStatus status);
    void updateLastSeen(UUID deviceId);
    void revokeDevice(UUID deviceId);
    void revokeAllUserDevicesExcept(UUID userId, UUID exceptDeviceId);
    void deleteDevice(UUID deviceId);
    boolean isDeviceRegistered(String hardwareId);
    Device getPrimaryDevice(UUID userId);
}