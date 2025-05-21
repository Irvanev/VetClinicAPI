package dev.clinic.mainservice.mapping;

import dev.clinic.mainservice.dtos.auth.SignUpRequest;
import dev.clinic.mainservice.dtos.users.DoctorResponseForSelectInAppointment;
import dev.clinic.mainservice.dtos.users.EditClientRequest;
import dev.clinic.mainservice.dtos.users.UserProfileResponse;
import dev.clinic.mainservice.dtos.users.UserResponse;
import dev.clinic.mainservice.models.entities.*;

public class UserMapping {
    public static UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setPhoneNumber(user.getNumberPhone());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().getName().getName());

        return response;
    }

    public static Client fromRequest(EditClientRequest request) {
        if (request == null) {
            return null;
        }

        Client client = new Client();

        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());
        client.setNumberPhone(request.getNumberPhone());
        client.setDateOfBirth(request.getBirthDate());

        return client;
    }

    public static Client registerClient(SignUpRequest request) {
        if (request == null) {
            return null;
        }

        Client client = new Client();

        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());
        client.setNumberPhone(request.getNumber());
        client.setEmail(request.getEmail());

        return client;
    }

    public static UserProfileResponse toProfileResponse(User user) {
        if (user == null) {
            return null;
        }
        UserProfileResponse response = new UserProfileResponse();
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setNumberPhone(user.getNumberPhone());
        response.setBirthDate(user.getDateOfBirth());
        response.setPhoto(user.getPhotoUrl());

        return response;
    }

    public static void updateFromRequest(Client client, EditClientRequest request) {
        if (request.getFirstName() != null) {
            client.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            client.setLastName(request.getLastName());
        }
        if (request.getNumberPhone() != null) {
            client.setNumberPhone(request.getNumberPhone());
        }
        if (request.getBirthDate() != null) {
            client.setDateOfBirth(request.getBirthDate());
        }
    }

    public static DoctorResponseForSelectInAppointment toDoctorByBranchId(Doctor doctor) {
        if (doctor == null) {
            return null;
        }
        DoctorResponseForSelectInAppointment response = new DoctorResponseForSelectInAppointment();
        response.setId(doctor.getId());
        response.setFullName(doctor.getLastName() + " " + doctor.getFirstName() + " " + doctor.getPatronymic());

        return response;
    }
}
