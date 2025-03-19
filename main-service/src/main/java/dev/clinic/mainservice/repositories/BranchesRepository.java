package dev.clinic.mainservice.repositories;

import dev.clinic.mainservice.models.entities.Branches;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchesRepository extends JpaRepository<Branches, Long> {
}
