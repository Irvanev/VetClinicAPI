package dev.clinic.mainservice.controllers;

import dev.clinic.mainservice.dtos.pets.PetRequest;
import dev.clinic.mainservice.dtos.pets.PetRequestAdmin;
import dev.clinic.mainservice.dtos.pets.PetResponse;
import dev.clinic.mainservice.services.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
@Tag(name = "Питомцы", description = "Контроллер для управления питомцами")
public class PetController {

    private final PetService petService;

    @Autowired
    public PetController(PetService petService) {
        this.petService = petService;
    }

    /**
     * Создание нового питомца.
     * Питомец привязывается к текущему пользователю (хозяину).
     *
     * @param petRequest объект с данными питомца для создания
     * @return созданный питомец
     */
    @Operation(
            summary = "Создание питомца",
            description = "Создает нового питомца и привязывает его к текущему пользователю."
    )
    @ApiResponse(responseCode = "201", description = "Питомец успешно создан")
    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    @PostMapping("/create")
    public ResponseEntity<PetResponse> createPet(
            @RequestBody @Parameter(description = "Данные питомца для создания") PetRequest petRequest) {
        PetResponse petResponse = petService.createPet(petRequest);
        return new ResponseEntity<>(petResponse, HttpStatus.CREATED);
    }

    /**
     * Создание нового питомца администратором.
     * Питомец привязывается к хозяину по его почте.
     *
     * @param petRequest объект с данными питомца для создания
     * @return созданный питомец
     */
    @Operation(
            summary = "Создание питомца",
            description = "Создает нового питомца и привязывает его к текущему пользователю."
    )
    @ApiResponse(responseCode = "201", description = "Питомец успешно создан")
    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    @PostMapping("/admin/create")
    public ResponseEntity<PetResponse> createPetAdmin(
            @RequestBody @Parameter(description = "Данные питомца для создания") PetRequestAdmin petRequest) {
        PetResponse petResponse = petService.createPetAdmin(petRequest);
        return new ResponseEntity<>(petResponse, HttpStatus.CREATED);
    }

    /**
     * Получение питомца по его идентификатору.
     *
     * @param id идентификатор питомца
     * @return найденный питомец
     */
    @Operation(
            summary = "Получение питомца по ID",
            description = "Возвращает информацию о питомце по его идентификатору."
    )
    @ApiResponse(responseCode = "200", description = "Питомец найден")
    @ApiResponse(responseCode = "404", description = "Питомец не найден")
    @GetMapping("/{id}")
    public PetResponse getPetById(
            @PathVariable @Parameter(description = "Идентификатор питомца") Long id) {
        return petService.getPetById(id);
    }

    /**
     * Получение списка всех питомцев.
     *
     * @return список питомцев
     */
    @Operation(
            summary = "Получение списка питомцев",
            description = "Возвращает список всех питомцев."
    )
    @ApiResponse(responseCode = "200", description = "Список питомцев успешно получен")
    @GetMapping("/get-all-pets")
    public ResponseEntity<List<PetResponse>> getAllPets() {
        try {
            List<PetResponse> petResponses = petService.getAllPets();
            return ResponseEntity.ok(petResponses);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Получение списка всех питомцев текущего пользователя.
     *
     * @return список питомцев
     */
    @Operation(
            summary = "Получение списка питомцев текущего пользователя",
            description = "Возвращает список всех питомцев текущего пользователя."
    )
    @ApiResponse(responseCode = "200", description = "Список питомцев успешно получен")
    @GetMapping("/get-all-owner-pets")
    public ResponseEntity<List<PetResponse>> getAllPetsByPrincipalOwner() {
        try {
            List<PetResponse> petResponses = petService.getAllPets();
            return ResponseEntity.ok(petResponses);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Редактирование информации о питомце.
     *
     * @param id идентификатор питомца
     * @param petRequest объект с обновленными данными питомца
     * @return обновленный питомец
     */

    @Operation(
            summary = "Редактирование питомца",
            description = "Обновляет информацию о питомце по его идентификатору."
    )
    @ApiResponse(responseCode = "200", description = "Питомец успешно обновлен")
    @ApiResponse(responseCode = "404", description = "Питомец не найден")
    @PutMapping("/{id}")
    public ResponseEntity<PetResponse> editPet(
            @PathVariable @Parameter(description = "Идентификатор питомца") Long id,
            @RequestBody @Parameter(description = "Обновленные данные питомца") PetRequest petRequest) {
        try {
            PetResponse petResponse = petService.editPet(id, petRequest);
            return ResponseEntity.ok(petResponse);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Удаление питомца.
     *
     * @param id идентификатор питомца
     * @return статус успешного удаления
     */
    @Operation(
            summary = "Удаление питомца",
            description = "Удаляет питомца по его идентификатору."
    )
    @ApiResponse(responseCode = "204", description = "Питомец успешно удален")
    @ApiResponse(responseCode = "404", description = "Питомец не найден")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(
            @PathVariable @Parameter(description = "Идентификатор питомца") Long id) {
        petService.deletePet(id);
        return ResponseEntity.noContent().build();
    }
}
