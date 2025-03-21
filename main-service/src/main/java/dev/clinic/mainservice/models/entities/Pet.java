package dev.clinic.mainservice.models.entities;

import dev.clinic.mainservice.models.enums.AnimalType;
import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Сущность Pet представляет питомца, принадлежащего пользователю(клиенту).
 * Содержит информацию об имени, породе и дате рождения.
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

    /** Порода питомца. */
    private String breed;

    /** Дата рождения питомца */
    private LocalDate birthDate;

    /** Тип питомца */
    private String animalType;

    /** Фото питомца */
    private String photoUrl;

    /** Миниатюрное фото питомца */
    private String thumbnailUrl;

    /** Владелец питомца. */
    private User owner;

    /**
     * Конструктор по умолчанию.
     */
    public Pet() {}

    /**
     * Конструктор, создающий новый объект Pet с указанными параметрами.
     *
     * @param name имя питомца
     * @param breed порода питомца
     * @param birthDate дата рождения питомца
     * @param owner владелец питомца
     */

    public Pet(String name, String breed, LocalDate birthDate, User owner) {
        this.name = name;
        this.breed = breed;
        this.birthDate = birthDate;
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getAnimalType() {
        return animalType;
    }

    public void setAnimalType(String animalType) {
        this.animalType = animalType;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}