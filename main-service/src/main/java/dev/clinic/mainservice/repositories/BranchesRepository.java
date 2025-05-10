package dev.clinic.mainservice.repositories;

import dev.clinic.mainservice.models.entities.Branches;
import dev.clinic.mainservice.models.enums.AppointmentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BranchesRepository extends JpaRepository<Branches, Long> {
    List<Branches> getAllBranchesByDoctorsId(Long doctorId);
    List<Branches> getAllBranchesByServicesContains(AppointmentType service);
    Optional<Branches> findByEmail(String email);
}
