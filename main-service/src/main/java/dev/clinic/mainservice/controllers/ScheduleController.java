package dev.clinic.mainservice.controllers;

import dev.clinic.mainservice.dtos.schedule.ScheduleRequest;
import dev.clinic.mainservice.dtos.schedule.ScheduleResponse;
import dev.clinic.mainservice.services.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@Tag(name = "Раписание графика врачей", description = "Управление распианием рабочих дней врачей")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @Operation(
            summary = "Получение расписания всех врачей",
            description = "Возвращает расписание всех врачей",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Расписание успешно получено"),
                    @ApiResponse(responseCode = "400", description = "Распиание не найдено")
            }
    )
    @GetMapping
    public ResponseEntity<List<ScheduleResponse>> getAllSchedules() {
        List<ScheduleResponse> response = scheduleService.getAllSchedules();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Получение списка расписания определенного врача",
            description = "Возвращает список расписания определенного врача",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Расписание успешно получено"),
                    @ApiResponse(responseCode = "400", description = "Распиание не найдено")
            }
    )
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<ScheduleResponse>> getAllSchedulesByDoctorId(
            @Parameter(description = "Уникальный идентификатор врача", required = true)
            @PathVariable Long doctorId
    ) {
        List<ScheduleResponse> response = scheduleService.getAllSchedulesByDoctorId(doctorId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Получение списка расписания определенного врача в определенный день",
            description = "Возвращает список расписания определенного врача в определенный день",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Расписание успешно получено"),
                    @ApiResponse(responseCode = "400", description = "Распиание не найдено")
            }
    )
    @GetMapping("/doctor/{doctorId}/date/{date}")
    public ResponseEntity<List<ScheduleResponse>> getAllSchedulesByDoctorIdAndDate(
            @Parameter(description = "Уникальный идентификатор врача", required = true)
            @PathVariable Long doctorId,

            @Parameter(description = "Дата расписания в формате ГГ-ММ-ДД", required = true)
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<ScheduleResponse> response = scheduleService.getAllSchedulesByDoctorIdAndDate(doctorId, date);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Получение списка расписания для текущего врача",
            description = "Возвращает список расписания для текущего врача",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Расписание успешно получено"),
                    @ApiResponse(responseCode = "400", description = "Распиание не найдено")
            }
    )
    @GetMapping("/me")
    public ResponseEntity<List<ScheduleResponse>> getAllPrincipalSchedules() {
        List<ScheduleResponse> response = scheduleService.getAllPrincipalSchedules();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Получение списка расписания для текущего врача в определенный день",
            description = "Возвращает список расписания для текущего врача в определенный день",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Расписание успешно получено"),
                    @ApiResponse(responseCode = "400", description = "Распиание не найдено")
            }
    )
    @GetMapping("/me/date/{date}")
    public ResponseEntity<List<ScheduleResponse>> getAllPrincipalSchedulesByDate(
            @Parameter(description = "Дата расписания в формате ГГ-ММ-ДД", required = true)
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<ScheduleResponse> response = scheduleService.getAllPrincipalSchedulesByDate(date);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Создать расписание врача",
            description = "Создаёт новое распиание рабочего дня для врача",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Расписание успешно создано"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
            }
    )
    @PostMapping("/create-doctor-schedule")
    public ResponseEntity<ScheduleResponse> createDoctorSchedule(
            @Parameter(description = "Данные для создания распиания врача", required = true)
            @RequestBody ScheduleRequest scheduleRequest
    ) {
        ScheduleResponse response = scheduleService.createDoctorSchedule(scheduleRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ScheduleResponse> deleteDoctorSchedule(
            @Parameter(description = "Уникальный идетификатор расписания", required = true)
            @PathVariable Long id
    ) {
        scheduleService.deleteDoctorSchedule(id);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }
}
