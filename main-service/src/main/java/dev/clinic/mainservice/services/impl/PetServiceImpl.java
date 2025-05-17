package dev.clinic.mainservice.services.impl;

import dev.clinic.mainservice.dtos.pets.PetRequest;
import dev.clinic.mainservice.dtos.pets.PetRequestAdmin;
import dev.clinic.mainservice.dtos.pets.PetResponse;
import dev.clinic.mainservice.exceptions.ResourceNotFoundException;
import dev.clinic.mainservice.mapping.PetMapper;
import dev.clinic.mainservice.models.entities.Client;
import dev.clinic.mainservice.models.entities.Pet;
import dev.clinic.mainservice.repositories.ClientRepository;
import dev.clinic.mainservice.repositories.PetRepository;
import dev.clinic.mainservice.services.ImageUploaderService;
import dev.clinic.mainservice.services.PetService;
import dev.clinic.mainservice.utils.AuthUtil;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;
    private final ImageUploaderService imageUploaderService;
    private final AuthUtil authUtil;
    private final ClientRepository clientRepository;

    private static final Logger log = LoggerFactory.getLogger(PetServiceImpl.class);

    public PetServiceImpl(
            PetRepository petRepository,
            ImageUploaderService imageUploaderService,
            AuthUtil authUtil,
            ClientRepository clientRepository
    ) {
        this.petRepository = petRepository;
        this.imageUploaderService = imageUploaderService;
        this.authUtil = authUtil;
        this.clientRepository = clientRepository;
    }

    /**
     * Создание питомца авторизированным клиентом
     * @param petRequest объект для создания питомца
     * @return возвращает созданного питомца
     */
    @Override
    @CacheEvict(value = "pets", allEntries = true)
    public PetResponse createPet(PetRequest petRequest) {
        log.info("Create pet was called");
        try {
            String ownerEmail = authUtil.getPrincipalEmail();
            log.debug("User is authenticated with email: {}", ownerEmail);
            Client owner = clientRepository.findByEmail(ownerEmail)
                    .orElseThrow(() -> {
                        log.warn("Owner not found with email: {}", ownerEmail);
                        return new ResourceNotFoundException("Owner not found with email: " + ownerEmail);
                    });

            Pet pet = PetMapper.fromRequest(petRequest, owner);

            Pet saved = petRepository.save(pet);
            log.info("Pet was created with id: {}", saved.getId());

            return PetMapper.toResponse(saved);

        } catch (ResourceNotFoundException | IllegalArgumentException ex) {
            log.error("Validation error in createPet: {}", ex.getMessage());
            throw ex;
        } catch (DataAccessException ex) {
            log.error("Database error in createPet", ex);
            throw new ServiceException("Database error while creating pet", ex);
        } catch (Exception ex) {
            log.error("Unexpected error in createPet", ex);
            throw new ServiceException("Unexpected error while creating pet", ex);
        }
    }

    /**
     * Создание питомца администратором
     * @param petRequest объект для создания питомца
     * @return возвращает созданного питомца
     */
    @Override
    @CacheEvict(value = "pets", allEntries = true)
    public PetResponse createPetAdmin(PetRequestAdmin petRequest) {
        log.info("Create pet by admin was called");
        try {
            Client owner = clientRepository.findByEmail(petRequest.getEmail())
                    .orElseThrow(() -> {
                        log.warn("Owner not found: {}", petRequest.getEmail());
                        return new ResourceNotFoundException("Owner not found with email: " + petRequest.getEmail());
                    });

            Pet pet = PetMapper.fromRequestByAdmin(petRequest, owner);

            Pet saved = petRepository.save(pet);
            log.info("Pet was created by admin with id: {}", saved.getId());

            return PetMapper.toResponse(saved);

        } catch (ResourceNotFoundException ex) {
            log.error("Validation error in createPetAdmin: {}", ex.getMessage());
            throw ex;
        } catch (DataAccessException ex) {
            log.error("Database error in createPetAdmin", ex);
            throw new ServiceException("Database error while creating pet as admin", ex);
        } catch (Exception ex) {
            log.error("Unexpected error in createPetAdmin", ex);
            throw new ServiceException("Unexpected error while creating pet as admin", ex);
        }
    }

    /**
     * Добавление фото питомцу
     * @param petId уникальный идентификатор питомца
     * @param photo объект для добавление фото питомца
     * @return возвращает питомца
     */
    @Override
    @CacheEvict(value = "pets", key = "#petId", allEntries = true)
    public PetResponse updatePetPhoto(Long petId, MultipartFile photo) {
        log.info("Add pet's photo was called");
        try {
            if (photo == null || photo.isEmpty()) {
                log.warn("No photo provided for petId = {}", petId);
                throw new IllegalArgumentException("Photo must be provided");
            }
            String contentType = photo.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                log.warn("Invalid file type {} for petId = {}", contentType, petId);
                throw new IllegalArgumentException("Invalid file type. Only images are allowed");
            }

            Pet pet = petRepository.findById(petId)
                    .orElseThrow(() -> {
                        log.warn("Pet not found with id = {}", petId);
                        return new ResourceNotFoundException("Pet not found with id: " + petId);
                    });

            String photoUrl;
            try {
                photoUrl = imageUploaderService.uploadImage(photo);
                log.debug("Photo uploaded to URL = {}", photoUrl);
            } catch (IOException ex) {
                log.error("IOException in uploadImage for petId = {}", petId, ex);
                throw new ServiceException("Image upload failed", ex);
            }

            pet.setPhotoUrl(photoUrl);
            Pet updated = petRepository.save(pet);
            log.info("Pet's photo was added for pet id = {}", updated.getId());

            return PetMapper.toResponse(updated);

        } catch (ResourceNotFoundException | IllegalArgumentException ex) {
            log.error("Validation error in updatePetPhoto: {}", ex.getMessage());
            throw ex;
        } catch (DataAccessException ex) {
            log.error("Database error in updatePetPhoto for petId = {}", petId, ex);
            throw new ServiceException("Database error while updating pet photo", ex);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error in updatePetPhoto for petId = {}", petId, ex);
            throw new ServiceException("Unexpected error while updating pet photo", ex);
        }
    }

    /**
     * Получения конкретного питомца
     * @param id уникальный идентификатор питомца
     * @return возвращает полученного питомца
     */
    @Override
    public PetResponse getPetById(Long id) {
        log.info("Get pet by id was called");
        try {
            Pet pet = petRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Pet not found with id = {}", id);
                        return new ResourceNotFoundException("Pet not found with id: " + id);
                    });

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() ||
                    authentication.getPrincipal().equals("anonymousUser")) {
                log.warn("User is not authenticated");
                throw new ResourceNotFoundException("User isn't authenticated");
            }
            String userEmail = authentication.getName();
            log.debug("User is authenticated with email: {}", userEmail);

            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("Admin"));

            if (!isAdmin && !pet.getOwner().getEmail().equals(userEmail)) {
                log.warn("Access denied for user '{}' on pet id={}", userEmail, id);
                throw new AccessDeniedException("Access denied: you are not allowed to view this pet");
            }

            log.info("Pet found with id {}", pet.getId());
            return PetMapper.toResponse(pet);

        } catch (ResourceNotFoundException | AccessDeniedException ex) {
            log.error("Error in getPetById: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error in getPetById for id = {}", id, ex);
            throw new ServiceException("Unexpected error while retrieving pet", ex);
        }
    }

    /**
     * Получения списка всех питомцев
     * @return возвращает полученный список питомцев
     */
    @Override
    @Cacheable(value = "pets", key = "'all'")
    public List<PetResponse> getAllPets() {
        log.info("Get all pets was called");
        try {
            List<PetResponse> result = petRepository.findAll().stream()
                    .map(PetMapper::toResponse)
                    .collect(Collectors.toList());
            log.debug("Fetched {} pets", result.size());

            return result;

        } catch (DataAccessException ex) {
            log.error("Database error in getAllPets", ex);
            throw new ServiceException("Database error while fetching all pets", ex);
        } catch (Exception ex) {
            log.error("Unexpected error in getAllPets", ex);
            throw new ServiceException("Unexpected error while fetching all pets", ex);
        }
    }

    /**
     * Получения списка всех питомцев для авторизированого пользователя (для владельца)
     * @return возвращает полученный список питомцев
     */
    @Override
    @Cacheable(value = "pets", key = "'all'")
    public List<PetResponse> getAllPetsByPrincipalOwner() {
        log.info("Get all pets by principal owner was called");
        try {
            String ownerEmail = authUtil.getPrincipalEmail();
            log.debug("User is authenticated with email: {}", ownerEmail);

            List<PetResponse> result = petRepository.findAllByOwnerEmail(ownerEmail).stream()
                    .map(PetMapper::toResponse)
                    .collect(Collectors.toList());
            log.debug("Fetched {} pets for owner {}", result.size(), ownerEmail);

            return result;

        } catch (Exception ex) {
            log.error("Unexpected error in getAllPetsByPrincipalOwner", ex);
            throw new ServiceException("Unexpected error while fetching your pets", ex);
        }
    }

    /**
     * Редактирование питомца
     * @param petRequest объект для редактирования питомца
     * @param id уникальный идентификатор питомца
     * @return возвращает отредактированного питомца
     */
    @Override
    @CacheEvict(value = "pets", allEntries = true)
    public PetResponse editPet(Long id, PetRequest petRequest) {
        log.info("Edit pet by id was called");
        try {
            Pet pet = petRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Pet not found with id = {}", id);
                        return new ResourceNotFoundException("Pet not found with id: " + id);
                    });

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() ||
                    authentication.getPrincipal().equals("anonymousUser")) {
                log.warn("User is not authenticated");
                throw new ResourceNotFoundException("User isn't authenticated");
            }
            String userEmail = authentication.getName();
            log.debug("User is authenticated with email: {}", userEmail);

            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("Admin"));

            if (!isAdmin && !pet.getOwner().getEmail().equals(userEmail)) {
                log.warn("Access denied for user '{}' on editPet id={}", userEmail, id);
                throw new AccessDeniedException("Access denied: you are not allowed to edit this pet");
            }

            PetMapper.updateFromRequest(pet, petRequest);

            Pet updatedPet = petRepository.save(pet);
            log.info("Pet edited with id {}", updatedPet.getId());

            return PetMapper.toResponse(updatedPet);

        } catch (ResourceNotFoundException | AccessDeniedException | IllegalArgumentException ex) {
            log.error("Error in editPet: {}", ex.getMessage());
            throw ex;
        } catch (DataAccessException ex) {
            log.error("Database error in editPet for id = {}", id, ex);
            throw new ServiceException("Database error while editing pet", ex);
        } catch (Exception ex) {
            log.error("Unexpected error in editPet for id = {}", id, ex);
            throw new ServiceException("Unexpected error while editing pet", ex);
        }
    }

    /**
     * Удаление питомца
     * @param id уникальный идентификатор питомца
     */
    @Override
    @CacheEvict(value = "pets", allEntries = true)
    public void deletePet(Long id) {
        log.info("Delete pet by id was called");
        try {
            Pet pet = petRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Pet not found with id = {}", id);
                        return new ResourceNotFoundException("Pet not found with id: " + id);
                    });

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
                log.warn("User is not authenticated");
                throw new ResourceNotFoundException("User isn't authenticated");
            }
            String userEmail = authentication.getName();
            log.debug("User is authenticated with email: {}", userEmail);

            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("Admin"));

            if (!isAdmin && !pet.getOwner().getEmail().equals(userEmail)) {
                log.warn("Access denied for user '{}' on deletePet id={}", userEmail, id);
                throw new AccessDeniedException("Access denied: you are not allowed to delete this pet");
            }

            petRepository.deleteById(id);
            log.info("Pet deleted with id {}", id);

        } catch (ResourceNotFoundException | AccessDeniedException ex) {
            log.error("Error in deletePet: {}", ex.getMessage());
            throw ex;
        } catch (DataAccessException ex) {
            log.error("Database error in deletePet for id = {}", id, ex);
            throw new ServiceException("Database error while deleting pet", ex);
        } catch (Exception ex) {
            log.error("Unexpected error in deletePet for id = {}", id, ex);
            throw new ServiceException("Unexpected error while deleting pet", ex);
        }
    }
}
