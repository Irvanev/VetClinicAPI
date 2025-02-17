package dev.clinic.mainservice.repositories;

import dev.clinic.mainservice.models.entities.Role;
import dev.clinic.mainservice.models.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleEnum name);
}
