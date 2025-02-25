package dev.clinic.mainservice.controllers;

import dev.clinic.mainservice.dtos.pets.PetRequest;
import dev.clinic.mainservice.dtos.pets.PetResponse;
import dev.clinic.mainservice.services.PetService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pets")
public class PetController {

    private final PetService petService;

    @Autowired
    public PetController(PetService petService) {
        this.petService = petService;
    }

    /**
     * Создание нового питомца.
     * Питомец привязывается к текущему пользователю (хозяину).
     */
    @PostMapping
    public ResponseEntity<PetResponse> createPet(@RequestBody PetRequest petRequest) {
        PetResponse petResponse = petService.createPet(petRequest);
        return new ResponseEntity<>(petResponse, HttpStatus.CREATED);
    }

    /**
     * Получение питомца по его идентификатору.
     */
    @GetMapping("/{id}")
    public PetResponse getPetById(@PathVariable Long id) {
        return petService.getPetById(id);
    }

    /**
     * Получение списка всех питомцев.
     */
    @GetMapping
    public ResponseEntity<List<PetResponse>> getAllPets() {
        List<PetResponse> petResponses = petService.getAllPets();
        return ResponseEntity.ok(petResponses);
    }

    /**
     * Редактирование информации о питомце.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PetResponse> editPet(@PathVariable Long id, @RequestBody PetRequest petRequest) {
        try {
            PetResponse petResponse = petService.editPet(id, petRequest);
            return ResponseEntity.ok(petResponse);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Удаление питомца.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable Long id) {
        petService.deletePet(id);
        return ResponseEntity.noContent().build();
    }
}
