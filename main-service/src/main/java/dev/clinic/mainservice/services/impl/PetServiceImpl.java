package dev.clinic.mainservice.services.impl;

import dev.clinic.mainservice.dtos.pets.PetRequest;
import dev.clinic.mainservice.dtos.pets.PetRequestAdmin;
import dev.clinic.mainservice.dtos.pets.PetResponse;
import dev.clinic.mainservice.exceptions.ResourceNotFoundException;
import dev.clinic.mainservice.models.entities.Pet;
import dev.clinic.mainservice.models.entities.User;
import dev.clinic.mainservice.repositories.PetRepository;
import dev.clinic.mainservice.repositories.UserRepository;
import dev.clinic.mainservice.services.ImageUploaderService;
import dev.clinic.mainservice.services.PetService;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.service.spi.ServiceException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PetServiceImpl implements PetService {

    private final ModelMapper modelMapper;
    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final ImageUploaderService imageUploaderService;

    @Autowired
    public PetServiceImpl(
            ModelMapper modelMapper,
            PetRepository petRepository,
            UserRepository userRepository,
            ImageUploaderService imageUploaderService
    ) {
        this.modelMapper = modelMapper;
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.imageUploaderService = imageUploaderService;
    }

    @Override
    public PetResponse createPet(PetRequest petRequest, MultipartFile photo) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResourceNotFoundException("User isn't authenticated");
        }
        String ownerEmail = authentication.getName();

        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with email: " + ownerEmail));

        Pet pet = modelMapper.map(petRequest, Pet.class);
        pet.setOwner(owner);

        if (photo != null && !photo.isEmpty()) {
            try {
                if (!Objects.requireNonNull(photo.getContentType()).startsWith("image/")) {
                    throw new IllegalArgumentException("Invalid file type. Only images are allowed");
                }

                String newPhotoUrl = imageUploaderService.uploadImage(photo);
                pet.setPhotoUrl(newPhotoUrl);

            } catch (IOException | RuntimeException ex) {
                throw new ServiceException("Image processing failed", ex);
            }
        }

        Pet savedPet = petRepository.save(pet);
        return modelMapper.map(savedPet, PetResponse.class);
    }

    @Override
    public PetResponse createPetAdmin(PetRequestAdmin petRequest) {
        User owner = userRepository.findByEmail(petRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with email: " + petRequest.getEmail()));

        Pet pet = modelMapper.map(petRequest, Pet.class);
        pet.setOwner(owner);

        Pet savedPet = petRepository.save(pet);
        return modelMapper.map(savedPet, PetResponse.class);
    }


    @Override
    public PetResponse getPetById(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            throw new ResourceNotFoundException("User isn't authenticated");
        }
        String userEmail = authentication.getName();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("Admin"));

        if (!isAdmin && !pet.getOwner().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("Access denied: you are not allowed to view this pet");
        }

        return modelMapper.map(pet, PetResponse.class);
    }

    // метод для администрации
    @Override
    public List<PetResponse> getAllPets() {
        return petRepository.findAll()
                .stream()
                .map(pet -> modelMapper.map(pet, PetResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<PetResponse> getAllPetsByPrincipalOwner() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new ResourceNotFoundException("User isn't authenticated");
        }
        String ownerEmail = authentication.getName();

        List<Pet> pets = petRepository.findAllByOwnerEmail(ownerEmail);
        return pets
                .stream()
                .map(pet -> modelMapper.map(pet, PetResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public PetResponse editPet(Long id, PetRequest petRequest) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            throw new ResourceNotFoundException("User isn't authenticated");
        }
        String userEmail = authentication.getName();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !pet.getOwner().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("Access denied: you are not allowed to edit this pet");
        }

        modelMapper.map(petRequest, pet);
        Pet updatedPet = petRepository.save(pet);
        return modelMapper.map(updatedPet, PetResponse.class);
    }

    @Override
    public boolean deletePet(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new ResourceNotFoundException("User isn't authenticated");
        }
        String userEmail = authentication.getName();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("Admin"));

        if (!isAdmin && !pet.getOwner().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("Access denied: you are not allowed to delete this pet");
        }

        petRepository.deleteById(id);
        return true;
    }
}
