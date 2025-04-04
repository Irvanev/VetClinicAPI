package dev.clinic.mainservice.services;

import dev.clinic.mainservice.dtos.pets.PetRequestAdmin;
import dev.clinic.mainservice.dtos.pets.PetResponse;
import dev.clinic.mainservice.dtos.pets.PetRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PetService {
    PetResponse createPet(PetRequest petRequest, MultipartFile photo);
    PetResponse createPetAdmin(PetRequestAdmin createPetDto);
    PetResponse getPetById(Long id);
    List<PetResponse> getAllPets();
    List<PetResponse> getAllPetsByPrincipalOwner();
    PetResponse editPet(Long id, PetRequest createPetDto);
    boolean deletePet(Long id);
}
