package dev.clinic.mainservice.repositories;

import dev.clinic.mainservice.models.entities.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
