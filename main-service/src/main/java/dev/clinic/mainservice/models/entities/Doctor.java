package dev.clinic.mainservice.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "doctors")
public class Doctor extends User {
    private String specialization;
    private String cabinente;

    protected Doctor() {}

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getCabinente() {
        return cabinente;
    }

    public void setCabinente(String cabinente) {
        this.cabinente = cabinente;
    }
}
