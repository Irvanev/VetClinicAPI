package dev.clinic.mainservice.models.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.time.Duration;

public enum AppointmentType {

    XRAY(Duration.ofMinutes(15), 1, "Рентген"),
    CONSULTATION(Duration.ofMinutes(30), 2, "Консультация"),
    VACCINATION(Duration.ofMinutes(15), 3, "Вакцинация"),
    SURGERY(Duration.ofMinutes(60), 4, "Операция"),
    INSPECTION(Duration.ofMinutes(15), 5, "Осмотр"),
    CHIPPING(Duration.ofMinutes(15), 6, "Чипирование");

    private final Duration duration;
    private int number;
    private String name;

    AppointmentType(Duration duration, int number, String name) {
        this.duration = duration;
        this.number = number;
        this.name = name;
    }

    public Duration getDuration() {
        return duration;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonCreator
    public static AppointmentType fromName(String name) {
        for (AppointmentType type : values()) {
            if (type.name.equalsIgnoreCase(name.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown AppointmentType: " + name);
    }
}
