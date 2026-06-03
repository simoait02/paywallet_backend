package com.paylogic.paywalletlite.service.identity;

import com.paylogic.paywalletlite.domain.identity.Device;
import com.paylogic.paywalletlite.domain.identity.User;
import com.paylogic.paywalletlite.domain.identity.enums.DevicePlatform;
import com.paylogic.paywalletlite.domain.identity.enums.DeviceStatus;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.repository.identity.DeviceRepository;
import com.paylogic.paywalletlite.repository.identity.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    @Autowired
    public DeviceServiceImpl(DeviceRepository deviceRepository, UserRepository userRepository) {
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Device registerDevice(UUID userId, String deviceName, String platform, String hardwareId, boolean isPrimary) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found: " + userId));

        if (deviceRepository.findByHardwareId(hardwareId).isPresent()) {
            throw new BusinessException("Device already registered: " + hardwareId);
        }

        if (isPrimary) {
            // Revoke previous primary device
            List<Device> primaries = deviceRepository.findPrimaryDevicesByUserId(userId);
            for (Device d : primaries) {
                d.setIsPrimary(false);
                deviceRepository.save(d);
            }
        }

        Device device = new Device();
        device.setDeviceId(UUID.randomUUID());
        device.setUserId(user.getUserId());
        device.setDeviceName(deviceName);
        device.setPlatform(DevicePlatform.valueOf(platform.toUpperCase()));
        device.setHardwareId(hardwareId);
        device.setRegisteredAt(LocalDateTime.now());
        device.setLastSeen(LocalDateTime.now());
        device.setStatus(DeviceStatus.ACTIVE);
        device.setIsPrimary(isPrimary);

        return deviceRepository.save(device);
    }

    @Override
    public Device findById(UUID deviceId) {
        return deviceRepository.findById(deviceId)
                .orElseThrow(() -> new BusinessException("Device not found: " + deviceId));
    }

    @Override
    public Device findByHardwareId(String hardwareId) {
        return deviceRepository.findByHardwareId(hardwareId)
                .orElseThrow(() -> new BusinessException("Device not found: " + hardwareId));
    }

    @Override
    public List<Device> findByUserId(UUID userId) {
        return deviceRepository.findByUserId(userId);
    }

    @Override
    public List<Device> findByStatus(DeviceStatus status) {
        return deviceRepository.findByStatus(status);
    }

    @Override
    @Transactional
    public void updateStatus(UUID deviceId, DeviceStatus status) {
        findById(deviceId);
        deviceRepository.updateStatus(deviceId, status);
    }

    @Override
    @Transactional
    public void updateLastSeen(UUID deviceId) {
        findById(deviceId);
        deviceRepository.updateLastSeen(deviceId);
    }

    @Override
    @Transactional
    public void revokeDevice(UUID deviceId) {
        updateStatus(deviceId, DeviceStatus.REVOKED);
    }

    @Override
    @Transactional
    public void revokeAllUserDevicesExcept(UUID userId, UUID exceptDeviceId) {
        deviceRepository.revokeAllDevicesExcept(userId, exceptDeviceId);
    }

    @Override
    @Transactional
    public void deleteDevice(UUID deviceId) {
        Device device = findById(deviceId);
        deviceRepository.delete(device);
    }

    @Override
    public boolean isDeviceRegistered(String hardwareId) {
        return deviceRepository.findByHardwareId(hardwareId).isPresent();
    }

    @Override
    public Device getPrimaryDevice(UUID userId) {
        List<Device> primaries = deviceRepository.findPrimaryDevicesByUserId(userId);
        if (primaries.isEmpty()) {
            throw new BusinessException("No primary device found for user: " + userId);
        }
        return primaries.get(0);
    }
}