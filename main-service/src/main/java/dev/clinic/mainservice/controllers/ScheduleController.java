package dev.clinic.mainservice.controllers;

import dev.clinic.mainservice.dtos.schedule.ScheduleRequest;
import dev.clinic.mainservice.dtos.schedule.ScheduleResponse;
import dev.clinic.mainservice.services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping("/create-doctor-schedule")
    public ResponseEntity<ScheduleResponse> createDoctorSchedule(@RequestBody ScheduleRequest scheduleRequest) {
        ScheduleResponse response = scheduleService.createDoctorSchedule(scheduleRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
