package dev.clinic.mainservice.dtos.pets;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Получение питомца")
public class PetResponse {
    private Long id;
    private String name;
    private LocalDate birthDate;
    private String animalType;
    private String breed;

    @Schema(description = "Уникальный идентификатор питомца", example = "1")
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Schema(description = "Имя питомца", example = "Арнольд")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Schema(description = "Тип животного", example = "Собака")
    public String getAnimalType() {
        return animalType;
    }
    public void setAnimalType(String animalType) {
        this.animalType = animalType;
    }

    @Schema(description = "Дата рождения питомца", example = "2020-01-01")
    public LocalDate getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    @Schema(description = "Порода питомца", example = "Лабрадор")
    public String getBreed() {
        return breed;
    }
    public void setBreed(String breed) {
        this.breed = breed;
    }
}
