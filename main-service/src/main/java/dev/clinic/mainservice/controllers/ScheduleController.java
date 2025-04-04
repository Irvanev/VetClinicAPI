package dev.clinic.mainservice.controllers;

import dev.clinic.mainservice.dtos.schedule.ScheduleRequest;
import dev.clinic.mainservice.dtos.schedule.ScheduleResponse;
import dev.clinic.mainservice.services.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/schedule")
@Tag(name = "Раписание графика врачей", description = "Управление распианием рабочих дней врачей")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
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
}
