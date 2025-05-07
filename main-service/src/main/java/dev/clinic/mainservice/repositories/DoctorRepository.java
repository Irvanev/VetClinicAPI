package dev.clinic.mainservice.repositories;

import dev.clinic.mainservice.models.entities.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    boolean existsByEmail(String email);
    List<Doctor> findAllByBranchId(Long branchId);
    Optional<Doctor> findByEmail(String email);
}
