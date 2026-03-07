package com.campus.complaint.controller;

import com.campus.complaint.dto.AuthResponse;
import com.campus.complaint.dto.LoginRequest;
import com.campus.complaint.dto.SignupRequest;
import com.campus.complaint.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        try {
            AuthResponse response = authService.signup(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        try {
            authService.verifyEmail(token);
            return ResponseEntity.ok("Email verified successfully! You can now log in.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody java.util.Map<String, String> request,
                                            jakarta.servlet.http.HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401).body(java.util.Map.of("error", "Not authenticated"));
            }
            String currentPassword = request.get("currentPassword");
            String newPassword = request.get("newPassword");
            if (newPassword == null || newPassword.length() < 6) {
                return ResponseEntity.badRequest().body(java.util.Map.of("error", "New password must be at least 6 characters"));
            }
            authService.changePassword(userId, currentPassword, newPassword);
            return ResponseEntity.ok(java.util.Map.of("message", "Password changed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }
}
