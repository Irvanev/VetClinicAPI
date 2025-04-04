package dev.clinic.mainservice.mapping;

import dev.clinic.mainservice.dtos.users.EditClientRequest;
import dev.clinic.mainservice.dtos.users.UserResponse;
import dev.clinic.mainservice.models.entities.*;

public class UsersMapper {
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
}
