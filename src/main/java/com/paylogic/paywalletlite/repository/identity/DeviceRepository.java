package com.paylogic.paywalletlite.repository.identity;

import com.paylogic.paywalletlite.domain.identity.Device;
import com.paylogic.paywalletlite.domain.identity.enums.DeviceStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceRepository {
    Device save(Device device);
    Optional<Device> findById(UUID deviceId);
    Optional<Device> findByHardwareId(String hardwareId);
    List<Device> findByUserId(UUID userId);
    List<Device> findByStatus(DeviceStatus status);
    List<Device> findPrimaryDevicesByUserId(UUID userId);
    Optional<Device> findByUserIdAndHardwareId(UUID userId, String hardwareId);
    void delete(Device device);
    void updateStatus(UUID deviceId, DeviceStatus status);
    void updateLastSeen(UUID deviceId);
    void revokeAllDevicesExcept(UUID userId, UUID exceptDeviceId);
}