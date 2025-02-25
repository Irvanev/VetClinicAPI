package dev.clinic.mainservice.services.impl;

import dev.clinic.mainservice.dtos.users.UserResponse;
import dev.clinic.mainservice.exceptions.ResourceNotFoundException;
import dev.clinic.mainservice.models.entities.User;
import dev.clinic.mainservice.repositories.PetRepository;
import dev.clinic.mainservice.repositories.UserRepository;
import dev.clinic.mainservice.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PetRepository petRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.petRepository = petRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> modelMapper.map(user, UserResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public UserResponse getUserByPetId(Long petId) {
        User user = userRepository.findByPetsId(petId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with pet id: " + petId));
        return modelMapper.map(user, UserResponse.class);
    }
}
