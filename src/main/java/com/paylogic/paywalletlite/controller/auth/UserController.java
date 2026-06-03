package com.paylogic.paywalletlite.controller.auth;

import com.paylogic.paywalletlite.domain.identity.User;
import com.paylogic.paywalletlite.domain.identity.enums.AccountStatus;
import com.paylogic.paywalletlite.domain.identity.enums.RoleType;
import com.paylogic.paywalletlite.dto.request.RegisterRequestDto;
import com.paylogic.paywalletlite.dto.request.UpdateUserRequestDto;
import com.paylogic.paywalletlite.dto.response.ApiErrorResponseDto;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.service.identity.UserService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ==================== CREATE ====================

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody RegisterRequestDto request) {
        try {
            User user = userService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorResponseDto("ERROR", e.getMessage(), null));
        }
    }

    // ==================== READ ====================

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable UUID userId) {
        try {
            return ResponseEntity.ok(userService.findById(userId));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiErrorResponseDto("NOT_FOUND", e.getMessage(), null));
        }
    }

    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<?> getUserByPhone(@PathVariable String phoneNumber) {
        try {
            return ResponseEntity.ok(userService.findByPhoneNumber(phoneNumber));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiErrorResponseDto("NOT_FOUND", e.getMessage(), null));
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        try {
            return ResponseEntity.ok(userService.findByEmail(email));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiErrorResponseDto("NOT_FOUND", e.getMessage(), null));
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<User>> getUsersByStatus(@PathVariable AccountStatus status) {
        return ResponseEntity.ok(userService.findByStatus(status));
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable RoleType role) {
        return ResponseEntity.ok(userService.findByRole(role));
    }

    @GetMapping("/{userId}/locked")
    public ResponseEntity<?> isAccountLocked(@PathVariable UUID userId) {
        try {
            boolean locked = userService.isAccountLocked(userId);
            Map<String, String> details = new HashMap<>();
            details.put("locked", String.valueOf(locked));

            return ResponseEntity.ok(new ApiErrorResponseDto("SUCCESS",
                    locked ? "Account is locked" : "Account is not locked", details));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiErrorResponseDto("NOT_FOUND", e.getMessage(), null));
        }
    }

    // ==================== UPDATE ====================

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable UUID userId,
                                        @Valid @RequestBody UpdateUserRequestDto request) {
        try {
            // Implémentation à ajouter dans UserService si besoin
            return ResponseEntity.ok(new ApiErrorResponseDto("SUCCESS", "User updated", null));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorResponseDto("ERROR", e.getMessage(), null));
        }
    }

    @PutMapping("/{userId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable UUID userId,
                                          @RequestParam AccountStatus status) {
        try {
            userService.updateStatus(userId, status);
            return ResponseEntity.ok(new ApiErrorResponseDto("SUCCESS",
                    "Status updated to " + status, null));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiErrorResponseDto("NOT_FOUND", e.getMessage(), null));
        }
    }

    @PostMapping("/{userId}/lock")
    public ResponseEntity<?> lockAccount(@PathVariable UUID userId,
                                         @RequestParam(defaultValue = "30") int minutes) {
        try {
            userService.lockAccount(userId, minutes);
            return ResponseEntity.ok(new ApiErrorResponseDto("SUCCESS",
                    "Account locked for " + minutes + " minutes", null));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiErrorResponseDto("NOT_FOUND", e.getMessage(), null));
        }
    }

    @PostMapping("/{userId}/unlock")
    public ResponseEntity<?> unlockAccount(@PathVariable UUID userId) {
        try {
            userService.unlockAccount(userId);
            return ResponseEntity.ok(new ApiErrorResponseDto("SUCCESS",
                    "Account unlocked", null));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiErrorResponseDto("NOT_FOUND", e.getMessage(), null));
        }
    }

    // ==================== DELETE ====================

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(new ApiErrorResponseDto("SUCCESS",
                    "User deleted successfully", null));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiErrorResponseDto("NOT_FOUND", e.getMessage(), null));
        }
    }
}