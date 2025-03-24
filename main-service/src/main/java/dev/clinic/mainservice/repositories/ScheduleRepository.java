package dev.clinic.mainservice.repositories;

import dev.clinic.mainservice.models.entities.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Optional<Schedule> findByDoctorIdAndDate(Long doctorId, LocalDate date);
}
