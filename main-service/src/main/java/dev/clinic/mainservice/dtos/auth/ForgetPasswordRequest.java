package dev.clinic.mainservice.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public class ForgetPasswordRequest {
    private String email;

    @Schema(description = "Почта", example = "test@example.com")
    @NotEmpty(message = "Почта не может быть пустая")
    @Email
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
