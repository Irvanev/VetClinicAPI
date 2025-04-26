package dev.clinic.mainservice.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;
    private String repeatedNewPassword;

    @Schema(description = "Текущий пароль", example = "password")
    @NotEmpty(message = "Пароль не может быть пустым")
    public String getOldPassword() {
        return oldPassword;
    }
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    @Schema(description = "Новый пароль", example = "password")
    @NotEmpty(message = "Пароль не может быть пустым")
    public String getNewPassword() {
        return newPassword;
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Schema(description = "Новый пароль снова", example = "password")
    @NotEmpty(message = "Пароль не может быть пустым")
    public String getRepeatedNewPassword() {
        return repeatedNewPassword;
    }
    public void setRepeatedNewPassword(String repeatedNewPassword) {
        this.repeatedNewPassword = repeatedNewPassword;
    }
}
