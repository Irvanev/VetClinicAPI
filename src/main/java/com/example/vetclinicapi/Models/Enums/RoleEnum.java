package com.example.vetclinicapi.Models.Enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RoleEnum {

    User (1, "Пользователь"),
    Doctor (2, "Доктор"),
    Admin (3, "Администартор");

    private final int number;
    private String name;

    RoleEnum(int number, String name) {
        this.number = number;
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
