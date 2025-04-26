package dev.clinic.mainservice.dtos.appointments;

import dev.clinic.mainservice.models.enums.AppointmentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Schema(description = "Запрос на создание приема")
public class AppointmentRequest {
    private Long doctorId;
    private Long petId;
    private LocalDate appointmentDate;
    private LocalTime appointmentStartTime;
    private AppointmentType appointmentType;
    private String comments;

    @Schema(description = "Уникальный идентификатор врача", example = "1")
    @NotNull(message = "Идентификатор врача не может быть пустым")
    @Positive(message = "Идентификатор врача должен быть положительным числом")
    public Long getDoctorId() {
        return doctorId;
    }
    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    @Schema(description = "Уникальный идентификатор питомца", example = "1")
    @NotNull(message = "Идентификатор питомца не может быть пустым")
    @Positive(message = "Идентификатор питомца должен быть положительным числом")
    public Long getPetId() {
        return petId;
    }
    public void setPetId(Long petId) {
        this.petId = petId;
    }

    @Schema(description = "Дата приема", example = "2023-10-01")
    @NotNull(message = "Дата приема не может быть пустой")
    @FutureOrPresent(message = "Дата приема должна быть сегодняшней или будущей")
    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }
    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    @Schema(description = "Время начала приема", example = "10:00")
    @NotNull(message = "Время начала приема не может быть пустым")
    public LocalTime getAppointmentStartTime() {
        return appointmentStartTime;
    }
    public void setAppointmentStartTime(LocalTime appointmentStartTime) {
        this.appointmentStartTime = appointmentStartTime;
    }

    @Schema(description = "Тип приема", example = "CONSULTATION")
    @NotEmpty(message = "Тип приема не может быть пустым")
    public AppointmentType getAppointmentType() {
        return appointmentType;
    }
    public void setAppointmentType(AppointmentType appointmentType) {
        this.appointmentType = appointmentType;
    }

    @Schema(description = "Комментарии к приему", example = "Плановый осмотр")
    @Size(max = 500, message = "Комментарий не должен превышать 500 символов")
    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }
}
