package dev.clinic.mainservice.services.impl;

import dev.clinic.mainservice.dtos.schedule.ScheduleRequest;
import dev.clinic.mainservice.dtos.schedule.ScheduleResponse;
import dev.clinic.mainservice.exceptions.ResourceNotFoundException;
import dev.clinic.mainservice.mapping.ScheduleMapper;
import dev.clinic.mainservice.models.entities.Doctor;
import dev.clinic.mainservice.models.entities.Schedule;
import dev.clinic.mainservice.repositories.DoctorRepository;
import dev.clinic.mainservice.repositories.ScheduleRepository;
import dev.clinic.mainservice.services.ScheduleService;
import dev.clinic.mainservice.utils.AuthUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;
    private final AuthUtil authUtil;
    private static final Logger log = LoggerFactory.getLogger(ScheduleServiceImpl.class);

    @Autowired
    public ScheduleServiceImpl(
            ScheduleRepository scheduleRepository,
            DoctorRepository doctorRepository,
            AuthUtil authUtil
    ) {
        this.scheduleRepository = scheduleRepository;
        this.doctorRepository = doctorRepository;
        this.authUtil = authUtil;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponse> getAllSchedules() {
        log.info("Fetching all schedules");
        List<Schedule> schedules = scheduleRepository.findAll();
        log.debug("Found {} schedules", schedules.size());
        return schedules.stream()
                .map(ScheduleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponse> getAllSchedulesByDoctorId(Long doctorId) {
        log.info("Fetching schedules for doctor id={}", doctorId);
        if (doctorId == null || doctorId <= 0) {
            log.warn("Invalid doctorId passed: {}", doctorId);
            throw new IllegalArgumentException("Doctor id must be provided and greater than zero");
        }

        List<Schedule> schedules = scheduleRepository.findAllByDoctorId(doctorId);
        log.debug("Doctor id={} has {} schedules", doctorId, schedules.size());
        return schedules.stream()
                .map(ScheduleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponse> getAllSchedulesByDoctorIdAndDate(Long doctorId, LocalDate date) {
        log.info("Fetching schedules for doctor id={} on date={}", doctorId, date);
        if (doctorId == null || doctorId <= 0) {
            log.warn("Invalid doctorId passed: {}", doctorId);
            throw new IllegalArgumentException("Doctor id must be provided and greater than zero");
        }
        List<Schedule> schedules = scheduleRepository.findAllByDoctorIdAndDate(doctorId, date);
        log.debug("Doctor id={} on {} has {} schedules", doctorId, date, schedules.size());
        return schedules.stream()
                .map(ScheduleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponse> getAllPrincipalSchedules() {
        String userEmail = authUtil.getPrincipalEmail();
        log.info("Fetching schedules for currently authenticated doctor '{}'", userEmail);
        List<Schedule> schedules = scheduleRepository.findAllByDoctorEmail(userEmail);
        log.info("Fetching schedules for currently authenticated doctor '{}'", userEmail);
        return schedules.stream()
                .map(ScheduleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponse> getAllPrincipalSchedulesByDate(LocalDate date) {
        String userEmail = authUtil.getPrincipalEmail();
        log.info("Fetching schedules for doctor '{}' on date={}", userEmail, date);
        List<Schedule> schedules = scheduleRepository.findAllByDoctorEmailAndDate(userEmail, date);
        log.debug("Doctor '{}' on {} has {} schedules", userEmail, date, schedules.size());
        return schedules.stream()
                .map(ScheduleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ScheduleResponse createDoctorSchedule(ScheduleRequest request) {
        log.info("Creating schedule for doctorId={} on date={} at {}",
                request.getDoctorId(), request.getDate(), request.getStartTime());
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> {
                    log.error("Doctor not found with id={}", request.getDoctorId());
                    return new ResourceNotFoundException("Doctor with id " + request.getDoctorId() + " not found");
                });

        Schedule schedule = ScheduleMapper.fromRequest(request, doctor);

        Schedule saved = scheduleRepository.save(schedule);
        log.info("Created schedule id={} for doctorId={}", saved.getId(), request.getDoctorId());

        return ScheduleMapper.toResponse(schedule);
    }

    @Override
    @Transactional
    public void editDoctorSchedule(Long id, ScheduleRequest request) {
        log.info("Editing schedule id={}", id);
        if (id == null || id <= 0) {
            log.warn("Invalid schedule id passed for edit: {}", id);
            throw new IllegalArgumentException("Schedule id must be provided");
        }
    }

    @Override
    @Transactional
    public void deleteDoctorSchedule(Long id) {
        log.info("Deleting schedule id={}", id);
        if (id == null || id <= 0) {
            log.warn("Invalid schedule id passed for delete: {}", id);
            throw new IllegalArgumentException("Schedule id must be provided and greater than zero");
        }
        try {
            scheduleRepository.deleteById(id);
            log.info("Successfully deleted schedule id={}", id);
        } catch (EmptyResultDataAccessException ex) {
            log.error("Failed to delete schedule id={}, not found", id);
            throw new ResourceNotFoundException("Schedule not found with id: " + id);
        }
    }
}
