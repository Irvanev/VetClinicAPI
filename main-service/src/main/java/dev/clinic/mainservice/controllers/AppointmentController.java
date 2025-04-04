package dev.clinic.mainservice.controllers;

import dev.clinic.mainservice.dtos.appointments.AppointmentRequest;
import dev.clinic.mainservice.dtos.appointments.AppointmentResponse;
import dev.clinic.mainservice.dtos.appointments.AppointmentResponseOwner;
import dev.clinic.mainservice.models.enums.AppointmentType;
import dev.clinic.mainservice.services.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@Tag(name = "Приемы", description = "Управление приёмами пациентов")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Operation(
            summary = "Создать приём",
            description = "Создаёт новый приём для текущего пользователя",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Приём успешно создан"),
                    @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
                    @ApiResponse(responseCode = "404", description = "Ресурс не найден")
            }
    )
    @PostMapping
    public ResponseEntity<Void> createAppointment(
            @Parameter(description = "Данные для создания приёма", required = true)
            @Valid @RequestBody AppointmentRequest request
    ) {
        appointmentService.createAppointment(request);
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Получить приём по ID",
            description = "Возвращает детальную информацию о конкретном приёме",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение данных"),
                    @ApiResponse(responseCode = "404", description = "Приём не найден")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getAppointment(
            @Parameter(description = "ID приёма", required = true)
            @PathVariable Long id
    ) {
        return new ResponseEntity<>(appointmentService.getAppointmentById(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Получить все приёмы владельца",
            description = "Возвращает список всех приёмов текущего пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение списка")
            }
    )
    @GetMapping("/owner")
    public ResponseEntity<List<AppointmentResponseOwner>> getOwnerAppointments() {
        return new ResponseEntity<>(appointmentService.getAllOwnerAppointments(), HttpStatus.OK);
    }

    @Operation(
            summary = "Получить доступные временные слоты",
            description = "Возвращает список свободного времени для записи",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение данных"),
                    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
            }
    )
    @GetMapping("/available-slots")
    public ResponseEntity<List<LocalTime>> getAvailableTimeSlots(
            @Parameter(description = "ID врача", required = true)
            @RequestParam Long doctorId,

            @Parameter(description = "Дата приёма (формат: YYYY-MM-DD)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,

            @Parameter(description = "Тип приёма", required = true)
            @RequestParam AppointmentType type
    ) {
        return new ResponseEntity<>(appointmentService.getAvailableTimeSlots(doctorId, date, type), HttpStatus.OK);
    }
}
