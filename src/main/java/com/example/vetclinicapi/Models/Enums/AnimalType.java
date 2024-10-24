package com.example.vetclinicapi.Models.Enums;

public enum AnimalType {

    Dog (1, "Dog"),
    Cat (2, "Cat");

    private int number;
    private String name;

    AnimalType(int number, String name) {
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
