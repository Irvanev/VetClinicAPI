package dev.clinic.mainservice.services.impl;

import dev.clinic.mainservice.dtos.schedule.ScheduleRequest;
import dev.clinic.mainservice.dtos.schedule.ScheduleResponse;
import dev.clinic.mainservice.exceptions.ResourceNotFoundException;
import dev.clinic.mainservice.mapping.ScheduleMapper;
import dev.clinic.mainservice.models.entities.Doctor;
import dev.clinic.mainservice.models.entities.Schedule;
import dev.clinic.mainservice.repositories.DoctorRepository;
import dev.clinic.mainservice.repositories.ScheduleRepository;
import dev.clinic.mainservice.services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;

    @Autowired
    public ScheduleServiceImpl(
            ScheduleRepository scheduleRepository,
            DoctorRepository doctorRepository
    ) {
        this.scheduleRepository = scheduleRepository;
        this.doctorRepository = doctorRepository;
    }

    @Override
    public ScheduleResponse createDoctorSchedule(ScheduleRequest request) {
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor with id " + request.getDoctorId() + " not found"));

        Schedule schedule = ScheduleMapper.fromRequest(request);
        schedule.setDoctor(doctor);

        scheduleRepository.saveAndFlush(schedule);

        return ScheduleMapper.toResponse(schedule);
    }
}
