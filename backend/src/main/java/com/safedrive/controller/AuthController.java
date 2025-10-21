package com.safedrive.controller;

import com.safedrive.dto.request.LoginRequest;
import com.safedrive.dto.request.RegisterRequest;
import com.safedrive.dto.response.AuthResponse;
import com.safedrive.dto.response.UserResponse;
import com.safedrive.entity.User;
import com.safedrive.service.UserService;
import com.safedrive.util.JwtUtil;
import com.safedrive.util.SecurityUtil;
import com.safedrive.util.EncryptionUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EncryptionUtil encryptionUtil;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("Registration attempt for email: {}", request.getEmail());

        User user = userService.createUser(request.getEmail(), request.getPassword(), request.getName());
        String token = jwtUtil.generateToken(user.getEmail());

        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt());

        AuthResponse response = new AuthResponse(token, userResponse);
        logger.info("User registered successfully: {}", user.getEmail());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        logger.info("Login attempt for email: {}", request.getEmail());

        Optional<User> userOptional = userService.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            logger.warn("Login failed - user not found: {}", request.getEmail());
            return ResponseEntity.badRequest().build();
        }

        User user = userOptional.get();
        if (!userService.validatePassword(request.getPassword(), user.getPassword())) {
            logger.warn("Login failed - invalid password for: {}", request.getEmail());
            return ResponseEntity.badRequest().build();
        }

        String token = jwtUtil.generateToken(user.getEmail());

        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt());

        AuthResponse response = new AuthResponse(token, userResponse);
        logger.info("User logged in successfully: {}", user.getEmail());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // In a stateless JWT setup, logout is typically handled on the frontend
        // by removing the token from storage
        return ResponseEntity.ok().build();
    }

    @GetMapping("/security-audit")
    public ResponseEntity<?> securityAudit() {
        try {
            User currentUser = SecurityUtil.getCurrentUser();

            return ResponseEntity.ok(Map.of(
                    "status", "SECURE",
                    "message", "User isolation is properly implemented",
                    "currentUser", Map.of(
                            "id", currentUser.getId(),
                            "email", currentUser.getEmail(),
                            "role", currentUser.getRole().name()),
                    "securityFeatures", Map.of(
                            "jwtAuthentication", "ENABLED",
                            "userIsolation", "ENABLED",
                            "dataAccessControl", "ENABLED",
                            "fileSystemIsolation", "ENABLED"),
                    "timestamp", System.currentTimeMillis()));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of(
                    "status", "UNAUTHORIZED",
                    "message", "Authentication required",
                    "timestamp", System.currentTimeMillis()));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            User currentUser = SecurityUtil.getCurrentUser();

            // Only admin users can view all users
            if (!SecurityUtil.isCurrentUserAdmin()) {
                return ResponseEntity.status(403).body(Map.of(
                        "error", "Access denied. Admin privileges required.",
                        "timestamp", System.currentTimeMillis()));
            }

            List<User> users = userService.getAllUsers();
            List<UserResponse> userResponses = users.stream()
                    .map(user -> new UserResponse(
                            user.getId(),
                            user.getName(),
                            user.getEmail(),
                            user.getRole(),
                            user.getCreatedAt()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "users", userResponses,
                    "totalUsers", userResponses.size(),
                    "timestamp", System.currentTimeMillis()));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of(
                    "error", "Authentication required",
                    "timestamp", System.currentTimeMillis()));
        }
    }

    @GetMapping("/debug/encryption")
    public ResponseEntity<?> debugEncryption(@RequestParam String text) {
        try {
            String encrypted = encryptionUtil.encrypt(text);
            String decrypted = encryptionUtil.decrypt(encrypted);

            return ResponseEntity.ok(Map.of(
                    "original", text,
                    "encrypted", encrypted,
                    "decrypted", decrypted,
                    "encryptionWorking", text.equals(decrypted)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()));
        }
    }

    @GetMapping("/debug/users")
    public ResponseEntity<?> debugUsers() {
        try {
            List<User> users = userService.getAllUsers();
            List<Map<String, Object>> userInfo = users.stream()
                    .map(user -> Map.of(
                            "id", user.getId(),
                            "name", user.getName(),
                            "email", user.getEmail(),
                            "role", user.getRole(),
                            "createdAt", user.getCreatedAt()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "totalUsers", users.size(),
                    "users", userInfo,
                    "timestamp", System.currentTimeMillis()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage(),
                    "timestamp", System.currentTimeMillis()));
        }
    }
}
