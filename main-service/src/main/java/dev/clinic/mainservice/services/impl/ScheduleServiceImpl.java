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
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;
    private final AuthUtil authUtil;

    private static final Logger log = LoggerFactory.getLogger(ScheduleServiceImpl.class);

    public ScheduleServiceImpl(
            ScheduleRepository scheduleRepository,
            DoctorRepository doctorRepository,
            AuthUtil authUtil
    ) {
        this.scheduleRepository = scheduleRepository;
        this.doctorRepository = doctorRepository;
        this.authUtil = authUtil;
    }

    /**
     * Получение расписаний всех врачей
     * @return возвращает список расписаний
     */
    @Override
    @Cacheable(value = "schedules", key = "'all'")
    public List<ScheduleResponse> getAllSchedules() {
        log.info("Fetching all schedules was called");
        try {
            List<Schedule> schedules = scheduleRepository.findAll();
            log.debug("Found {} schedules", schedules.size());
            return schedules.stream()
                    .map(ScheduleMapper::toResponse)
                    .collect(Collectors.toList());
        } catch (DataAccessException ex) {
            log.error("Database error in getAllSchedules", ex);
            throw new ServiceException("Database error while fetching all schedules", ex);
        } catch (Exception ex) {
            log.error("Unexpected error in getAllSchedules", ex);
            throw new ServiceException("Unexpected error while fetching all schedules", ex);
        }
    }

    /**
     * Получения расписания определнного врача
     * @param doctorId уникальный идентификатор врача
     * @return возвращает полученное расписание врача
     */
    @Override
    @Cacheable(value = "schedules", key = "#doctorId")
    public List<ScheduleResponse> getAllSchedulesByDoctorId(Long doctorId) {
        log.info("Fetching schedules for doctor id={} was called", doctorId);
        try {
            if (doctorId == null || doctorId <= 0) {
                log.warn("Invalid doctorId passed: {}", doctorId);
                throw new IllegalArgumentException("Doctor id must be provided and greater than zero");
            }

            List<Schedule> schedules = scheduleRepository.findAllByDoctorId(doctorId);
            log.debug("Doctor id={} has {} schedules", doctorId, schedules.size());

            return schedules.stream()
                    .map(ScheduleMapper::toResponse)
                    .collect(Collectors.toList());

        } catch (DataAccessException ex) {
            log.error("Database error in getAllSchedulesByDoctorId", ex);
            throw new ServiceException("Database error while fetching all schedules by doctor id", ex);
        } catch (Exception ex) {
            log.error("Unexpected error in getAllSchedulesByDoctorId", ex);
            throw new ServiceException("Unexpected error while fetching all schedules bu doctor id", ex);
        }
    }

    /**
     * Получения списка расписания определлного врача в определленную дату
     * @param doctorId уникальный идентификатор врача
     * @param date дата рабочего графика врача
     * @return возвращает полученное расписание врача
     */
    @Override
    @Cacheable("schedules")
    public List<ScheduleResponse> getAllSchedulesByDoctorIdAndDate(Long doctorId, LocalDate date) {
        log.info("Fetching schedules for doctor id={} on date={} was called", doctorId, date);
        try {
            if (doctorId == null || doctorId <= 0) {
                log.warn("Invalid doctorId passed: {}", doctorId);
                throw new IllegalArgumentException("Doctor id must be provided and greater than zero");
            }
            List<Schedule> schedules = scheduleRepository.findAllByDoctorIdAndDate(doctorId, date);
            log.debug("Doctor id={} on {} has {} schedules", doctorId, date, schedules.size());

            return schedules.stream()
                    .map(ScheduleMapper::toResponse)
                    .collect(Collectors.toList());

        } catch (ResourceNotFoundException | AccessDeniedException ex) {
            log.error("Error in getAllSchedulesByDoctorIdAndDate: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error in getAllSchedulesByDoctorIdAndDate for id = {}", doctorId, ex);
            throw new ServiceException("Unexpected error while retrieving schedule", ex);
        }
    }

    /**
     * Получения расписания для авторизированного врача
     * @return возвращает полученное расписание врача
     */
    @Override
    @Cacheable("schedules")
    public List<ScheduleResponse> getAllPrincipalSchedules() {
        log.info("Get all principal schedules was called");
        try {
            String userEmail = authUtil.getPrincipalEmail();
            log.info("Fetching schedules for currently authenticated doctor '{}'", userEmail);
            List<Schedule> schedules = scheduleRepository.findAllByDoctorEmail(userEmail);

            return schedules.stream()
                    .map(ScheduleMapper::toResponse)
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            log.error("Unexpected error in getAllPrincipalSchedules", ex);
            throw new ServiceException("Unexpected error while fetching schedule", ex);
        }
    }

    /**
     * Получения расписания для авторизированного врача в определенную дату
     * @param date дата рабочего графика врача
     * @return возвращает полученное расписание врача
     */
    @Override
    @Cacheable("schedules")
    public List<ScheduleResponse> getAllPrincipalSchedulesByDate(LocalDate date) {
        log.info("Get all principal schedules by date was called");
        try {
            String userEmail = authUtil.getPrincipalEmail();
            log.debug("User is authenticated with email: {}", userEmail);

            log.info("Fetching schedules for doctor '{}' on date={}", userEmail, date);
            List<Schedule> schedules = scheduleRepository.findAllByDoctorEmailAndDate(userEmail, date);
            log.debug("Doctor '{}' on {} has {} schedules", userEmail, date, schedules.size());

            return schedules.stream()
                    .map(ScheduleMapper::toResponse)
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            log.error("Unexpected error in getAllPrincipalSchedulesByDate", ex);
            throw new ServiceException("Unexpected error while fetching schedule", ex);
        }
    }

    /**
     * Создание раписания врача
     * @param request объект для создания расписания
     * @return возвращает полученное расписание врача
     */
    @Override
    @CacheEvict(value = "schedules", allEntries = true)
    public ScheduleResponse createDoctorSchedule(ScheduleRequest request) {
        log.info("Creating schedule for doctorId={} on date={} at {}",
                request.getDoctorId(), request.getDate(), request.getStartTime());
        try {
            Doctor doctor = doctorRepository.findById(request.getDoctorId())
                    .orElseThrow(() -> {
                        log.error("Doctor not found with id={}", request.getDoctorId());
                        return new ResourceNotFoundException("Doctor with id " + request.getDoctorId() + " not found");
                    });

            Schedule schedule = ScheduleMapper.fromRequest(request, doctor);

            Schedule saved = scheduleRepository.save(schedule);
            log.info("Created schedule id={} for doctorId={}", saved.getId(), request.getDoctorId());

            return ScheduleMapper.toResponse(schedule);

        } catch (ResourceNotFoundException | IllegalArgumentException ex) {
            log.error("Validation error in createDoctorSchedule: {}", ex.getMessage());
            throw ex;
        } catch (DataAccessException ex) {
            log.error("Database error in createDoctorSchedule", ex);
            throw new ServiceException("Database error while creating schedule", ex);
        } catch (Exception ex) {
            log.error("Unexpected error in createDoctorSchedule", ex);
            throw new ServiceException("Unexpected error while creating schedule", ex);
        }
    }

    /**
     * Редактирование расписания врача
     * @param id уникальный идентфикатор раписания
     * @param request объект для реадактирования расписания
     */
    @Override
    @CacheEvict(value = "schedules", allEntries = true)
    public void editDoctorSchedule(Long id, ScheduleRequest request) {
        log.info("Editing schedule id={}", id);
        try {
            if (id == null || id <= 0) {
                log.warn("Invalid schedule id passed for edit: {}", id);
                throw new IllegalArgumentException("Schedule id must be provided");
            }
        } catch (ResourceNotFoundException | AccessDeniedException | IllegalArgumentException ex) {
            log.error("Error in editDoctorSchedule: {}", ex.getMessage());
            throw ex;
        } catch (DataAccessException ex) {
            log.error("Database error in editDoctorSchedule for id = {}", id, ex);
            throw new ServiceException("Database error while editing schedule", ex);
        } catch (Exception ex) {
            log.error("Unexpected error in editDoctorSchedule for id = {}", id, ex);
            throw new ServiceException("Unexpected error while editing schedule", ex);
        }
    }

    /**
     * Удлаения расписания врача
     * @param id уникальный идентификатор расписания
     */
    @Override
    @CacheEvict(value = "schedules", allEntries = true)
    public void deleteDoctorSchedule(Long id) {
        log.info("Deleting schedule id={}", id);
        try {
            if (id == null || id <= 0) {
                log.warn("Invalid schedule id passed for delete: {}", id);
                throw new IllegalArgumentException("Schedule id must be provided and greater than zero");
            }

            scheduleRepository.deleteById(id);

            log.info("Successfully deleted schedule id={}", id);

        } catch (ResourceNotFoundException | AccessDeniedException ex) {
            log.error("Error in deleteDoctorSchedule: {}", ex.getMessage());
            throw ex;
        } catch (DataAccessException ex) {
            log.error("Database error in deleteDoctorSchedule for id = {}", id, ex);
            throw new ServiceException("Database error while deleting schedule", ex);
        } catch (Exception ex) {
            log.error("Unexpected error in deleteDoctorSchedule for id = {}", id, ex);
            throw new ServiceException("Unexpected error while deleting schedule", ex);
        }
    }
}
