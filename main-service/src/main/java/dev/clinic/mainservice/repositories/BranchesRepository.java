package dev.clinic.mainservice.repositories;

import dev.clinic.mainservice.models.entities.Branches;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BranchesRepository extends JpaRepository<Branches, Long> {
    List<Branches> getAllBranchesByDoctorsId(Long doctorId);
}
