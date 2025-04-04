package dev.clinic.mainservice.repositories;

import dev.clinic.mainservice.models.entities.MedicalRecords;
import dev.clinic.mainservice.models.entities.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalRecordsRepository extends JpaRepository<MedicalRecords, Long> {
    MedicalRecords findByPet(Pet pet);
    List<MedicalRecords> findAllByPetId(Long petId);
}
