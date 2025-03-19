package dev.clinic.mainservice.repositories;

import dev.clinic.mainservice.models.entities.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    boolean existsByEmail(String email);
    List<Doctor> findAllByBranchId(Long branchId);
}
