package dev.clinic.mainservice.mapping;

import dev.clinic.mainservice.dtos.appointments.AppointmentAdminRequest;
import dev.clinic.mainservice.dtos.appointments.AppointmentRequest;
import dev.clinic.mainservice.dtos.appointments.AppointmentResponse;
import dev.clinic.mainservice.dtos.appointments.AppointmentResponseOwner;
import dev.clinic.mainservice.models.entities.Appointment;
import dev.clinic.mainservice.models.entities.Client;
import dev.clinic.mainservice.models.entities.Doctor;
import dev.clinic.mainservice.models.entities.Pet;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AppointmentMapper {

    public static Appointment fromRequest(AppointmentRequest request) {
        if (request == null) {
            return null;
        }
        Appointment appointment = new Appointment();

        Doctor doctor = new Doctor();
        doctor.setId(request.getDoctorId());
        appointment.setDoctor(doctor);

        Pet pet = new Pet();
        pet.setId(request.getPetId());
        appointment.setPet(pet);

        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentType(request.getAppointmentType());
        appointment.setComments(request.getComments());

        return appointment;
    }

    public static Appointment fromAdminRequest(AppointmentAdminRequest request) {
        if (request == null) {
            return null;
        }
        Appointment appointment = new Appointment();

        Doctor doctor = new Doctor();
        doctor.setId(request.getDoctorId());
        appointment.setDoctor(doctor);

        Pet pet = new Pet();
        pet.setId(request.getPetId());
        appointment.setPet(pet);

        Client owner = new Client();
        owner.setId(request.getClintId());
        appointment.setClient(owner);

        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentType(request.getAppointmentType());
        appointment.setComments(request.getComments());

        return appointment;
    }

    public static AppointmentResponse toResponse(Appointment appointment) {
        if (appointment == null) {
            return null;
        }
        Doctor doctor = appointment.getDoctor();
        Pet pet = appointment.getPet();

        AppointmentResponse response = new AppointmentResponse();
        response.setId(appointment.getId());
        response.setDoctorId(doctor.getId());
        response.setPetId(pet.getId());
        response.setAppointmentDate(appointment.getAppointmentDate());
        response.setAppointmentType(appointment.getAppointmentType());
        response.setStatus(appointment.getStatus());
        response.setComments(appointment.getComments());

        return response;
    }

    public static AppointmentResponseOwner toResponseOwner(Appointment appointment) {
        if (appointment == null) {
            return null;
        }
        AppointmentResponseOwner response = new AppointmentResponseOwner();
        response.setId(appointment.getId());

        if (appointment.getDoctor() != null) {
            response.setDoctorId(appointment.getDoctor().getId());
            response.setDoctorName(appointment.getDoctor().getFullName()); // или getName(), если поле называется иначе
        }

        if (appointment.getPet() != null) {
            response.setPetId(appointment.getPet().getId());
            response.setPetName(appointment.getPet().getName()); // убедитесь, что у Pet есть поле name
        }

        response.setAppointmentDate(appointment.getAppointmentDate());
        response.setAppointmentType(appointment.getAppointmentType());
        response.setStatus(appointment.getStatus());
        response.setComments(appointment.getComments());

        return response;
    }

    // Преобразование списка Appointment в список AppointmentResponseOwner
    public static List<AppointmentResponseOwner> toResponseOwnerList(List<Appointment> appointments) {
        if (appointments == null) {
            return Collections.emptyList();
        }
        return appointments.stream()
                .map(AppointmentMapper::toResponseOwner)
                .collect(Collectors.toList());
    }

    public static List<AppointmentResponse> toResponseList(List<Appointment> appointments) {
        if (appointments == null) {
            return Collections.emptyList();
        }
        return appointments.stream()
                .map(AppointmentMapper::toResponse)
                .collect(Collectors.toList());
    }
}
