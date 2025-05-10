package dev.clinic.mainservice.mapping;

import dev.clinic.mainservice.dtos.appointments.AppointmentAdminRequest;
import dev.clinic.mainservice.dtos.appointments.AppointmentRequest;
import dev.clinic.mainservice.dtos.appointments.AppointmentResponse;
import dev.clinic.mainservice.dtos.appointments.AppointmentResponseOwner;
import dev.clinic.mainservice.models.entities.Appointment;
import dev.clinic.mainservice.models.entities.Client;
import dev.clinic.mainservice.models.entities.Doctor;
import dev.clinic.mainservice.models.entities.Pet;
import dev.clinic.mainservice.models.enums.AppointmentStatus;

import java.time.Duration;

public class AppointmentMapper {

    public static Appointment fromRequest(AppointmentRequest request, Client client, Pet pet, Doctor doctor) {
        if (request == null || client == null || pet == null || doctor == null) {
            return null;
        }

        Appointment appointment = new Appointment();

        Duration duration = request.getAppointmentType().getDuration();

        appointment.setDoctor(doctor);
        appointment.setPet(pet);
        appointment.setClient(client);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentStartTime(request.getAppointmentStartTime());
        appointment.setAppointmentEndTime(request.getAppointmentStartTime().plus(duration));
        appointment.setAppointmentType(request.getAppointmentType());
        appointment.setComments(request.getComments());
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        return appointment;
    }

    public static Appointment fromAdminRequest(AppointmentAdminRequest request, Client client, Pet pet, Doctor doctor) {
        if (request == null || client == null || pet == null || doctor == null) {
            return null;
        }

        Appointment appointment = new Appointment();

        Duration duration = request.getAppointmentType().getDuration();

        appointment.setDoctor(doctor);
        appointment.setPet(pet);
        appointment.setClient(client);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentStartTime(request.getAppointmentStartTime());
        appointment.setAppointmentEndTime(request.getAppointmentStartTime().plus(duration));
        appointment.setAppointmentType(request.getAppointmentType());
        appointment.setComments(request.getComments());

        return appointment;
    }

    public static AppointmentResponse toResponse(Appointment appointment) {
        if (appointment == null) {
            return null;
        }

        AppointmentResponse response = new AppointmentResponse();

        response.setId(appointment.getId());

        if (appointment.getDoctor() != null) {
            response.setDoctorId(appointment.getDoctor().getId());
        }

        if (appointment.getPet() != null) {
            response.setPetId(appointment.getPet().getId());
        }

        response.setAppointmentDate(appointment.getAppointmentDate());
        response.setAppointmentStartTime(appointment.getAppointmentStartTime());
        response.setAppointmentType(appointment.getAppointmentType().getName());
        response.setStatus(appointment.getStatus().getName());
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
            response.setDoctorName(appointment.getDoctor().getLastName() + " " + appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getPatronymic());
        }

        if (appointment.getPet() != null) {
            response.setPetId(appointment.getPet().getId());
            response.setPetName(appointment.getPet().getName());
        }

        response.setAppointmentDate(appointment.getAppointmentDate());
        response.setAppointmentStartTime(appointment.getAppointmentStartTime());
        response.setAppointmentType(appointment.getAppointmentType().getName());
        response.setStatus(appointment.getStatus().getName());
        response.setComments(appointment.getComments());
        response.setDoctorPhoto(appointment.getDoctor().getPhoto());
        response.setBranchName(appointment.getDoctor().getBranch().getShortName());
        return response;
    }
}
