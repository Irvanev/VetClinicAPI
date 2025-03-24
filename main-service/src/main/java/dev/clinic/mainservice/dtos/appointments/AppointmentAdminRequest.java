package dev.clinic.mainservice.dtos.appointments;

import dev.clinic.mainservice.models.enums.AppointmentType;

import java.time.LocalDateTime;

public class AppointmentAdminRequest {
    private Long doctorId;
    private Long petId;
    private Long clintId;
    private LocalDateTime appointmentDate;
    private AppointmentType appointmentType;
    private String comments;

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public Long getClintId() {
        return clintId;
    }

    public void setClintId(Long clintId) {
        this.clintId = clintId;
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public AppointmentType getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(AppointmentType appointmentType) {
        this.appointmentType = appointmentType;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
