package dev.clinic.mainservice.services;

import dev.clinic.mainservice.dtos.pets.PetResponse;
import dev.clinic.mainservice.dtos.pets.PetRequest;

import java.util.List;

public interface PetService {
    PetResponse createPet(PetRequest createPetDto);
    PetResponse getPetById(Long id);
    List<PetResponse> getAllPets();
    PetResponse editPet(Long id, PetRequest createPetDto);
    boolean deletePet(Long id);
}
