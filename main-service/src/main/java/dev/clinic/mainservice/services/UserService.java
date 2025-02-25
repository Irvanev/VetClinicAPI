package dev.clinic.mainservice.services;

import dev.clinic.mainservice.dtos.users.UserResponse;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserResponse> getAllUsers();
    UserResponse getUserByEmail(String email);
    UserResponse getUserById(Long id);
    UserResponse getUserByPetId(Long petId);
}
