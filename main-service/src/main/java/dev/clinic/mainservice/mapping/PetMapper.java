package dev.clinic.mainservice.mapping;

import dev.clinic.mainservice.dtos.pets.PetRequest;
import dev.clinic.mainservice.dtos.pets.PetRequestAdmin;
import dev.clinic.mainservice.dtos.pets.PetResponse;
import dev.clinic.mainservice.models.entities.Client;
import dev.clinic.mainservice.models.entities.Pet;

public class PetMapper {
    public static Pet fromRequest(PetRequest petRequest, Client owner) {
        if (petRequest == null || owner == null) {
            return null;
        }
        Pet pet = new Pet();
        pet.setBirthDate(petRequest.getBirthDate());
        pet.setName(petRequest.getName());
        pet.setBreed(petRequest.getBreed());
        pet.setOwner(owner);

        return pet;
    }

    public static Pet fromRequestByAdmin(PetRequestAdmin petRequest, Client owner) {
        if (petRequest == null || owner == null) {
            return null;
        }
        Pet pet = new Pet();
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
        petResponse.setBirthDate(pet.getBirthDate());
        petResponse.setBreed(pet.getBreed());
        petResponse.setAnimalType(pet.getAnimalType());
        petResponse.setPhotoUrl(pet.getPhotoUrl());

        return petResponse;
    }

    public static void updateFromRequest(Pet pet, PetRequest request) {
        if (request.getName() != null) {
            pet.setName(request.getName());
        }
        if (request.getBreed() != null) {
            pet.setBreed(request.getBreed());
        }
        if (request.getBirthDate() != null) {
            pet.setBirthDate(request.getBirthDate());
        }
        if (request.getAnimalType() != null) {
            pet.setAnimalType(request.getAnimalType());
        }
    }
}
