package dev.clinic.mainservice.dtos.schedule;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "Поучение расписания врача")
public class ScheduleResponse {
    private Long id;
    private Long doctorId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    @Schema(description = "Уникальный идентификатор расписания", example = "1")
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Schema(description = "Уникальный идентификатор врача", example = "1")
    public Long getDoctorId() {
        return doctorId;
    }
    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    @Schema(description = "Дата рабочего дня", example = "2023-10-01")
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Schema(description = "Время начала рабочего дня", example = "09:00")
    public LocalTime getStartTime() {
        return startTime;
    }
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    @Schema(description = "Время окончания рабочего дня", example = "17:00")
    public LocalTime getEndTime() {
        return endTime;
    }
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
