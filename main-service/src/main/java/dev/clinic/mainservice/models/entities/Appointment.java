package dev.clinic.mainservice.models.entities;

import dev.clinic.mainservice.models.enums.AppointmentStatus;
import dev.clinic.mainservice.models.enums.AppointmentType;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Сущность {@code Appointment} представляет информацию об приемах.
 * <p>
 * Данный класс хранит общую информацию о приемах, такие как
 * врач, к которому был записан клиент, клиент, питомец, время проведения приема,
 * тип приема, статус, описание.
 * </p>
 *
 * @author Irvanev
 * @version 1.0
 */
@Entity
@Table(name = "appointments")
public class Appointment extends BaseEntity {

    /** Врач, который будет проводить прием */
    private Doctor doctor;

    /** Клиент, который записался на прием */
    private Client client;

    /** Питомец, который записан на прием */
    private Pet pet;

    /** Дата проведения приема */
    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    /** Время начала приема */
    @Column(name = "appointment_start_time", nullable = false)
    private LocalTime appointmentStartTime;

    /** Время окончания приема */
    @Column(name = "appointment_end_time", nullable = false)
    private LocalTime appointmentEndTime;

    /** Тип приема */
    private AppointmentType appointmentType;

    /** Статус приема */
    private AppointmentStatus status;

    /** Описание (доп. информация к приему) */
    private String comments;

    /**
     * Конструктор по умолчанию, необходимый для работы JPA.
     */
    public Appointment() {}

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    public Doctor getDoctor() {
        return doctor;
    }
    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    public Client getClient() {
        return client;
    }
    public void setClient(Client client) {
        this.client = client;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    public Pet getPet() {
        return pet;
    }
    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }
    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentStartTime() {
        return appointmentStartTime;
    }
    public void setAppointmentStartTime(LocalTime appointmentStartTime) {
        this.appointmentStartTime = appointmentStartTime;
    }

    public LocalTime getAppointmentEndTime() {
        return appointmentEndTime;
    }
    public void setAppointmentEndTime(LocalTime appointmentEndTime) {
        this.appointmentEndTime = appointmentEndTime;
    }

    @Enumerated(EnumType.STRING)
    public AppointmentType getAppointmentType() {
        return appointmentType;
    }
    public void setAppointmentType(AppointmentType appointmentType) {
        this.appointmentType = appointmentType;
    }

    @Enumerated(EnumType.STRING)
    public AppointmentStatus getStatus() {
        return status;
    }
    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }
}
