package dev.clinic.mainservice.models.entities;

import dev.clinic.mainservice.models.enums.AnimalType;
import jakarta.persistence.*;

/**
 * Сущность Pet представляет питомца, принадлежащего пользователю(клиенту).
 * Содержит информацию об имени, возрасте, поле, весе, породе, цвете и типе животного.
 *
 * <p>Эта сущность связана с {@link User} через поле owner.</p>
 *
 * <p>
 * Данный класс наследуется от {@link BaseEntity}, который содержит уникальный идентификатор.
 * </p>
 * @author Irvanev
 * @version 1.0
 */
@Entity
@Table(name = "pets")
public class Pet extends BaseEntity {

    /** Имя питомца. */
    private String name;

    /** Возраст питомца. */
    private int age;

    /** Пол питомца. */
    private String gender;

    /** Вес питомца. */
    private String weight;

    /** Порода питомца. */
    private String breed;

    /** Цвет питомца. */
    private String color;

    /** Тип животного (например, собака, кошка и т.д.). */
    private AnimalType animalType;

    /** Владелец питомца. */
    private User owner;

    /**
     * Конструктор по умолчанию (требуется JPA).
     */
    protected Pet() {}

    /**
     * Конструктор, создающий новый объект Pet с указанными параметрами.
     *
     * @param name имя питомца
     * @param age возраст питомца
     * @param gender пол питомца
     * @param weight вес питомца
     * @param breed порода питомца
     * @param color цвет питомца
     * @param animalType тип животного
     * @param owner владелец питомца
     */
    public Pet(String name, int age, String gender, String weight, String breed, String color, AnimalType animalType, User owner) {
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