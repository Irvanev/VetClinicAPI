package dev.clinic.mainservice.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AppointmentStatus {
    SCHEDULED(1, "Запланирован"),   // Запланирован
    COMPLETED(2, "Завершён"),   // Завершён
    CANCELLED(3, "Отменён"),   // Отменён
    NO_SHOW(1, "Пациент не пришёл");      // Пациент не пришёл

    private int number;
    private String name;

    AppointmentStatus(int number, String name) {
        this.number = number;
        this.name = name;
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
    public static AppointmentStatus fromName(String name) {
        for (AppointmentStatus status : values()) {
            if (status.name.equalsIgnoreCase(name.trim())) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown AppointmentType: " + name);
    }
}
