package dev.clinic.mainservice.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public class SignInRequest {
    private String email;
    private String password;
    private String tokenDevice;

    public SignInRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public SignInRequest() {};

    @Schema(description = "Почта", example = "test@example.com")
    @NotEmpty(message = "Почта не может быть пустая")
    @Email
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    @Schema(description = "Пароль", example = "password")
    @NotEmpty(message = "Пароль не может быть пустым")
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    @Schema(description = "Токен устройсчтва для отправки Push-уведомлений")
    public String getTokenDevice() {
        return tokenDevice;
    }
    public void setTokenDevice(String tokenDevice) {
        this.tokenDevice = tokenDevice;
    }

    @Override
    public String toString() {
        return "SignInRequest{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
