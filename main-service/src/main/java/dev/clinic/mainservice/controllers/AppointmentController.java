package dev.clinic.mainservice.controllers;

import dev.clinic.mainservice.dtos.appointments.AppointmentRequest;
import dev.clinic.mainservice.dtos.appointments.AppointmentResponse;
import dev.clinic.mainservice.dtos.appointments.AppointmentResponseOwner;
import dev.clinic.mainservice.services.AppointmentService;
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
@RequestMapping("/api/appointments")
@Tag(name = "Приемы", description = "Контроллер для над приемами")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    /**
     * Создание нового приема пользователем.
     * Прием привязывается к текущему пользователю.
     *
     * @param appointment объект с данными приема для создания
     * @return созданный прием
     */
    @Operation(
            summary = "Создание приема",
            description = "Создает новый прием и привязывает его к текущему пользователю."
    )
    @ApiResponse(responseCode = "201", description = "Прием успешно создан")
    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    @PostMapping("/create-appointment")
    public ResponseEntity<AppointmentResponse> createAppointment(
            @RequestBody @Parameter(description = "Данные приема для создания") AppointmentRequest appointment) {
        try {
            appointmentService.createAppointment(appointment);
            return new ResponseEntity<>(appointmentService.createAppointment(appointment), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Получение приема по его идентификатору.
     *
     * @param id идентификатор приема
     * @return найденный прием
     */
    @Operation(
            summary = "Получение приема по ID",
            description = "Возвращает информацию о приеме по его идентификатору."
    )
    @ApiResponse(responseCode = "200", description = "Прием найден")
    @ApiResponse(responseCode = "404", description = "Прием не найден")
    @GetMapping("/get-appointment/{id}")
    public ResponseEntity<AppointmentResponse> getAppointment(
            @PathVariable @Parameter(description = "Идентификатор приема") Long id) {
        try {
            return new ResponseEntity<>(appointmentService.getAppointmentById(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Получение списка всех приемов для текущего пользователя.
     *
     * @return список приемов
     */
    @Operation(
            summary = "Получение списка приемов",
            description = "Возвращает список всех приемов."
    )
    @ApiResponse(responseCode = "200", description = "Список приемов успешно получен")
    @ApiResponse(responseCode = "404", description = "Приемы не найдены")
    @GetMapping("/get-all-appointments-owner")
    public ResponseEntity<List<AppointmentResponseOwner>> getAllAppointmentsOwner() {
        try {
            return new ResponseEntity<>(appointmentService.getAllOwnerAppointments(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}
