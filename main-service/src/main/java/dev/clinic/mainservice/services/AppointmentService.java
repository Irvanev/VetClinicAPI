package dev.clinic.mainservice.services;

import dev.clinic.mainservice.dtos.appointments.AppointmentAdminRequest;
import dev.clinic.mainservice.dtos.appointments.AppointmentRequest;
import dev.clinic.mainservice.dtos.appointments.AppointmentResponse;
import dev.clinic.mainservice.dtos.appointments.AppointmentResponseOwner;

import java.util.List;

public interface AppointmentService {
    AppointmentResponse createAppointment(AppointmentRequest appointmentRequest);
    AppointmentResponse createAppointmentAdmin(AppointmentAdminRequest appointmentAdminRequest);
    AppointmentResponse getAppointmentById(Long id);
    List<AppointmentResponseOwner> getAllOwnerAppointments();
    List<AppointmentResponseOwner> getAllAppointmentsByOwnerId(Long ownerId);
    List<AppointmentResponse> getAllAppointments();
}
