package dev.clinic.mainservice.repositories;

import dev.clinic.mainservice.models.entities.Appointment;
import dev.clinic.mainservice.models.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findAllByClientEmail(String email);
    List<Appointment> findAllByClientEmailAndStatusOrderByAppointmentDateDescAppointmentStartTimeDesc(String email, AppointmentStatus status);
    List<Appointment> findAllByClientIdOrderByAppointmentDateDesc(Long id);
    List<Appointment> findAllByPetIdOrderByAppointmentDateDesc(Long id);
    List<Appointment> findByDoctorIdAndAppointmentDateBetween(Long doctorId, LocalDate start, LocalDate end);
    List<Appointment> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDate date);
    List<Appointment> findByStatusAndAppointmentEndTimeBefore(AppointmentStatus status, LocalTime time);
}
