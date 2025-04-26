package dev.clinic.mainservice.dtos.medicalRecords;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Получение медицинской записи")
public class MedicalRecordResponse {
    private Long id;
    private Long doctorId;
    private Long clientId;
    private Long petId;
    private Long appointmentId;
    private String diagnosis;
    private String treatment;
    private String notes;

    @Schema(description = "Уникальный идентификатор записи", example = "1")
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

    @Schema(description = "Уникальный идентификатор клиента", example = "1")
    public Long getClientId() {
        return clientId;
    }
    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    @Schema(description = "Уникальный идентификатор питомца", example = "1")
    public Long getPetId() {
        return petId;
    }
    public void setPetId(Long petId) {
        this.petId = petId;
    }

    @Schema(description = "Уникальный идентификатор приема", example = "1")
    public Long getAppointmentId() {
        return appointmentId;
    }
    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    @Schema(description = "Диагноз", example = "Почечная недостаточность")
    public String getDiagnosis() {
        return diagnosis;
    }
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    @Schema(description = "Лечение", example = "Каждое утро принимать таблетки")
    public String getTreatment() {
        return treatment;
    }
    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    @Schema(description = "Прочее описание", example = "Явится на повторный прием через 2 недели")
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
