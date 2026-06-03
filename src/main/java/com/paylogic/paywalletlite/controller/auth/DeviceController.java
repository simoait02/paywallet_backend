package com.paylogic.paywalletlite.controller.auth;

import com.paylogic.paywalletlite.domain.identity.Device;
import com.paylogic.paywalletlite.domain.identity.enums.DeviceStatus;
import com.paylogic.paywalletlite.dto.response.ApiErrorResponseDto;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.service.identity.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/devices")
public class DeviceController {

    private final DeviceService deviceService;

    @Autowired
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerDevice(
            @RequestParam(name = "userId") UUID userId,
            @RequestParam(name = "deviceName") String deviceName,
            @RequestParam(name = "platform") String platform,
            @RequestParam(name = "hardwareId") String hardwareId,
            @RequestParam(name = "isPrimary", defaultValue = "false") boolean isPrimary) {
        try {
            Device device = deviceService.registerDevice(userId, deviceName, platform, hardwareId, isPrimary);
            return ResponseEntity.status(HttpStatus.CREATED).body(device);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorResponseDto("ERROR", e.getMessage(), null));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Device>> getUserDevices(@PathVariable UUID userId) {
        return ResponseEntity.ok(deviceService.findByUserId(userId));
    }

    @GetMapping("/{deviceId}")
    public ResponseEntity<Device> getDevice(@PathVariable UUID deviceId) {
        return ResponseEntity.ok(deviceService.findById(deviceId));
    }

    @PutMapping("/{deviceId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable UUID deviceId, @RequestParam DeviceStatus status) {
        deviceService.updateStatus(deviceId, status);
        return ResponseEntity.ok(new ApiErrorResponseDto("SUCCESS", "Device status updated", null));
    }

    @PostMapping("/{deviceId}/revoke")
    public ResponseEntity<?> revokeDevice(@PathVariable UUID deviceId) {
        deviceService.revokeDevice(deviceId);
        return ResponseEntity.ok(new ApiErrorResponseDto("SUCCESS", "Device revoked", null));
    }

    @DeleteMapping("/{deviceId}")
    public ResponseEntity<?> deleteDevice(@PathVariable UUID deviceId) {
        deviceService.deleteDevice(deviceId);
        return ResponseEntity.ok(new ApiErrorResponseDto("SUCCESS", "Device deleted", null));
    }
}