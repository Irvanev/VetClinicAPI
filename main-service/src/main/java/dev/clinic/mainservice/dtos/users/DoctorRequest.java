package dev.clinic.mainservice.dtos.users;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.Date;

@Schema(description = "Запрос на регистрацию врача")
public class DoctorRequest {
    private Long branchId;
    private String email;
    private String fullName;
    private String numberPhone;
    private Date dateOfBirth;
    private String specialization;
    private String address;
    private String education;
    private String photo;

    @Schema(description = "Уникальный идентификатор филиала", example = "1")
    @NotNull(message = "Уникальный идентификатор филиала не может быть пустым")
    @Positive(message = "Уникальный идентификатор филиала должен быть положительным")
    public Long getBranchId() {
        return branchId;
    }
    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    @Schema(description = "Почта врача", example = "test@example.com")
    @NotEmpty(message = "Почта врача не может быть пустой")
    @Email(message = "Почта врача должна быть корректной")
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    @Schema(description = "ФИО врача", example = "Иванов Иван Иванович")
    @NotEmpty(message = "ФИО врача не может быть пустым")
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Schema(description = "Номер телефона врача", example = "79999999999")
    @NotEmpty(message = "Номер телефона врача не может быть пустым")
    @Pattern(regexp = "^\\+?[0-9\\s\\-\\(\\)]{10,20}$", message = "Неверный формат номера телефона")
    public String getNumberPhone() {
        return numberPhone;
    }
    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }

    @Schema(description = "Дата рождения", example = "2023-10-01")
    @NotNull(message = "Дата рождения не может быть пустой")
    @Past(message = "Дата рождения должна быть в прошлом")
    public Date getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Schema(description = "Специальность врача", example = "Терапевт")
    @NotEmpty(message = "Специальность врача не может быть пустой")
    public String getSpecialization() {
        return specialization;
    }
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Schema(description = "Адрес врача", example = "г. Москва, ул. Ленина, д. 1")
    @NotEmpty(message = "Адрес врача не может быть пустым")
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    @Schema(description = "Образование врача", example = "МГМУ им. Сеченова")
    @NotEmpty(message = "Образование врача не может быть пустым")
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
