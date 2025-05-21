package dev.clinic.mainservice.services.impl;

import dev.clinic.mainservice.dtos.auth.ChangePasswordRequest;
import dev.clinic.mainservice.dtos.users.*;
import dev.clinic.mainservice.exceptions.ResourceNotFoundException;
import dev.clinic.mainservice.mapping.DoctorMapping;
import dev.clinic.mainservice.mapping.PetMapper;
import dev.clinic.mainservice.mapping.UserMapping;
import dev.clinic.mainservice.models.entities.*;
import dev.clinic.mainservice.models.enums.RoleEnum;
import dev.clinic.mainservice.repositories.*;
import dev.clinic.mainservice.services.ImageUploaderService;
import dev.clinic.mainservice.services.UserService;
import dev.clinic.mainservice.utils.AuthUtil;
import org.hibernate.service.spi.ServiceException;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    private final AuthUtil authUtil;

    public UserServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            EmailMessageProducer emailService,
            ModelMapper modelMapper,
            PasswordEncoder passwordEncoder,
            BranchesRepository branchesRepository,
            DoctorRepository doctorRepository,
            ClientRepository clientRepository,
            ImageUploaderService imageUploaderService,
            AuthUtil authUtil
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
        this.authUtil = authUtil;
    }

    @Override
    @Cacheable(value = "users", key = "'all'")
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> usersPage = userRepository.findAll(pageable);
        List<UserResponse> userResponses = usersPage.getContent().stream()
                .map(UserMapping::toResponse)
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
        String ownerEmail = authUtil.getPrincipalEmail();
        Client client = clientRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + ownerEmail));
        return modelMapper.map(client, UserDetailResponse.class);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void changePassword(ChangePasswordRequest request) {
        String userEmail = authUtil.getPrincipalEmail();

        Client client = (Client) userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), client.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        if (!request.getNewPassword().equals(request.getRepeatedNewPassword())) {
            throw new IllegalArgumentException("New password and repeated password do not match");
        }

        client.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.saveAndFlush(client);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public DoctorResponse createDoctor(DoctorRequest request) {
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

        return DoctorMapping.toResponse(doctor);
    }

    @Override
    @Cacheable(value = "users", key = "#branchId")
    public List<DoctorResponseForSelectInAppointment> getAllDoctorsByBranchId(Long branchId) {
        return doctorRepository.findAllByBranchId(branchId).stream()
                .map(UserMapping::toDoctorByBranchId)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "users", allEntries = true)
    @Override
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

        UserMapping.fromRequest(request);

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

    @Override
    public UserProfileResponse editClient(EditClientRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        String clientEmail = authUtil.getPrincipalEmail();

        Client client = clientRepository.findByEmail(clientEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        UserMapping.updateFromRequest(client, request);

        try {
            Client updatedClient = clientRepository.save(client);
            return UserMapping.toProfileResponse(updatedClient);
        } catch (DataAccessException ex) {
            throw new ServiceException("Error saving client data", ex);
        }
    }

    @Override
    public UserProfileResponse editPhoto(MultipartFile photo) {
        try {
            if (photo == null || photo.isEmpty()) {
                throw new IllegalArgumentException("Photo must be provided");
            }
            String contentType = photo.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Invalid file type. Only images are allowed");
            }
            String userEmail = authUtil.getPrincipalEmail();

            Client client = clientRepository.findByEmail(userEmail)
                    .orElseThrow(() -> {
                        return new ResourceNotFoundException("Client not found with id: " + userEmail);
                    });

            String photoUrl;
            try {
                photoUrl = imageUploaderService.uploadImage(photo);
            } catch (IOException ex) {
                throw new ServiceException("Image upload failed", ex);
            }

            client.setPhotoUrl(photoUrl);
            Client updated = clientRepository.save(client);

            return UserMapping.toProfileResponse(updated);

        } catch (ResourceNotFoundException | IllegalArgumentException ex) {
            throw ex;
        } catch (DataAccessException ex) {
            throw new ServiceException("Database error while updating client photo", ex);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException("Unexpected error while updating client photo", ex);
        }
    }
}
