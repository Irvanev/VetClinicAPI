package com.example.vetclinicapi.Models.Entities;

import com.example.vetclinicapi.Models.Enums.AnimalType;
import jakarta.persistence.*;

@Entity
@Table(name = "animals")
public class Animal extends BaseEntity {
    private String name;
    private int age;
    private String gender;
    private String weight;
    private String breed;
    private String color;
    private AnimalType animalType;

    private User owner;

    protected Animal() {}

    public Animal(String name, int age, String gender, String weight, String breed, String color, AnimalType animalType, User owner) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.weight = weight;
        this.breed = breed;
        this.color = color;
        this.animalType = animalType;
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public AnimalType getAnimalType() {
        return animalType;
    }

    public void setAnimalType(AnimalType animalType) {
        this.animalType = animalType;
    }
}
