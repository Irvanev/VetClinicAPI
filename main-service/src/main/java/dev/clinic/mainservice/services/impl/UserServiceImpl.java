package dev.clinic.mainservice.services.impl;

import dev.clinic.mainservice.dtos.auth.ChangePasswordRequest;
import dev.clinic.mainservice.dtos.pets.PetResponse;
import dev.clinic.mainservice.dtos.users.*;
import dev.clinic.mainservice.exceptions.ResourceNotFoundException;
import dev.clinic.mainservice.mapping.UsersMapper;
import dev.clinic.mainservice.models.entities.*;
import dev.clinic.mainservice.models.enums.RoleEnum;
import dev.clinic.mainservice.repositories.*;
import dev.clinic.mainservice.services.ImageUploaderService;
import dev.clinic.mainservice.services.UserService;
import org.hibernate.service.spi.ServiceException;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailMessageProducer emailService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final BranchesRepository branchesRepository;
    private final DoctorRepository doctorRepository;
    private final ClientRepository clientRepository;
    private final ImageUploaderService imageUploaderService;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            EmailMessageProducer emailService,
            ModelMapper modelMapper,
            PasswordEncoder passwordEncoder,
            BranchesRepository branchesRepository,
            DoctorRepository doctorRepository,
            ClientRepository clientRepository,
            ImageUploaderService imageUploaderService
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.emailService = emailService;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.branchesRepository = branchesRepository;
        this.doctorRepository = doctorRepository;
        this.clientRepository = clientRepository;
        this.imageUploaderService = imageUploaderService;
    }


    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> usersPage = userRepository.findAll(pageable);
        List<UserResponse> userResponses = usersPage.getContent().stream()
                .map(UsersMapper::toResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(userResponses, pageable, usersPage.getTotalElements());
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public UserResponse getUserByPetId(Long petId) {
        User user = userRepository.findByPetsId(petId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with pet id: " + petId));
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public UserDetailResponse getPrincipalUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new ResourceNotFoundException("User isn't authenticated");
        }
        String ownerEmail = authentication.getName();
        Client client = clientRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + ownerEmail));
        return modelMapper.map(client, UserDetailResponse.class);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        // Получаем аутентифицированного пользователя из SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResourceNotFoundException("User isn't authenticated");
        }

        String userEmail = authentication.getName();

        // Загружаем пользователя из базы данных по email
        Client client = (Client) userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Проверяем, что введённый старый пароль соответствует сохранённому
        if (!passwordEncoder.matches(request.getOldPassword(), client.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        // Проверяем, что новый пароль и подтверждение совпадают
        if (!request.getNewPassword().equals(request.getRepeatedNewPassword())) {
            throw new IllegalArgumentException("New password and repeated password do not match");
        }

        // Обновляем пароль (шифруем новый пароль)
        client.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // Сохраняем изменения в базе данных
        userRepository.saveAndFlush(client);
    }

    @Override
    public UserResponse createDoctor(DoctorRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User already exists");
        }
        String temporaryPassword = UUID.randomUUID().toString();

        Doctor doctor = modelMapper.map(request, Doctor.class);

        Role role = roleRepository.findByName(RoleEnum.Doctor)
                .orElseThrow(() -> new IllegalStateException("Default role not found"));

        Branches branch = branchesRepository.findById(request.getBranchId())
                        .orElseThrow(() -> new IllegalStateException("Default branch not found"));

        doctor.setPassword(passwordEncoder.encode(temporaryPassword));
        doctor.setEnabled(true);
        doctor.setVerificationCode(null);
        doctor.setVerificationCodeExpiresAt(null);
        doctor.setRole(role);
        doctor.setBranch(branch);

        userRepository.saveAndFlush(doctor);

        emailService.sendPasswordEmail(request.getEmail(), temporaryPassword);

        return modelMapper.map(doctor, UserResponse.class);
    }

    @Override
    public List<DoctorResponseForSelectInAppointment> getAllDoctorsByBranchId(Long branchId) {
        return doctorRepository.findAllByBranchId(branchId).stream()
                .map(user -> modelMapper.map(user, DoctorResponseForSelectInAppointment.class))
                .collect(Collectors.toList());
    }

    public UserResponse editClientByAdmin(EditClientRequest request, Long clientId, MultipartFile photo) {
        if (clientId == null || clientId <= 0) {
            throw new IllegalArgumentException("Invalid client ID");
        }

        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        String oldPhotoUrl = client.getPhotoUrl();

        UsersMapper.fromRequest(request);

        if (photo != null && !photo.isEmpty()) {
            try {
                if (!Objects.requireNonNull(photo.getContentType()).startsWith("image/")) {
                    throw new IllegalArgumentException("Invalid file type. Only images are allowed");
                }

                String newPhotoUrl = imageUploaderService.uploadImage(photo);
                client.setPhotoUrl(newPhotoUrl);

                if (oldPhotoUrl != null && !oldPhotoUrl.isEmpty()) {
                    try {
                        imageUploaderService.deleteImage(oldPhotoUrl);
                    } catch (Exception e) {
                        throw new ResourceNotFoundException("Image upload failed");
                    }
                }
            } catch (IOException | RuntimeException ex) {
                throw new ServiceException("Image processing failed", ex);
            }
        }

        try {
            Client updatedClient = clientRepository.save(client);
            return modelMapper.map(updatedClient, UserResponse.class);
        } catch (DataAccessException ex) {
            throw new ServiceException("Error saving client data", ex);
        }
    }
}
