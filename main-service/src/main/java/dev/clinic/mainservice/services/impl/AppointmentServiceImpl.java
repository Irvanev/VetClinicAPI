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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
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

    @Override
    @CacheEvict(value = {"appointments", "appointmentsList"}, allEntries = true)
    public void createAppointment(AppointmentRequest appointmentRequest) {
        String ownerEmail = authUtil.getPrincipalEmail();
        log.info("START make an appointment: email={}", ownerEmail);

        LocalDateTime appointmentStartDateTime = appointmentRequest.getAppointmentDate()
                .atTime(appointmentRequest.getAppointmentStartTime());

        if (appointmentStartDateTime.isBefore(LocalDateTime.now())) {
            log.warn("Attempt to book a past date: {}", appointmentStartDateTime);
            throw new IllegalArgumentException("Нельзя записаться на время, которое уже прошло.");
        }

        Client owner = clientRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> {
                    log.error("Owner not found with email: {}", ownerEmail);
                    return new ResourceNotFoundException("Owner not found with email: " + ownerEmail);
                });

        Pet pet = petRepository.findById(appointmentRequest.getPetId())
                .orElseThrow(() -> {
                    log.error("Pet not found with id: {}", appointmentRequest.getPetId());
                    return new ResourceNotFoundException("Pet not found with id: " + appointmentRequest.getPetId());
                });

        Doctor doctor = doctorRepository.findById(appointmentRequest.getDoctorId())
                .orElseThrow(() -> {
                    log.error("Doctor not found with id: {}", appointmentRequest.getDoctorId());
                    return new ResourceNotFoundException("Doctor not found with id: " + appointmentRequest.getDoctorId());
                });

        Appointment appointment = AppointmentMapper.fromRequest(appointmentRequest, owner, pet, doctor);

        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "appointment");
        payload.put("date", appointmentRequest.getAppointmentDate());
        payload.put("start_time", appointmentRequest.getAppointmentStartTime());
        payload.put("name_doctor", doctor.getFirstName() + " " + doctor.getLastName());


        notificationsService.sendNotificationEventAsString(owner.getId(), "Новое уведомление", "Вы записались на прием", payload);

        appointmentRepository.saveAndFlush(appointment);
        log.info("Appointment created successfully for user: {}, doctor: {}", ownerEmail, doctor.getId());
    }

    @Override
    @CacheEvict(value = {"appointments", "appointmentsList"}, allEntries = true)
    public void createAppointmentAdmin(AppointmentAdminRequest appointmentAdminRequest) {
        Client owner = clientRepository.findById(appointmentAdminRequest.getClintId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with email: " + appointmentAdminRequest.getClintId()));
        Pet pet = petRepository.findById(appointmentAdminRequest.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + appointmentAdminRequest.getPetId()));
        Doctor doctor = doctorRepository.findById(appointmentAdminRequest.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + appointmentAdminRequest.getDoctorId()));

        Appointment appointment = AppointmentMapper.fromAdminRequest(appointmentAdminRequest, owner, pet, doctor);

        appointmentRepository.saveAndFlush(appointment);

    }


    @Override
    @Cacheable(value = "appointments", key = "#id")
    public AppointmentResponse getAppointmentById(Long id) {
        return AppointmentMapper.toResponse(appointmentRepository.findById(id).orElseThrow());
    }

    @Override
    @Cacheable("appointmentsList")
    public List<AppointmentResponseOwner> getAllOwnerAppointments() {

        String ownerEmail = authUtil.getPrincipalEmail();

        Client owner = clientRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with email: " + ownerEmail));

        List<Appointment> appointments = appointmentRepository.findAllByClientEmail(owner.getEmail());
        return appointments
                .stream()
                .map(AppointmentMapper::toResponseOwner)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "appointmentsList", key = "#ownerId")
    public List<AppointmentResponseOwner> getAllAppointmentsByOwnerId(Long ownerId) {
        if (ownerId == null || ownerId <= 0) {
            throw new IllegalArgumentException("Invalid ownerId: " + ownerId);
        }
        Client client = clientRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + ownerId));

        try {
            List<Appointment> appointments = appointmentRepository.findAllByClientIdOrderByAppointmentDateDesc(client.getId());
            return appointments
                    .stream()
                    .map(AppointmentMapper::toResponseOwner)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new ServiceException("Error accessing data", e);
        }
    }

    @Override
    @Cacheable(value = "appointmentsList", key = "#petId")
    public List<AppointmentResponseOwner> getAllOwnerAppointmentsByPetId(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + petId));

        List<Appointment> appointments = appointmentRepository.findAllByPetIdOrderByAppointmentDateDesc(pet.getId());
        return appointments
                .stream()
                .map(AppointmentMapper::toResponseOwner)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponse> getAllAppointments() {
        List<Appointment> appointments = appointmentRepository.findAll();
        return appointments
                .stream()
                .map(AppointmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<LocalTime> getAvailableTimeSlots(Long doctorId, LocalDate date, AppointmentType appointmentType) {
        log.info("Calculating available time slots for doctorId={}, date={}", doctorId, date);
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
    }

    @Scheduled(fixedDelay = 60000)
    @Override
    public void updateNoShowAppointments() {
        log.info("Running scheduled task to update no-show appointments");
        LocalTime threshold = LocalTime.now().minusHours(2);
        List<Appointment> appointments = appointmentRepository.findByStatusAndAppointmentEndTimeBefore(AppointmentStatus.SCHEDULED, threshold);
        if (!appointments.isEmpty()) {
            log.info("Updating status to NO_SHOW for {} appointments", appointments.size());
            appointments.forEach(appointment -> appointment.setStatus(AppointmentStatus.NO_SHOW));
            appointmentRepository.saveAll(appointments);
        } else {
            log.info("No scheduled appointments to update.");
        }
    }
}
