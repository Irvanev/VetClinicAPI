package dev.clinic.mainservice.services.impl;

import dev.clinic.mainservice.dtos.medicalRecords.MedicalRecordRequest;
import dev.clinic.mainservice.dtos.medicalRecords.MedicalRecordResponse;
import dev.clinic.mainservice.exceptions.ResourceNotFoundException;
import dev.clinic.mainservice.mapping.MedicalRecordsMapping;
import dev.clinic.mainservice.models.entities.Appointment;
import dev.clinic.mainservice.models.entities.MedicalRecords;
import dev.clinic.mainservice.repositories.AppointmentRepository;
import dev.clinic.mainservice.repositories.MedicalRecordsRepository;
import dev.clinic.mainservice.services.MedicalRecordsService;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicalRecordsServiceImpl implements MedicalRecordsService {

    private final MedicalRecordsRepository medicalRecordsRepository;
    private final AppointmentRepository appointmentRepository;

    private static final Logger log = LoggerFactory.getLogger(MedicalRecordsServiceImpl.class);

    public MedicalRecordsServiceImpl(
            MedicalRecordsRepository medicalRecordsRepository,
            AppointmentRepository appointmentRepository
    ) {
        this.medicalRecordsRepository = medicalRecordsRepository;
        this.appointmentRepository = appointmentRepository;
    }

    /**
     * Создание медецинской записи после прохождения приема
     * @param appointmentId уникальный идентификатор приема
     */
    @Override
    @CacheEvict(value = "medicalRecords", allEntries = true)
    public void createMedicalRecord(Long appointmentId, MedicalRecordRequest request) {
        log.info("Create medical record was called by appointmentId: {}", appointmentId);
        if (appointmentId == null || appointmentId < 0) {
            log.error("Invalid appointmentId: {}", appointmentId);
            throw new IllegalArgumentException("Invalid appointment id: " + appointmentId);
        }
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> {
                    log.warn("Appointment not found, id={}", appointmentId);
                    return new ResourceNotFoundException("Appointment not found with id: " + appointmentId);
                });
        try {
            MedicalRecords medicalRecords = MedicalRecordsMapping.fromRequest(request, appointment);
            medicalRecordsRepository.saveAndFlush(medicalRecords);
            log.info("Medical record was created with id: {}", medicalRecords.getId());
        } catch (DataAccessException ex) {
            log.error("Database error in createMedicalRecord for appointmentId={}", appointmentId, ex);
            throw new ServiceException("Failed to create medical record", ex);
        } catch (Exception ex) {
            log.error("Unexpected error in createMedicalRecord for appointmentId={}", appointmentId, ex);
            throw new ServiceException("Unexpected error while creating medical record", ex);
        }
    }

    /**
     * Получение медицинской записи
     * @param id уникальный идентификатор записи
     * @return Возращает полученную запись
     */
    @Override
    public MedicalRecordResponse getMedicalRecord(Long id) {
        log.info("Get Medical record with id: {} was called", id);
        if (id == null || id < 0) {
            log.error("Invalid medical record id: {}", id);
            throw new IllegalArgumentException("Invalid medical record id: " + id);
        }
        try {
            MedicalRecordResponse resp = medicalRecordsRepository.findById(id)
                    .map(MedicalRecordsMapping::toResponse)
                    .orElseThrow(() -> {
                        log.warn("Medical record not found, id={}", id);
                        return new ResourceNotFoundException("Medical record not found with id: " + id);
                    });
            log.info("Medical record fetched with id: {}", id);
            return resp;
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (DataAccessException ex) {
            log.error("Database error in getMedicalRecord for id={}", id, ex);
            throw new ServiceException("Failed to retrieve medical record", ex);
        } catch (Exception ex) {
            log.error("Unexpected error in getMedicalRecord for id={}", id, ex);
            throw new ServiceException("Unexpected error while retrieving medical record", ex);
        }
    }

    /**
     * Удаление медицинской записи
     * @param id уникальный идентификатор записи
     */
    @Override
    @CacheEvict(value = "medicalRecords", allEntries = true)
    public void deleteMedicalRecord(Long id) {
        log.info("Delete medical record with id: {} was called", id);
        if (id == null || id < 0) {
            log.error("Invalid medical record id: {}", id);
            throw new IllegalArgumentException("Invalid medical record id: " + id);
        }
        if (!medicalRecordsRepository.existsById(id)) {
            log.warn("Medical record not found, id={}", id);
            throw new ResourceNotFoundException("Medical record not found with id: " + id);
        }
        try {
            medicalRecordsRepository.deleteById(id);
            log.info("Medical record was deleted with id: {}", id);
        } catch (DataAccessException ex) {
            log.error("Database error in deleteMedicalRecord for id={}", id, ex);
            throw new ServiceException("Error deleting medical record with id: " + id, ex);
        } catch (Exception ex) {
            log.error("Unexpected error in deleteMedicalRecord for id={}", id, ex);
            throw new ServiceException("Unexpected error while deleting medical record", ex);
        }
    }

    /**
     * Получение всех медициснких записей для определенного питомца
     * @param petId уникальный идентификатор питомца
     * @return Возращает полученные записи
     */
    @Override
    @Cacheable(value = "medicalRecords", key = "#petId")
    public List<MedicalRecordResponse> getAllMedicalRecordByPet(Long petId) {
        log.info("Get medical records by pet with id: {}", petId);
        if (petId == null || petId < 0) {
            log.error("Invalid pet id: {}", petId);
            throw new IllegalArgumentException("Invalid pet id: " + petId);
        }
        try {
            List<MedicalRecordResponse> list = medicalRecordsRepository.findAllByPetId(petId).stream()
                    .map(MedicalRecordsMapping::toResponse)
                    .collect(Collectors.toList());
            log.info("END getAllMedicalRecordByPet: found {} records for petId={}", list.size(), petId);
            return list;
        } catch (DataAccessException ex) {
            log.error("Database error in getAllMedicalRecordByPet for petId={}", petId, ex);
            throw new ServiceException("Failed to retrieve medical records for pet with id: " + petId, ex);
        } catch (Exception ex) {
            log.error("Unexpected error in getAllMedicalRecordByPet for petId={}", petId, ex);
            throw new ServiceException("Unexpected error while fetching medical records for pet", ex);
        }
    }

    /**
     * Получени всех медецинских записей
     * @return Возращает спсиок всех записей
     */
    @Override
    @Cacheable(value = "medicalRecords", key = "'all'")
    public List<MedicalRecordResponse> getAllMedicalRecords() {
        log.info("Get all medical records was called");
        try {
            List<MedicalRecordResponse> list = medicalRecordsRepository.findAll().stream()
                    .map(MedicalRecordsMapping::toResponse)
                    .collect(Collectors.toList());
            log.info("Medical records fetching by size: {}", list.size());
            return list;
        } catch (DataAccessException ex) {
            log.error("Database error in getAllMedicalRecords", ex);
            throw new ServiceException("Failed to retrieve all medical records", ex);
        } catch (Exception ex) {
            log.error("Unexpected error in getAllMedicalRecords", ex);
            throw new ServiceException("Unexpected error while fetching all medical records", ex);
        }
    }
}
