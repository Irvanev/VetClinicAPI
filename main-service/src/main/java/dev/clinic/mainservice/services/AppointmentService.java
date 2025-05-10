package dev.clinic.mainservice.services;

import dev.clinic.mainservice.dtos.appointments.AppointmentAdminRequest;
import dev.clinic.mainservice.dtos.appointments.AppointmentRequest;
import dev.clinic.mainservice.dtos.appointments.AppointmentResponse;
import dev.clinic.mainservice.dtos.appointments.AppointmentResponseOwner;
import dev.clinic.mainservice.models.enums.AppointmentStatus;
import dev.clinic.mainservice.models.enums.AppointmentType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentService {
    void createAppointment(AppointmentRequest appointmentRequest);
    void cancelAppointment(Long id);
    AppointmentResponse getAppointmentById(Long id);
    List<AppointmentResponseOwner> getAllOwnerAppointments();
    public List<AppointmentResponseOwner> getAllOwnerAppointmentsByStatus(AppointmentStatus status);
    List<AppointmentResponseOwner> getAllOwnerAppointmentsByPetId(Long petId);
    List<AppointmentResponseOwner> getAllAppointmentsByOwnerId(Long ownerId);
    List<AppointmentResponse> getAllAppointments();
    List<LocalTime> getAvailableTimeSlots(Long doctorId, LocalDate date, AppointmentType type);
    void updateNoShowAppointments();

    void createAppointmentAdmin(AppointmentAdminRequest appointmentAdminRequest);
}
