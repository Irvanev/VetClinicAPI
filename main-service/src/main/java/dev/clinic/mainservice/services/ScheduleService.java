package dev.clinic.mainservice.services;

import dev.clinic.mainservice.dtos.schedule.ScheduleRequest;
import dev.clinic.mainservice.dtos.schedule.ScheduleResponse;

public interface ScheduleService {
    ScheduleResponse createDoctorSchedule(ScheduleRequest request);
}
