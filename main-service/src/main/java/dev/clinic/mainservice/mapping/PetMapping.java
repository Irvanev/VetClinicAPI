package dev.clinic.mainservice.mapping;

import dev.clinic.mainservice.dtos.pets.PetRequest;
import dev.clinic.mainservice.dtos.pets.PetResponse;
import dev.clinic.mainservice.models.entities.Client;
import dev.clinic.mainservice.models.entities.Pet;

public class PetMapping {
    public static Pet toRequest(PetRequest petRequest) {
        if (petRequest == null) {
            return null;
        }
        Pet pet = new Pet();
        Client owner = new Client();
        
        pet.setBirthDate(petRequest.getBirthDate());
        pet.setName(petRequest.getName());
        pet.setBreed(petRequest.getBreed());
        pet.setOwner(owner);

        return pet;
    }

    public static PetResponse toResponse(Pet pet) {
        if (pet == null) {
            return null;
        }
        PetResponse petResponse = new PetResponse();
        petResponse.setId(pet.getId());
        petResponse.setName(pet.getName());
        petResponse.setBreed(pet.getBreed());

        return petResponse;
    }
}
