package dev.clinic.mainservice.repositories;

import dev.clinic.mainservice.models.entities.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Optional<Schedule> findByDoctorIdAndDate(Long doctorId, LocalDate date);
    List<Schedule> findAllByDoctorId(Long doctorId);
    List<Schedule> findAllByDoctorEmail(String email);
    List<Schedule> findAllByDoctorIdAndDate(Long doctorId, LocalDate date);
    List<Schedule> findAllByDoctorEmailAndDate(String email, LocalDate date);
}
