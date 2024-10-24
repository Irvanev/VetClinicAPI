package com.example.vetclinicapi.Services;

import com.example.vetclinicapi.Dtos.UserDto.AllUserDto;
import com.example.vetclinicapi.Dtos.UserDto.RegisterUserDto;

import java.util.List;

public interface UserService {
    List<AllUserDto> getAllUsers();
}
