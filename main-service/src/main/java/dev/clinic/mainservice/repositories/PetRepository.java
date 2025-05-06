package dev.clinic.mainservice.repositories;

import dev.clinic.mainservice.models.entities.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findAllByOwnerEmail(String ownerEmail);
    Optional<Pet> findByName(String name);
}
