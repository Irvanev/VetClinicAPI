package dev.clinic.mainservice.services;

import dev.clinic.mainservice.dtos.schedule.ScheduleRequest;
import dev.clinic.mainservice.dtos.schedule.ScheduleResponse;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {
    List<ScheduleResponse> getAllSchedules();
    List<ScheduleResponse> getAllSchedulesByDoctorId(Long id);
    List<ScheduleResponse> getAllSchedulesByDoctorIdAndDate(Long doctorId, LocalDate date);
    List<ScheduleResponse> getAllPrincipalSchedules();
    List<ScheduleResponse> getAllPrincipalSchedulesByDate(LocalDate date);
    ScheduleResponse createDoctorSchedule(ScheduleRequest request);
    void editDoctorSchedule(Long id, ScheduleRequest request);
    void deleteDoctorSchedule(Long id);

}
