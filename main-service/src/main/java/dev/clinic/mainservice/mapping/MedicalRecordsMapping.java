package dev.clinic.mainservice.mapping;

import dev.clinic.mainservice.dtos.medicalRecords.MedicalRecordRequest;
import dev.clinic.mainservice.dtos.medicalRecords.MedicalRecordResponse;
import dev.clinic.mainservice.models.entities.*;

import java.time.LocalDateTime;

public class MedicalRecordsMapping {
    public static MedicalRecordResponse toResponse(MedicalRecords medicalRecord) {
        if (medicalRecord == null) {
            return null;
        }

        MedicalRecordResponse response = new MedicalRecordResponse();

        response.setId(medicalRecord.getId());
        response.setDate(medicalRecord.getAppointment().getAppointmentDate());
        response.setTime(medicalRecord.getAppointment().getAppointmentStartTime());
        response.setBranchShortName(medicalRecord.getDoctor().getBranch().getShortName());
        response.setPhotoDoctor(medicalRecord.getDoctor().getPhoto());
        response.setDoctorName(medicalRecord.getDoctor().getLastName() + " " + medicalRecord.getDoctor().getFirstName() + " " + medicalRecord.getDoctor().getPatronymic());
        response.setType(medicalRecord.getAppointment().getAppointmentType());
        response.setDiagnosis(medicalRecord.getDiagnosis());
        response.setTreatment(medicalRecord.getTreatment());
        response.setNotes(medicalRecord.getNotes());

        return response;
    }

    public static MedicalRecords fromRequest(MedicalRecordRequest request, Appointment appointment) {
        if (request == null || appointment == null) {
            return null;
        }

        MedicalRecords medicalRecords = new MedicalRecords();

        medicalRecords.setDiagnosis(request.getDiagnosis());
        medicalRecords.setTreatment(request.getTreatment());
        medicalRecords.setNotes(request.getNotes());
        medicalRecords.setRecordDate(LocalDateTime.now());

        medicalRecords.setClient(appointment.getClient());
        medicalRecords.setAppointment(appointment);
        medicalRecords.setDoctor(appointment.getDoctor());
        medicalRecords.setPet(appointment.getPet());

        return medicalRecords;
    }
}
