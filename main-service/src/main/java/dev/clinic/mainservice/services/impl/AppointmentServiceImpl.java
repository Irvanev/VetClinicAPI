package dev.clinic.mainservice.services.impl;

import dev.clinic.mainservice.dtos.appointments.AppointmentRequest;
import dev.clinic.mainservice.dtos.appointments.AppointmentResponse;
import dev.clinic.mainservice.dtos.appointments.AppointmentResponseOwner;
import dev.clinic.mainservice.exceptions.ResourceNotFoundException;
import dev.clinic.mainservice.mapping.AppointmentMapper;
import dev.clinic.mainservice.models.entities.*;
import dev.clinic.mainservice.repositories.*;
import dev.clinic.mainservice.services.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PetRepository petRepository;
    private final ClientRepository clientRepository;
    private final DoctorRepository doctorRepository;

    @Autowired
    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, PetRepository petRepository, ClientRepository clientRepository, DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.petRepository = petRepository;
        this.clientRepository = clientRepository;
        this.doctorRepository = doctorRepository;
    }

    @Override
    public AppointmentResponse createAppointment(AppointmentRequest appointmentRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new ResourceNotFoundException("User isn't authenticated");
        }
        String ownerEmail = authentication.getName();

        Client owner = clientRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with email: " + ownerEmail));

        Pet pet = petRepository.findById(appointmentRequest.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + appointmentRequest.getPetId()));

        Doctor doctor = doctorRepository.findById(appointmentRequest.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + appointmentRequest.getDoctorId()));

        Appointment appointment = AppointmentMapper.fromRequest(appointmentRequest);
        appointment.setClient(owner);
        appointment.setPet(pet);
        appointment.setDoctor(doctor);

        appointment = appointmentRepository.saveAndFlush(appointment);

        return AppointmentMapper.toResponse(appointment);
    }

    @Override
    public AppointmentResponse getAppointmentById(Long id) {
        return AppointmentMapper.toResponse(appointmentRepository.findById(id).orElseThrow());
    }

    @Override
    public List<AppointmentResponseOwner> getAllOwnerAppointments() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new ResourceNotFoundException("User isn't authenticated");
        }
        String ownerEmail = authentication.getName();

        Client owner = clientRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with email: " + ownerEmail));

        List<Appointment> appointments = appointmentRepository.findAllByClientEmail(owner.getEmail());
        return AppointmentMapper.toResponseOwnerList(appointments);
    }

    @Override
    public List<AppointmentResponse> getAllAppointmentsByOwnerId(Long ownerId) {
        return List.of();
    }

    @Override
    public List<AppointmentResponse> getAllAppointments() {
        return List.of();
    }
}
