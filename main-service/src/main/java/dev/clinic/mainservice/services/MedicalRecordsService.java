package dev.clinic.mainservice.services;

import dev.clinic.mainservice.dtos.medicalRecords.MedicalRecordRequest;
import dev.clinic.mainservice.dtos.medicalRecords.MedicalRecordResponse;

import java.util.List;
import java.util.Optional;

public interface MedicalRecordsService {
    void createMedicalRecord(Long appointmentId, MedicalRecordRequest request);
    MedicalRecordResponse getMedicalRecord(Long id);
    List<MedicalRecordResponse> getAllMedicalRecords();
    List<MedicalRecordResponse> getAllMedicalRecordByPet(Long petId);
    void updateMedicalRecord(Long id, MedicalRecordRequest request);
    boolean deleteMedicalRecord(Long id);
}
