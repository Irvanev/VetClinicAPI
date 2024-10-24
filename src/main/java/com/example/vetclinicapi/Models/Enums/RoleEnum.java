package com.example.vetclinicapi.Models.Enums;

public enum RoleEnum {

    User (1, "Пользователь"),
    Doctor (2, "Доктор"),
    Admin (3, "Администартор");

    private int number;
    private String name;

    RoleEnum(int number, String name) {
        this.number = number;
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
