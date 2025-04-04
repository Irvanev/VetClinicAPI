package dev.clinic.mainservice.mapping;

import dev.clinic.mainservice.dtos.schedule.ScheduleRequest;
import dev.clinic.mainservice.dtos.schedule.ScheduleResponse;
import dev.clinic.mainservice.models.entities.Doctor;
import dev.clinic.mainservice.models.entities.Schedule;

public class ScheduleMapper {

    public static Schedule fromRequest(ScheduleRequest request) {
        if (request == null) {
            return null;
        }
        Schedule schedule = new Schedule();

        Doctor doctor = new Doctor();
        doctor.setId(request.getDoctorId());
        schedule.setDoctor(doctor);

        schedule.setDate(request.getDate());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());

        return schedule;
    }

    public static ScheduleResponse toResponse(Schedule schedule) {
        if (schedule == null) {
            return null;
        }
        ScheduleResponse response = new ScheduleResponse();
        response.setId(schedule.getId());

        if (schedule.getDoctor() != null) {
            response.setDoctorId(schedule.getDoctor().getId());
        }

        response.setDate(schedule.getDate());
        response.setStartTime(schedule.getStartTime());
        response.setEndTime(schedule.getEndTime());

        return response;
    }
}

