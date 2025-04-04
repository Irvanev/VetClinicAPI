package dev.clinic.mainservice.controllers;

import dev.clinic.mainservice.dtos.medicalRecords.MedicalRecordRequest;
import dev.clinic.mainservice.dtos.medicalRecords.MedicalRecordResponse;
import dev.clinic.mainservice.services.MedicalRecordsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
@Tag(name = "История посещений", description = "Управление медицинскими посещениями животных")
public class MedicalRecordController {

    private final MedicalRecordsService medicalRecordsService;

    @Autowired
    public MedicalRecordController(MedicalRecordsService medicalRecordsService) {
        this.medicalRecordsService = medicalRecordsService;
    }

    @Operation(
            summary = "Создать посещение",
            description = "Создаёт новое посещение, привязанное к приёму, доктору, клиенту и питомцу",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Посещение успешно создано"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
            }
    )
    @PostMapping
    public ResponseEntity<Void> createMedicalRecord(
            @Parameter(description = "ID приёма, к которому относится посещение", required = true)
            @RequestParam Long appointmentId,

            @Parameter(description = "Данные для создания посещения", required = true)
            @Valid @RequestBody MedicalRecordRequest medicalRecordRequest
    ) {
        medicalRecordsService.createMedicalRecord(appointmentId, medicalRecordRequest);
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Получить посещение по ID",
            description = "Возвращает информацию о посещении по его идентификатору",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Посещение найдено"),
                    @ApiResponse(responseCode = "404", description = "Посещение не найдено")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecordResponse> getMedicalRecordById(
            @Parameter(description = "ID посещения", required = true)
            @PathVariable Long id
    ) {
        MedicalRecordResponse response = medicalRecordsService.getMedicalRecord(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Получить все посещения",
            description = "Возвращает список всех посещений",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список посещений успешно получен")
            }
    )
    @GetMapping
    public ResponseEntity<List<MedicalRecordResponse>> getAllMedicalRecords() {
        List<MedicalRecordResponse> responseList = medicalRecordsService.getAllMedicalRecords();
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    @Operation(
            summary = "Получить все посещения по ID питомца",
            description = "Возвращает список всех посещений, связанных с конкретным питомцем",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Посещения найдены"),
                    @ApiResponse(responseCode = "404", description = "Питомец не найден или нет посещений")
            }
    )
    @GetMapping("/by-pet/{petId}")
    public ResponseEntity<List<MedicalRecordResponse>> getAllMedicalRecordsByPetId(
            @Parameter(description = "ID питомца", required = true)
            @PathVariable Long petId
    ) {
        List<MedicalRecordResponse> responseList = medicalRecordsService.getAllMedicalRecordByPet(petId);
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    @Operation(
            summary = "Удалить посещение",
            description = "Удаляет посещение по его идентификатору",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Посещение успешно удалено"),
                    @ApiResponse(responseCode = "404", description = "Посещение не найдено")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicalRecord(
            @Parameter(description = "ID посещения", required = true)
            @PathVariable Long id
    ) {
        medicalRecordsService.deleteMedicalRecord(id);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }
}
