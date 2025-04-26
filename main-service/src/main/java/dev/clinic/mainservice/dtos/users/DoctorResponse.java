package dev.clinic.mainservice.dtos.users;

import dev.clinic.mainservice.dtos.pets.PetResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Получение врача")
public class DoctorResponse {
    private Long id;
    private String fullName;
    private String role;
    private String email;
    private String phoneNumber;
    private List<PetResponse> pets;
    private String specialization;
    private String address;
    private String education;
    private String photo;

    @Schema(description = "Уникальный идентификатор врача", example = "1")
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Schema(description = "ФИО врача", example = "Иванов Иван Иванович")
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Schema(description = "Роль врача", example = "Доктор")
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    @Schema(description = "Почта врача", example = "test@example.com")
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    @Schema(description = "Номер телефона врача", example = "79999999999")
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<PetResponse> getPets() {
        return pets;
    }
    public void setPets(List<PetResponse> pets) {
        this.pets = pets;
    }

    @Schema(description = "Специальность врача", example = "Терапевт")
    public String getSpecialization() {
        return specialization;
    }
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Schema(description = "Адрес врача", example = "г. Москва, ул. Ленина, д. 1")
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    @Schema(description = "Образование врача", example = "МГУ")
    public String getEducation() {
        return education;
    }
    public void setEducation(String education) {
        this.education = education;
    }

    @Schema(description = "Фото врача")
    public String getPhoto() {
        return photo;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
