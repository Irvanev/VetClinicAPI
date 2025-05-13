package dev.clinic.mainservice.dtos.medicalRecords;

import dev.clinic.mainservice.models.enums.AppointmentType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "Получение медицинской записи")
public class MedicalRecordResponse {
    private Long id;
    private String doctorName;
    private String photoDoctor;
    private String branchShortName;
    private LocalDate date;
    private LocalTime time;
    private AppointmentType type;
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

    @Schema(description = "ФИО доктора", example = "Иванов Иван Иванович")
    public String getDoctorName() {
        return doctorName;
    }
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    @Schema(description = "Ссылка на фотографию доктора", example = "http://localhost:9000/images/doctor_photo.jpg")
    public String getPhotoDoctor() {
        return photoDoctor;
    }
    public void setPhotoDoctor(String photoDoctor) {
        this.photoDoctor = photoDoctor;
    }

    @Schema(description = "Название поликлиника (укороченное)", example = "Поликлина Лапа №5")
    public String getBranchShortName() {
        return branchShortName;
    }
    public void setBranchShortName(String branchShortName) {
        this.branchShortName = branchShortName;
    }

    @Schema(description = "Дата проведенеия приема", example = "2025-05-10")
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Schema(description = "Время проведения приема", example = "13:30")
    public LocalTime getTime() {
        return time;
    }
    public void setTime(LocalTime time) {
        this.time = time;
    }

    @Schema(description = "Тип услуги", example = "Чипирование")
    public AppointmentType getType() {
        return type;
    }
    public void setType(AppointmentType type) {
        this.type = type;
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
