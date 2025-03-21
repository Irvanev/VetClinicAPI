package dev.clinic.mainservice.models.entities;

import dev.clinic.mainservice.models.enums.AppointmentStatus;
import dev.clinic.mainservice.models.enums.AppointmentType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

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

    /** Время проведения приема */
    private LocalDateTime appointmentDate;

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
