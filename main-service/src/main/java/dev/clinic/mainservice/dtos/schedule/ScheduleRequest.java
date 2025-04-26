package dev.clinic.mainservice.dtos.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "Запрос на создание расписания врача")
public class ScheduleRequest {
    private Long doctorId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    @Schema(description = "Уникальный идентификатор врача", example = "1")
    @NotNull(message = "Уникальный идентификатор врача не может быть пустым")
    @Positive(message = "Уникальный идентификатор врача должен быть положительным числом")
    public Long getDoctorId() {
        return doctorId;
    }
    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    @Schema(description = "Дата рабочего дня", example = "2023-10-01")
    @NotNull(message = "Дата рабочего дня не может быть пустой")
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Schema(description = "Время начала рабочего дня", example = "09:00")
    @NotNull(message = "Время начала рабочего дня не может быть пустым")
    public LocalTime getStartTime() {
        return startTime;
    }
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    @Schema(description = "Время окончания рабочего дня", example = "17:00")
    @NotNull(message = "Время окончания рабочего дня не может быть пустым")
    public LocalTime getEndTime() {
        return endTime;
    }
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
