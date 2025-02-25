package dev.clinic.mainservice.services;

import dev.clinic.mainservice.dtos.auth.*;

public interface AuthService {
    void register(SignUpRequest signUpRequest);
    void verify(VerifyRequest verifyRequest);
    AuthResponse login(SignInRequest signInRequest);
    AuthResponse refresh(RefreshRequest refreshRequest);
}
