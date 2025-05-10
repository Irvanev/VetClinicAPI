package dev.clinic.mainservice.dtos.users;

import dev.clinic.mainservice.dtos.pets.PetResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Получение врача")
public class DoctorResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String role;
    private String email;
    private String phoneNumber;
    private String specialization;
    private String address;
    private String education;
    private String photo;

    public DoctorResponse(
            Long id, String firstName, String lastName,
            String role, String email, String phoneNumber,
            String specialization, String address,
            String education, String photo
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.specialization = specialization;
        this.address = address;
        this.education = education;
        this.photo = photo;
    }

    public DoctorResponse() {
    }

    @Schema(description = "Уникальный идентификатор врача", example = "1")
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Schema(description = "Имя врача", example = "Иван")
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Schema(description = "Фамилия врача", example = "Иванов")
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
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
