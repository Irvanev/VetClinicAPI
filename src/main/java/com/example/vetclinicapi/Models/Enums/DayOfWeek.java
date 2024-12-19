package com.example.vetclinicapi.Models.Enums;

public enum DayOfWeek {
    Monday (1, "Понедельник"),
    Tuesday (2, "Вторник"),
    Wednesday (3, "Среда"),
    Thursday (4, "Четверг"),
    Friday (5, "Пятница"),
    Saturday (6, "Суббота"),
    Sunday (7, "Воскресенье");

    private int number;
    private String name;

    DayOfWeek(int number, String name) {
        this.number = number;
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
