package com.example.vetclinicapi.Services.Impl;

import com.example.vetclinicapi.Dtos.UserDto.AllUserDto;
import com.example.vetclinicapi.Dtos.UserDto.RegisterUserDto;
import com.example.vetclinicapi.Models.Entities.User;
import com.example.vetclinicapi.Models.Enums.RoleEnum;
import com.example.vetclinicapi.Repositories.RoleRepository;
import com.example.vetclinicapi.Repositories.UserRepository;
import com.example.vetclinicapi.Services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<AllUserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(users -> modelMapper.map(users, AllUserDto.class)).collect(Collectors.toList());
    }
}
