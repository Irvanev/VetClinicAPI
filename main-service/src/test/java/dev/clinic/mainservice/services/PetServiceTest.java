package dev.clinic.mainservice.services;

import dev.clinic.mainservice.dtos.pets.PetRequest;
import dev.clinic.mainservice.dtos.pets.PetResponse;
import dev.clinic.mainservice.models.entities.Client;
import dev.clinic.mainservice.models.entities.Pet;
import dev.clinic.mainservice.repositories.ClientRepository;
import dev.clinic.mainservice.repositories.PetRepository;
import dev.clinic.mainservice.services.impl.PetServiceImpl;
import dev.clinic.mainservice.utils.AuthUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AuthUtil authUtil;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PetServiceImpl petService;

    @Test
    void createPet() {
        String ownerEmail = "test@example.com";
        PetRequest petRequest = new PetRequest();
        petRequest.setName("Buddy");
        petRequest.setAnimalType("Dog");

        Client owner = new Client();
        owner.setEmail(ownerEmail);

        Pet pet = new Pet();
        pet.setName("Buddy");
        pet.setAnimalType("Dog");
        pet.setOwner(owner);

        Pet savedPet = new Pet();
        savedPet.setId(1L);
        savedPet.setName("Buddy");
        savedPet.setAnimalType("Dog");
        savedPet.setOwner(owner);

        PetResponse expectedResponse = new PetResponse();
        expectedResponse.setId(1L);
        expectedResponse.setName("Buddy");
        expectedResponse.setAnimalType("Dog");

        when(authUtil.getPrincipalEmail()).thenReturn(ownerEmail);
        when(clientRepository.findByEmail(ownerEmail)).thenReturn(Optional.of(owner));
        when(modelMapper.map(petRequest, Pet.class)).thenReturn(pet);
        when(petRepository.save(pet)).thenReturn(savedPet);
        when(modelMapper.map(savedPet, PetResponse.class)).thenReturn(expectedResponse);

        PetResponse actualResponse = petService.createPet(petRequest);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        assertEquals(expectedResponse.getAnimalType(), actualResponse.getAnimalType());

        verify(authUtil).getPrincipalEmail();
        verify(clientRepository).findByEmail(ownerEmail);
        verify(petRepository).save(pet);
        verify(modelMapper).map(petRequest, Pet.class);
        verify(modelMapper).map(savedPet, PetResponse.class);
    }

    @Test
    void getAllPetsByOwner() {

    }
}
