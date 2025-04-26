package dev.clinic.mainservice.dtos.appointments;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "Получение приема для владельца")
public class AppointmentResponseOwner {
    private Long id;
    private Long doctorId;
    private String doctorName;
    private Long petId;
    private String petName;
    private LocalDate appointmentDate;
    private LocalTime appointmentStartTime;
    private String appointmentType;
    private String status;
    private String comments;

    @Schema(description = "Уникальный идентификатор приема", example = "1")
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

    @Schema(description = "ФИО врача", example = "Иванов Иван Иванович")
    public String getDoctorName() {
        return doctorName;
    }
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    @Schema(description = "Уникальный идентификатор питомца", example = "1")
    public Long getPetId() {
        return petId;
    }
    public void setPetId(Long petId) {
        this.petId = petId;
    }

    @Schema(description = "Имя питомца", example = "Арнольд")
    public String getPetName() {
        return petName;
    }
    public void setPetName(String petName) {
        this.petName = petName;
    }

    @Schema(description = "Дата приема", example = "2023-10-01")
    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }
    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    @Schema(description = "Время начала приема", example = "10:00")
    public LocalTime getAppointmentStartTime() {
        return appointmentStartTime;
    }
    public void setAppointmentStartTime(LocalTime appointmentStartTime) {
        this.appointmentStartTime = appointmentStartTime;
    }

    @Schema(description = "Тип приема", example = "Консультация")
    public String getAppointmentType() {
        return appointmentType;
    }
    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }

    @Schema(description = "Статус приема", example = "Запланирован")
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    @Schema(description = "Комментарии к приему", example = "Плановый осмотр")
    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }
}
