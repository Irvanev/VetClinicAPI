package dev.clinic.mainservice.controllers;

import dev.clinic.mainservice.dtos.*;
import dev.clinic.mainservice.services.impl.AuthServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthServiceImpl authService;

    // Регистрация нового пользователя
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody SignUpRequest request) {
        authService.register(request);
        return ResponseEntity.ok("Registration initiated. Check your email for verification code.");
    }

    // Верификация email (2FA)
    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerifyRequest request) {
        authService.verify(request);
        return ResponseEntity.ok("User verified successfully.");
    }

    // Логин пользователя
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody SignInRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // Обновление access токена по refresh токену
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request) {
        AuthResponse response = authService.refresh(request);
        return ResponseEntity.ok(response);
    }
}
