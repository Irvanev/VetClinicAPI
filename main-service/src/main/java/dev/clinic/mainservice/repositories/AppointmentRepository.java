package dev.clinic.mainservice.repositories;

import dev.clinic.mainservice.models.entities.Appointment;
import dev.clinic.mainservice.models.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findAllByClientEmail(String email);
    List<Appointment> findAllByClientEmailAndStatusOrderByAppointmentDateDescAppointmentStartTimeDesc(String email, AppointmentStatus status);
    List<Appointment> findAllByClientIdOrderByAppointmentDateDesc(Long id);
    List<Appointment> findAllByPetIdOrderByAppointmentDateDesc(Long id);
    List<Appointment> findByDoctorIdAndAppointmentDateBetween(Long doctorId, LocalDate start, LocalDate end);
    List<Appointment> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDate date);

    @Query("""
      select a
      from Appointment a
      where a.status = :status
        and (
          a.appointmentDate < :today
          or (
            a.appointmentDate = :today
            and a.appointmentEndTime < :timeThreshold
          )
        )
      """)
    List<Appointment> findNoShowCandidates(
            @Param("status") AppointmentStatus status,
            @Param("today") LocalDate today,
            @Param("timeThreshold") LocalTime timeThreshold
    );
}
