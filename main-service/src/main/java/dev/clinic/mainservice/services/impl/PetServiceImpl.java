package dev.clinic.mainservice.services.impl;

import dev.clinic.mainservice.dtos.pets.PetRequest;
import dev.clinic.mainservice.dtos.pets.PetRequestAdmin;
import dev.clinic.mainservice.dtos.pets.PetResponse;
import dev.clinic.mainservice.exceptions.ResourceNotFoundException;
import dev.clinic.mainservice.models.entities.Pet;
import dev.clinic.mainservice.models.entities.User;
import dev.clinic.mainservice.repositories.PetRepository;
import dev.clinic.mainservice.repositories.UserRepository;
import dev.clinic.mainservice.services.PetService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PetServiceImpl implements PetService {

    private final ModelMapper modelMapper;
    private final PetRepository petRepository;
    private final UserRepository userRepository;

    @Autowired
    public PetServiceImpl(ModelMapper modelMapper, PetRepository petRepository, UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.petRepository = petRepository;
        this.userRepository = userRepository;
    }

    @Override
    public PetResponse createPet(PetRequest petRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new ResourceNotFoundException("User isn't authenticated");
        }
        String ownerEmail = authentication.getName();

        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with email: " + ownerEmail));

        Pet pet = modelMapper.map(petRequest, Pet.class);
        pet.setOwner(owner);


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
        // Находим питомца по id
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));

        // Получаем аутентификационные данные текущего пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            throw new ResourceNotFoundException("User isn't authenticated");
        }
        String userEmail = authentication.getName();

        // Проверяем, имеет ли пользователь роль ADMIN
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("Admin"));

        // Если пользователь не администратор, проверяем, что он является владельцем питомца
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
        // Получаем питомца по id
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));

        // Получаем данные об аутентифицированном пользователе
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            throw new ResourceNotFoundException("User isn't authenticated");
        }
        String userEmail = authentication.getName();

        // Проверяем, имеет ли пользователь роль ADMIN
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        // Если пользователь не администратор, проверяем, что он является владельцем питомца
        if (!isAdmin && !pet.getOwner().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("Access denied: you are not allowed to edit this pet");
        }

        // Выполняем обновление данных питомца
        modelMapper.map(petRequest, pet);
        Pet updatedPet = petRepository.save(pet);
        return modelMapper.map(updatedPet, PetResponse.class);
    }

    @Override
    public boolean deletePet(Long id) {
        // Получаем питомца по id
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));

        // Получаем информацию об аутентифицированном пользователе
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new ResourceNotFoundException("User isn't authenticated");
        }
        String userEmail = authentication.getName();

        // Проверяем, имеет ли пользователь роль ADMIN
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));

        // Если пользователь не администратор, проверяем, что он является владельцем питомца
        if (!isAdmin && !pet.getOwner().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("Access denied: you are not allowed to delete this pet");
        }

        petRepository.deleteById(id);
        return true;
    }
}
