package dev.clinic.mainservice.repositories;

import dev.clinic.mainservice.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPetsId(Long petId);
    boolean existsByEmail(String email);
}
