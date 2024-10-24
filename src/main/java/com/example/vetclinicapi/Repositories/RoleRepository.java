package com.example.vetclinicapi.Repositories;

import com.example.vetclinicapi.Models.Entities.Role;
import com.example.vetclinicapi.Models.Enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(RoleEnum name);
}
