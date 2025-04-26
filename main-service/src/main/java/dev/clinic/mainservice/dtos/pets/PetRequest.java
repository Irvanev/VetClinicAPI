package dev.clinic.mainservice.dtos.pets;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "Запрос на создание питомца")
public class PetRequest {
    private String name;
    private String animalType;
    private LocalDate birthDate;
    private String breed;

    @Schema(description = "Имя питомца", example = "Арнольд")
    @NotEmpty(message = "Имя питомца не может быть пустым")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Schema(description = "Тип животного", example = "Собака")
    @NotEmpty(message = "Тип животного не может быть пустым")
    public String getAnimalType() {
        return animalType;
    }
    public void setAnimalType(String animalType) {
        this.animalType = animalType;
    }

    @Schema(description = "Дата рождения питомца", example = "2020-01-01")
    @NotNull(message = "Дата рождения питомца не может быть пустой")
    public LocalDate getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    @Schema(description = "Порода питомца", example = "Лабрадор")
    @NotEmpty(message = "Порода питомца не может быть пустой")
    public String getBreed() {
        return breed;
    }
    public void setBreed(String breed) {
        this.breed = breed;
    }
}
