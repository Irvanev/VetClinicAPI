package dev.clinic.mainservice.models.entities;

import jakarta.persistence.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Сущность {@code Schedule} представляет расписание.
 * Содержит информацию о докторе, кому принадлежит данное расписание, день недели, время начала рабочего дня и конец.
 *
 * <p>Эта сущность связана с {@link Doctor} через поле doctor.</p>
 *
 * <p>
 * Данный класс наследуется от {@link BaseEntity}, который содержит уникальный идентификатор.
 * </p>
 * @author Irvanev
 * @version 1.0
 */
@Entity
@Table(name = "schedules")
public class Schedule extends BaseEntity {

    /** Доктор, к которому данное расписание привязано */
    private User doctor;

    /** Время начала рабочего дня */
    private LocalTime startTime;

    /** Время окончания рабочего дня */
    private LocalTime endTime;

    /** Дата рабочего дня */
    private LocalDate date;

    /**
     * Конструктор по умолчанию, необходимый для работы JPA.
     */
    public Schedule() {}

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    public User getDoctor() {
        return doctor;
    }

    public void setDoctor(User doctor) {
        this.doctor = doctor;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
