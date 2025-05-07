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
    @Cacheable("schedulesList")
    public List<ScheduleResponse> getAllSchedules() {
        List<Schedule> schedules = scheduleRepository.findAll();

        return schedules.stream()
                .map(ScheduleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable("schedulesList")
    public List<ScheduleResponse> getAllSchedulesByDoctorId(Long doctorId) {
        if (doctorId == null || doctorId <= 0) {
            throw new IllegalArgumentException("Doctor id must be provided and greater than zero");
        }

        List<Schedule> schedules = scheduleRepository.findAllByDoctorId(doctorId);

        return schedules.stream()
                .map(ScheduleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable("schedules")
    public List<ScheduleResponse> getAllSchedulesByDoctorIdAndDate(Long doctorId, LocalDate date) {
        if (doctorId == null || doctorId <= 0) {
            throw new IllegalArgumentException("Doctor id must be provided and greater than zero");
        }
        List<Schedule> schedules = scheduleRepository.findAllByDoctorIdAndDate(doctorId, date);
        log.info("Requesting schedules for doctor '{}' on date {}", doctorId, date);
        return schedules.stream()
                .map(ScheduleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable("schedulesList")
    public List<ScheduleResponse> getAllPrincipalSchedules() {
        String userEmail = authUtil.getPrincipalEmail();
        log.info("Requesting schedules for doctor '{}'", userEmail);
        List<Schedule> schedules = scheduleRepository.findAllByDoctorEmail(userEmail);

        return schedules.stream()
                .map(ScheduleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "schedules")
    public List<ScheduleResponse> getAllPrincipalSchedulesByDate(LocalDate date) {
        String userEmail = authUtil.getPrincipalEmail();
        log.info("Requesting schedules for doctor '{}' on date {}", userEmail, date);
        List<Schedule> schedules = scheduleRepository.findAllByDoctorEmailAndDate(userEmail, date);

        return schedules.stream()
                .map(ScheduleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"schedules", "schedulesList"}, allEntries = true)
    public ScheduleResponse createDoctorSchedule(ScheduleRequest request) {
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor with id " + request.getDoctorId() + " not found"));

        Schedule schedule = ScheduleMapper.fromRequest(request, doctor);

        scheduleRepository.save(schedule);

        return ScheduleMapper.toResponse(schedule);
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "schedules", key = "#id"),
                    @CacheEvict(value = "schedulesList", allEntries = true)
            }
    )
    public void editDoctorSchedule(Long id, ScheduleRequest request) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Schedule id must be provided");
        }
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "schedules", key = "#id"),
                    @CacheEvict(value = "schedulesList", allEntries = true)
            }
    )
    public void deleteDoctorSchedule(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Schedule id must be provided and greater than zero");
        }
        try {
            scheduleRepository.deleteById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new ResourceNotFoundException("Schedule not found with id: " + id);
        }
    }
}
