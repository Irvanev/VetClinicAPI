package dev.clinic.mainservice.dtos.medicalRecords;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "Запрос на создание медицинской карты")
public class MedicalRecordRequest {
    private String diagnosis;
    private String treatment;
    private String notes;

    @Schema(description = "Диагноз", example = "Почечная недостаточность")
    @NotEmpty(message = "Диагноз не может быть пустым")
    public String getDiagnosis() {
        return diagnosis;
    }
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    @Schema(description = "Лечение", example = "Каждое утро принимать таблетки")
    @NotEmpty(message = "Лечение не может быть пустым")
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
