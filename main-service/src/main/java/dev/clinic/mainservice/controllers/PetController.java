package dev.clinic.mainservice.controllers;

import dev.clinic.mainservice.dtos.pets.PetRequest;
import dev.clinic.mainservice.dtos.pets.PetRequestAdmin;
import dev.clinic.mainservice.dtos.pets.PetResponse;
import dev.clinic.mainservice.services.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
@Tag(name = "Питомцы", description = "Управление питомцами")
public class PetController {

    private final PetService petService;

    @Autowired
    public PetController(PetService petService) {
        this.petService = petService;
    }

    @Operation(
            summary = "Создание питомца (владельцем)",
            description = "Создаёт нового питомца и привязывает его к текущему пользователю",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Питомец успешно создан"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные")
            }
    )
    @PostMapping
    public ResponseEntity<PetResponse> createPet(
            @Parameter(description = "Данные питомца", required = true)
            @RequestBody @Valid PetRequest petRequest
    ) {
        PetResponse response = petService.createPet(petRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Добавление фото питомцу после создания",
            description = "Добавляет фото к питомцу после его создания",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Фото успешно добавлено"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            }
    )
    @PostMapping(value = "/{petId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PetResponse> uploadPetPhoto(
            @PathVariable Long petId,
            @RequestPart("photo") MultipartFile photo
    ) {
        PetResponse response = petService.updatePetPhoto(petId, photo);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Создание питомца (администратором)",
            description = "Создаёт нового питомца и привязывает его к пользователю по email",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Питомец успешно создан"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные")
            }
    )
    @PostMapping("/admin")
    public ResponseEntity<PetResponse> createPetAdmin(
            @Parameter(description = "Данные питомца от администратора", required = true)
            @Valid @RequestBody PetRequestAdmin petRequest
    ) {
        PetResponse response = petService.createPetAdmin(petRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Получение питомца по ID",
            description = "Возвращает информацию о питомце по его идентификатору",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Питомец найден"),
                    @ApiResponse(responseCode = "404", description = "Питомец не найден")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<PetResponse> getPetById(
            @Parameter(description = "ID питомца", required = true)
            @PathVariable Long id
    ) {
        PetResponse response = petService.getPetById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Получение всех питомцев",
            description = "Возвращает список всех питомцев в системе",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список получен")
            }
    )
    @GetMapping
    public ResponseEntity<List<PetResponse>> getAllPets() {
        List<PetResponse> responses = petService.getAllPets();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Operation(
            summary = "Получение питомцев текущего пользователя",
            description = "Возвращает всех питомцев, принадлежащих текущему пользователю",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список питомцев успешно получен"),
                    @ApiResponse(responseCode = "404", description = "Питомцы не найдены")
            }
    )
    @GetMapping("/owner")
    public ResponseEntity<List<PetResponse>> getAllPetsByPrincipalOwner() {
        List<PetResponse> responses = petService.getAllPets();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Operation(
            summary = "Редактировать питомца",
            description = "Обновляет информацию о питомце по ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Питомец обновлён"),
                    @ApiResponse(responseCode = "404", description = "Питомец не найден")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<PetResponse> editPet(
            @Parameter(description = "ID питомца", required = true)
            @PathVariable Long id,

            @Parameter(description = "Обновлённые данные питомца", required = true)
            @Valid @RequestBody PetRequest petRequest
    ) {
        PetResponse response = petService.editPet(id, petRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Удалить питомца",
            description = "Удаляет питомца по его идентификатору",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Питомец успешно удалён"),
                    @ApiResponse(responseCode = "404", description = "Питомец не найден")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(
            @Parameter(description = "ID питомца", required = true)
            @PathVariable Long id
    ) {
        petService.deletePet(id);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }
}
