package dev.clinic.mainservice.services.impl;

import dev.clinic.mainservice.dtos.appointments.AppointmentAdminRequest;
import dev.clinic.mainservice.dtos.appointments.AppointmentRequest;
import dev.clinic.mainservice.dtos.appointments.AppointmentResponse;
import dev.clinic.mainservice.dtos.appointments.AppointmentResponseOwner;
import dev.clinic.mainservice.exceptions.ResourceNotFoundException;
import dev.clinic.mainservice.mapping.AppointmentMapper;
import dev.clinic.mainservice.models.entities.*;
import dev.clinic.mainservice.models.enums.AppointmentStatus;
import dev.clinic.mainservice.models.enums.AppointmentType;
import dev.clinic.mainservice.repositories.*;
import dev.clinic.mainservice.services.AppointmentService;
import dev.clinic.mainservice.utils.AuthUtil;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ScheduleRepository scheduleRepository;
    private final PetRepository petRepository;
    private final ClientRepository clientRepository;
    private final DoctorRepository doctorRepository;
    private final AuthUtil authUtil;
    private final NotificationsService notificationsService;

    private static final Logger log = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    public AppointmentServiceImpl(
            AppointmentRepository appointmentRepository,
            ScheduleRepository scheduleRepository,
            PetRepository petRepository,
            ClientRepository clientRepository,
            DoctorRepository doctorRepository,
            AuthUtil authUtil,
            NotificationsService notificationsService
    ) {
        this.appointmentRepository = appointmentRepository;
        this.scheduleRepository = scheduleRepository;
        this.petRepository = petRepository;
        this.clientRepository = clientRepository;
        this.doctorRepository = doctorRepository;
        this.authUtil = authUtil;
        this.notificationsService = notificationsService;
    }

    /**
     * Создание прима (запись на прием) пользователем
     *
     * @param appointmentRequest объект для создания приема
     */
    @Override
    public void createAppointment(AppointmentRequest appointmentRequest) {
        String ownerEmail = authUtil.getPrincipalEmail();
        log.info("START createAppointment: email={}, date={}, startTime={}",
                ownerEmail,
                appointmentRequest.getAppointmentDate(),
                appointmentRequest.getAppointmentStartTime());
        try {
            LocalDateTime appointmentStartDateTime = appointmentRequest.getAppointmentDate()
                    .atTime(appointmentRequest.getAppointmentStartTime());
            if (appointmentStartDateTime.isBefore(LocalDateTime.now())) {
                log.warn("Attempt to book past date: {}", appointmentStartDateTime);
                throw new IllegalArgumentException("Нельзя записаться на время, которое уже прошло.");
            }

            Client owner = clientRepository.findByEmail(ownerEmail)
                    .orElseThrow(() -> {
                        log.error("Owner not found: email={}", ownerEmail);
                        return new ResourceNotFoundException("Owner not found with email: " + ownerEmail);
                    });

            Pet pet = petRepository.findById(appointmentRequest.getPetId())
                    .orElseThrow(() -> {
                        log.error("Pet not found: id={}", appointmentRequest.getPetId());
                        return new ResourceNotFoundException("Pet not found with id: " +
                                appointmentRequest.getPetId());
                    });

            Doctor doctor = doctorRepository.findById(appointmentRequest.getDoctorId())
                    .orElseThrow(() -> {
                        log.error("Doctor not found: id={}", appointmentRequest.getDoctorId());
                        return new ResourceNotFoundException("Doctor not found with id: " +
                                appointmentRequest.getDoctorId());
                    });

            Appointment appointment = AppointmentMapper.fromRequest(
                    appointmentRequest, owner, pet, doctor);

            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "appointment");
            payload.put("date", appointmentRequest.getAppointmentDate());
            payload.put("start_time", appointmentRequest.getAppointmentStartTime());
            payload.put("name_doctor", doctor.getFirstName() + " " + doctor.getLastName());

            notificationsService.sendNotificationEventAsString(
                    owner.getId(),
                    "Новое уведомление",
                    "Вы записались на прием",
                    payload
            );

            appointmentRepository.saveAndFlush(appointment);
            log.info("END createAppointment: created for user={}, doctor={}",
                    ownerEmail, doctor.getId());

        } catch (IllegalArgumentException | ResourceNotFoundException ex) {
            log.warn("Validation error in createAppointment: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error in createAppointment", ex);
            throw new ServiceException("Unexpected error while creating appointment", ex);
        }
    }

    /**
     * Создание приема администратором
     *
     * @param appointmentAdminRequest объект для создания приема
     */
    @Override
    public void createAppointmentAdmin(AppointmentAdminRequest appointmentAdminRequest) {
        log.info("START createAppointmentAdmin: clientId={}, petId={}, doctorId={}",
                appointmentAdminRequest.getClintId(),
                appointmentAdminRequest.getPetId(),
                appointmentAdminRequest.getDoctorId());
        try {
            Client owner = clientRepository.findById(appointmentAdminRequest.getClintId())
                    .orElseThrow(() -> {
                        log.error("Owner not found: id={}", appointmentAdminRequest.getClintId());
                        return new ResourceNotFoundException("Owner not found with id: " +
                                appointmentAdminRequest.getClintId());
                    });

            Pet pet = petRepository.findById(appointmentAdminRequest.getPetId())
                    .orElseThrow(() -> {
                        log.error("Pet not found: id={}", appointmentAdminRequest.getPetId());
                        return new ResourceNotFoundException("Pet not found with id: " +
                                appointmentAdminRequest.getPetId());
                    });

            Doctor doctor = doctorRepository.findById(appointmentAdminRequest.getDoctorId())
                    .orElseThrow(() -> {
                        log.error("Doctor not found: id={}", appointmentAdminRequest.getDoctorId());
                        return new ResourceNotFoundException("Doctor not found with id: " +
                                appointmentAdminRequest.getDoctorId());
                    });

            Appointment appointment = AppointmentMapper.fromAdminRequest(
                    appointmentAdminRequest, owner, pet, doctor);

            appointmentRepository.saveAndFlush(appointment);
            log.info("END createAppointmentAdmin: created appointment id={}", appointment.getId());

        } catch (ResourceNotFoundException ex) {
            log.warn("Entity not found in createAppointmentAdmin: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error in createAppointmentAdmin", ex);
            throw new ServiceException("Unexpected error while creating appointment by admin", ex);
        }
    }

    /**
     * Отмена записи на прием
     *
     * @param appointmentId уникальный идентификатор приема
     */
    @Override
    public void cancelAppointment(Long appointmentId) {
        log.info("START cancelAppointment: id={}", appointmentId);
        try {
            Appointment appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> {
                        log.warn("Appointment not found: id={}", appointmentId);
                        return new ResourceNotFoundException("Appointment not found with id: " + appointmentId);
                    });

            if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
                log.warn("Attempt to cancel already cancelled appointment: id={}", appointmentId);
                throw new IllegalStateException("Appointment is already canceled");
            }

            appointment.setStatus(AppointmentStatus.CANCELLED);
            appointmentRepository.saveAndFlush(appointment);
            log.info("END cancelAppointment: cancelled id={}", appointmentId);

        } catch (ResourceNotFoundException | IllegalStateException ex) {
            log.warn("Error in cancelAppointment: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error in cancelAppointment: id={}", appointmentId, ex);
            throw new ServiceException("Unexpected error while canceling appointment", ex);
        }
    }

    /**
     * Получение приема по идентификатору
     *
     * @param id уникальный идентификатор приема
     * @return Возращает полученный прием
     */
    @Override
    public AppointmentResponse getAppointmentById(Long id) {
        log.info("START getAppointmentById: id={}", id);
        try {
            Appointment appointment = appointmentRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Appointment not found: id={}", id);
                        return new ResourceNotFoundException("Appointment not found with id: " + id);
                    });

            AppointmentResponse resp = AppointmentMapper.toResponse(appointment);
            log.info("END getAppointmentById: id={}", id);
            return resp;

        } catch (ResourceNotFoundException ex) {
            log.warn("Error in getAppointmentById: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error in getAppointmentById: id={}", id, ex);
            throw new ServiceException("Unexpected error while retrieving appointment", ex);
        }
    }

    /**
     * Получение всех приёмов текущего владельца
     *
     * @return Возвразает полученный список приемов
     */
    @Override
    public List<AppointmentResponseOwner> getAllOwnerAppointments() {
        log.info("START getAllOwnerAppointments");
        try {
            String ownerEmail = authUtil.getPrincipalEmail();
            Client owner = clientRepository.findByEmail(ownerEmail)
                    .orElseThrow(() -> {
                        log.warn("Owner not found: email={}", ownerEmail);
                        return new ResourceNotFoundException("Owner not found with email: " + ownerEmail);
                    });

            List<AppointmentResponseOwner> result = appointmentRepository
                    .findAllByClientEmail(owner.getEmail()).stream()
                    .map(AppointmentMapper::toResponseOwner)
                    .collect(Collectors.toList());

            log.info("END getAllOwnerAppointments: found {} for user={}", result.size(), ownerEmail);
            return result;

        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error in getAllOwnerAppointments", ex);
            throw new ServiceException("Unexpected error while fetching owner's appointments", ex);
        }
    }

    /**
     * Получение всех приемов текущего владельца по статусу
     *
     * @param status статус приема
     * @return Возращает полученный список приемов
     */
    @Override
    public List<AppointmentResponseOwner> getAllOwnerAppointmentsByStatus(AppointmentStatus status) {
        log.info("START getAllOwnerAppointmentsByStatus: status={}", status);
        try {
            String ownerEmail = authUtil.getPrincipalEmail();
            Client owner = clientRepository.findByEmail(ownerEmail)
                    .orElseThrow(() -> {
                        log.warn("Owner not found: email={}", ownerEmail);
                        return new ResourceNotFoundException("Owner not found with email: " + ownerEmail);
                    });

            List<AppointmentResponseOwner> result = appointmentRepository
                    .findAllByClientEmailAndStatusOrderByAppointmentDateDescAppointmentStartTimeDesc(
                            owner.getEmail(), status
                    ).stream()
                    .map(AppointmentMapper::toResponseOwner)
                    .collect(Collectors.toList());

            log.info("END getAllOwnerAppointmentsByStatus: found {} for user={}, status={}",
                    result.size(), ownerEmail, status);
            return result;

        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error in getAllOwnerAppointmentsByStatus", ex);
            throw new ServiceException("Unexpected error while fetching owner's appointments by status", ex);
        }
    }

    /**
     * Получение приемов для опредленного пользователя
     *
     * @param ownerId идентификатор владельца
     * @return Возращает полученный список приемов
     */
    @Override
    public List<AppointmentResponseOwner> getAllAppointmentsByOwnerId(Long ownerId) {
        log.info("START getAllAppointmentsByOwnerId: ownerId={}", ownerId);
        if (ownerId == null || ownerId <= 0) {
            log.warn("Invalid ownerId: {}", ownerId);
            throw new IllegalArgumentException("Invalid ownerId: " + ownerId);
        }
        try {
            Client client = clientRepository.findById(ownerId)
                    .orElseThrow(() -> {
                        log.warn("Client not found: id={}", ownerId);
                        return new ResourceNotFoundException("Client not found with id: " + ownerId);
                    });

            List<AppointmentResponseOwner> result = appointmentRepository
                    .findAllByClientIdOrderByAppointmentDateDesc(client.getId()).stream()
                    .map(AppointmentMapper::toResponseOwner)
                    .collect(Collectors.toList());

            log.info("END getAllAppointmentsByOwnerId: found {} for ownerId={}", result.size(), ownerId);
            return result;

        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error in getAllAppointmentsByOwnerId", ex);
            throw new ServiceException("Unexpected error while fetching appointments by ownerId", ex);
        }
    }

    /**
     * Получение приемов для определенного питомца
     *
     * @param petId уникальный идентификатор питомца
     * @return Возвращает список приемов определенного питомца
     */
    @Override
    public List<AppointmentResponseOwner> getAllOwnerAppointmentsByPetId(Long petId) {
        log.info("START getAllOwnerAppointmentsByPetId: petId={}", petId);
        try {
            Pet pet = petRepository.findById(petId)
                    .orElseThrow(() -> {
                        log.warn("Pet not found: id={}", petId);
                        return new ResourceNotFoundException("Pet not found with id: " + petId);
                    });

            List<AppointmentResponseOwner> result = appointmentRepository
                    .findAllByPetIdOrderByAppointmentDateDesc(pet.getId()).stream()
                    .map(AppointmentMapper::toResponseOwner)
                    .collect(Collectors.toList());

            log.info("END getAllOwnerAppointmentsByPetId: found {} for petId={}", result.size(), petId);
            return result;

        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error in getAllOwnerAppointmentsByPetId", ex);
            throw new ServiceException("Unexpected error while fetching appointments by petId", ex);
        }
    }

    /**
     * Получение всех приемов
     *
     * @return Возращает список приемов
     */
    @Override
    public List<AppointmentResponse> getAllAppointments() {
        log.info("START getAllAppointments");
        try {
            List<AppointmentResponse> result = appointmentRepository.findAll().stream()
                    .map(AppointmentMapper::toResponse)
                    .collect(Collectors.toList());
            log.info("END getAllAppointments: total={}", result.size());
            return result;
        } catch (Exception ex) {
            log.error("Unexpected error in getAllAppointments", ex);
            throw new ServiceException("Unexpected error while fetching all appointments", ex);
        }
    }

    /**
     * Расчет доступных временных слотов для записи на прием
     *
     * @param doctorId уникальный идентификатор врача
     * @param date дата приема
     * @param appointmentType тип приема для расчета длительности
     * @return Возвращает список доступных слотов
     */
    @Override
    public List<LocalTime> getAvailableTimeSlots(Long doctorId, LocalDate date, AppointmentType appointmentType) {
        log.info("Calculating available time slots for doctorId={}, date={}", doctorId, date);
        try {
            Duration slotDuration = appointmentType.getDuration();

            Schedule schedule = scheduleRepository.findByDoctorIdAndDate(doctorId, date)
                    .orElseThrow(() -> {
                        log.warn("Schedule not found for doctorId={} on date={}", doctorId, date);
                        return new ResourceNotFoundException("Schedule not found");
                    });
            LocalTime scheduleStart = schedule.getStartTime();
            LocalTime scheduleEnd = schedule.getEndTime();

            List<Appointment> bookedAppointments = appointmentRepository.findByDoctorIdAndAppointmentDate(doctorId, date);
            log.info("Found {} booked appointments for doctorId={} on date={}", bookedAppointments.size(), doctorId, date);

            List<LocalTime> availableSlots = new ArrayList<>();
            LocalTime now = LocalTime.now();
            boolean isToday = date.equals(LocalDate.now());

            for (LocalTime slot = scheduleStart; !slot.plus(slotDuration).isAfter(scheduleEnd); slot = slot.plusMinutes(15)) {
                if (isToday && slot.isBefore(now)) {
                    continue;
                }
                final LocalTime currentSlot = slot;
                boolean conflict = bookedAppointments.stream().anyMatch(appointment -> {
                    LocalTime apptStart = appointment.getAppointmentStartTime();
                    LocalTime apptEnd = appointment.getAppointmentEndTime();
                    LocalTime candidateEnd = currentSlot.plus(slotDuration);
                    return currentSlot.isBefore(apptEnd) && apptStart.isBefore(candidateEnd);
                });
                if (!conflict) {
                    availableSlots.add(currentSlot);
                }
            }
            log.info("Found {} available time slots for doctorId={} on date={}", availableSlots.size(), doctorId, date);

            return availableSlots;

        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error in getAvailableTimeSlots", ex);
            throw new ServiceException("Unexpected error while calculating available time slots", ex);
        }
    }

    /**
     * Регулярная задача: отмечает статус NO_SHOW для прошедших не проведенных приемов
     */
    @Scheduled(fixedDelay = 60_000)
    @Override
    public void updateNoShowAppointments() {
        log.info("START updateNoShowAppointments");
        try {
            LocalTime threshold = LocalTime.now().minusHours(2);
            List<Appointment> toUpdate = appointmentRepository
                    .findByStatusAndAppointmentEndTimeBefore(
                            AppointmentStatus.SCHEDULED,
                            threshold
                    );
            if (!toUpdate.isEmpty()) {
                log.info("Updating {} appointments to NO_SHOW", toUpdate.size());
                toUpdate.forEach(a -> a.setStatus(AppointmentStatus.NO_SHOW));
                appointmentRepository.saveAll(toUpdate);
            } else {
                log.info("No appointments to mark as NO_SHOW");
            }
            log.info("END updateNoShowAppointments");
        } catch (Exception ex) {
            log.error("Unexpected error in updateNoShowAppointments", ex);
            throw new ServiceException("Unexpected error in scheduled no-show update", ex);
        }
    }
}
