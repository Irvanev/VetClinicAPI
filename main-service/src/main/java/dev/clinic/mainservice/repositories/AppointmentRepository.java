package dev.clinic.mainservice.repositories;

import dev.clinic.mainservice.models.entities.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findAllByClientEmail(String email);
    List<Appointment> findAllByClientId(Long id);
}
