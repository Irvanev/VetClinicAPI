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
        response.setDiagnosis(medicalRecord.getDiagnosis());
        response.setTreatment(medicalRecord.getTreatment());
        response.setNotes(medicalRecord.getNotes());

        if (medicalRecord.getDoctor() != null) {
            response.setDoctorId(medicalRecord.getDoctor().getId());
        }
        if (medicalRecord.getPet() != null) {
            response.setPetId(medicalRecord.getPet().getId());
        }
        if (medicalRecord.getClient() != null) {
            response.setClientId(medicalRecord.getClient().getId());
        }
        if (medicalRecord.getAppointment() != null) {
            response.setAppointmentId(medicalRecord.getAppointment().getId());
        }

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
