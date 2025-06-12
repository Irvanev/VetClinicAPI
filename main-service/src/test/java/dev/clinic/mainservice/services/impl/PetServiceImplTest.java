package dev.clinic.mainservice.services.impl;

import dev.clinic.mainservice.dtos.pets.PetRequest;
import dev.clinic.mainservice.dtos.pets.PetResponse;
import dev.clinic.mainservice.exceptions.ResourceNotFoundException;
import dev.clinic.mainservice.mapping.PetMapper;
import dev.clinic.mainservice.models.entities.Client;
import dev.clinic.mainservice.models.entities.Pet;
import dev.clinic.mainservice.repositories.ClientRepository;
import dev.clinic.mainservice.repositories.PetRepository;
import dev.clinic.mainservice.services.ImageUploaderService;
import dev.clinic.mainservice.utils.AuthUtil;
import org.hibernate.service.spi.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PetServiceImplTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private ImageUploaderService imageUploaderService;

    @Mock
    private AuthUtil authUtil;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private PetServiceImpl petService;

    private static final String TEST_EMAIL = "test@example.com";
    private PetRequest petRequest;
    private Client owner;
    private Pet pet;
    private Pet savedPet;
    private PetResponse expectedResponse;

    @BeforeEach
    void setUp() {
        petRequest = new PetRequest();
        petRequest.setName("Fluffy");
        petRequest.setAnimalType("Cat");
        petRequest.setBreed("Persian");

        owner = new Client();
        owner.setId(1L);
        owner.setEmail("test@example.com");
        owner.setFirstName("Test");
        owner.setLastName("User");

        pet = new Pet();
        pet.setName("Fluffy");
        pet.setAnimalType("Cat");
        pet.setBreed("Persian");
        pet.setOwner(owner);

        savedPet = new Pet();
        savedPet.setId(1L);
        savedPet.setName("Fluffy");
        savedPet.setAnimalType("Cat");
        savedPet.setBreed("Persian");
        savedPet.setOwner(owner);

        expectedResponse = new PetResponse();
        expectedResponse.setId(1L);
        expectedResponse.setName("Fluffy");
        expectedResponse.setAnimalType("Cat");
        expectedResponse.setBreed("Persian");
    }

    @Test
    void createPet_Success() {
        when(authUtil.getPrincipalEmail()).thenReturn(TEST_EMAIL);
        when(clientRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(owner));
        when(petRepository.save(any(Pet.class))).thenReturn(savedPet);

        try (MockedStatic<PetMapper> petMapperMock = mockStatic(PetMapper.class)) {
            petMapperMock.when(() -> PetMapper.fromRequest(petRequest, owner)).thenReturn(pet);
            petMapperMock.when(() -> PetMapper.toResponse(savedPet)).thenReturn(expectedResponse);

            PetResponse result = petService.createPet(petRequest);

            assertNotNull(result);
            assertEquals(expectedResponse.getId(), result.getId());
            assertEquals(expectedResponse.getName(), result.getName());
            assertEquals(expectedResponse.getAnimalType(), result.getAnimalType());
            assertEquals(expectedResponse.getBreed(), result.getBreed());

            verify(authUtil).getPrincipalEmail();
            verify(clientRepository).findByEmail(TEST_EMAIL);
            verify(petRepository).save(pet);
            petMapperMock.verify(() -> PetMapper.fromRequest(petRequest, owner));
            petMapperMock.verify(() -> PetMapper.toResponse(savedPet));
        }
    }

    @Test
    void createPet_OwnerNotFound_ThrowsResourceNotFoundException() {
        when(authUtil.getPrincipalEmail()).thenReturn(TEST_EMAIL);
        when(clientRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> petService.createPet(petRequest));

        assertEquals("Owner not found with email: " + TEST_EMAIL, exception.getMessage());

        verify(authUtil).getPrincipalEmail();
        verify(clientRepository).findByEmail(TEST_EMAIL);
        verify(petRepository, never()).save(any());
    }

    @Test
    void createPet_DatabaseError_ThrowsServiceException() {
        when(authUtil.getPrincipalEmail()).thenReturn(TEST_EMAIL);
        when(clientRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(owner));
        DataAccessException dataException = new DataAccessException("Database error") {};
        when(petRepository.save(any(Pet.class))).thenThrow(dataException);

        try (MockedStatic<PetMapper> petMapperMock = mockStatic(PetMapper.class)) {
            petMapperMock.when(() -> PetMapper.fromRequest(petRequest, owner)).thenReturn(pet);

            ServiceException exception = assertThrows(ServiceException.class,
                    () -> petService.createPet(petRequest));

            assertEquals("Database error while creating pet", exception.getMessage());
            assertEquals(dataException, exception.getCause());

            verify(authUtil).getPrincipalEmail();
            verify(clientRepository).findByEmail(TEST_EMAIL);
            verify(petRepository).save(pet);
        }
    }

    @Test
    void createPet_UnexpectedError_ThrowsServiceException() {
        when(authUtil.getPrincipalEmail()).thenReturn(TEST_EMAIL);
        when(clientRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(owner));
        RuntimeException unexpectedException = new RuntimeException("Unexpected error");

        try (MockedStatic<PetMapper> petMapperMock = mockStatic(PetMapper.class)) {
            petMapperMock.when(() -> PetMapper.fromRequest(petRequest, owner)).thenThrow(unexpectedException);

            ServiceException exception = assertThrows(ServiceException.class,
                    () -> petService.createPet(petRequest));

            assertEquals("Unexpected error while creating pet", exception.getMessage());
            assertEquals(unexpectedException, exception.getCause());

            verify(authUtil).getPrincipalEmail();
            verify(clientRepository).findByEmail(TEST_EMAIL);
            verify(petRepository, never()).save(any());
        }
    }

    @Test
    void createPet_ValidationError_ThrowsIllegalArgumentException() {
        when(authUtil.getPrincipalEmail()).thenReturn(TEST_EMAIL);
        when(clientRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(owner));
        IllegalArgumentException validationException = new IllegalArgumentException("Invalid pet data");

        try (MockedStatic<PetMapper> petMapperMock = mockStatic(PetMapper.class)) {
            petMapperMock.when(() -> PetMapper.fromRequest(petRequest, owner)).thenThrow(validationException);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> petService.createPet(petRequest));

            assertEquals("Invalid pet data", exception.getMessage());

            verify(authUtil).getPrincipalEmail();
            verify(clientRepository).findByEmail(TEST_EMAIL);
            verify(petRepository, never()).save(any());
        }
    }
}
