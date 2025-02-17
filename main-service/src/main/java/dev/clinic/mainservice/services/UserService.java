package dev.clinic.mainservice.services;

import dev.clinic.mainservice.dtos.SignUpRequest;

public interface UserService {
    SignUpRequest signUp(SignUpRequest signUpRequest);
}
