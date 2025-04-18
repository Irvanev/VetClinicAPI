package dev.clinic.mainservice.services.impl;

import dev.clinic.mainservice.dtos.medicalRecords.MedicalRecordRequest;
import dev.clinic.mainservice.dtos.medicalRecords.MedicalRecordResponse;
import dev.clinic.mainservice.exceptions.ResourceNotFoundException;
import dev.clinic.mainservice.mapping.MedicalRecordsMapping;
import dev.clinic.mainservice.models.entities.Appointment;
import dev.clinic.mainservice.models.entities.MedicalRecords;
import dev.clinic.mainservice.models.entities.Pet;
import dev.clinic.mainservice.models.enums.AppointmentStatus;
import dev.clinic.mainservice.repositories.AppointmentRepository;
import dev.clinic.mainservice.repositories.MedicalRecordsRepository;
import dev.clinic.mainservice.repositories.PetRepository;
import dev.clinic.mainservice.services.MedicalRecordsService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MedicalRecordsServiceImpl implements MedicalRecordsService {

    private final MedicalRecordsRepository medicalRecordsRepository;
    private final AppointmentRepository appointmentRepository;

    @Autowired
    public MedicalRecordsServiceImpl(
            MedicalRecordsRepository medicalRecordsRepository,
            AppointmentRepository appointmentRepository) {
        this.medicalRecordsRepository = medicalRecordsRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public void createMedicalRecord(Long appointmentId, MedicalRecordRequest request) {
        if (appointmentId == null || appointmentId < 0) {
            throw new IllegalArgumentException("Invalid appointment id: " + appointmentId);
        }

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));

        try {
            MedicalRecords medicalRecords = MedicalRecordsMapping.fromRequest(request, appointment);
            medicalRecordsRepository.saveAndFlush(medicalRecords);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to create medical record", e);
        }
    }

    @Override
    public MedicalRecordResponse getMedicalRecord(Long id) {
        if (id == null || id < 0) {
            throw new IllegalArgumentException("Invalid medical record id: " + id);
        }

        return medicalRecordsRepository.findById(id)
                .map(MedicalRecordsMapping::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Medical record not found with id: " + id));
    }

    @Override
    public boolean deleteMedicalRecord(Long id) {
        if (id == null || id < 0) {
            throw new IllegalArgumentException("Invalid medical record id: " + id);
        }

        if (!medicalRecordsRepository.existsById(id)) {
            throw new ResourceNotFoundException("Medical record not found with id: " + id);
        }

        try {
            medicalRecordsRepository.deleteById(id);
            return true;
        } catch (DataAccessException e) {
            throw new ServiceException("Error deleting medical record with id: " + id, e);
        }
    }

    @Override
    public void updateMedicalRecord(Long id, MedicalRecordRequest request) {
//        if (id == null || id < 0) {
//            throw new IllegalArgumentException("Invalid medical record id: " + id);
//        }
//
//        MedicalRecords existingRecord = medicalRecordsRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Medical record not found with id: " + id));
//
//        try {
//            existingRecord.setDiagnosis(request.getDiagnosis());
//            existingRecord.setTreatment(request.getTreatment());
//            existingRecord.setNotes(request.getNotes());
//            existingRecord.setUpdatedAt(LocalDateTime.now());
//
//            medicalRecordsRepository.saveAndFlush(existingRecord);
//        } catch (DataAccessException e) {
//            throw new ServiceException("Failed to update medical record with id: " + id, e);
//        }
    }

    @Override
    public List<MedicalRecordResponse> getAllMedicalRecordByPet(Long petId) {
        if (petId == null || petId < 0) {
            throw new IllegalArgumentException("Invalid pet id: " + petId);
        }

        try {
            return medicalRecordsRepository.findAllByPetId(petId)
                    .stream()
                    .map(MedicalRecordsMapping::toResponse)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to retrieve medical records for pet with id: " + petId, e);
        }
    }

    @Override
    public List<MedicalRecordResponse> getAllMedicalRecords() {
        try {
            return medicalRecordsRepository.findAll()
                    .stream()
                    .map(MedicalRecordsMapping::toResponse)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to retrieve all medical records", e);
        }
    }
}
