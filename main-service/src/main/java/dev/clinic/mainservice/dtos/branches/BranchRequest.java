package dev.clinic.mainservice.dtos.branches;

import dev.clinic.mainservice.models.enums.AppointmentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.locationtech.jts.geom.Point;

import java.util.Set;

@Schema(description = "Запрос на создание филиала(отделения)")
public class BranchRequest {
    private String name;
    private String shortName;
    private String address;
    private String phone;
    private String email;
    private double latitude;
    private double longitude;
    private Set<AppointmentType> services;

    @Schema(description = "Полное название филиала", example = "Территориальное ветеринарное управление № 5, Ленинская ветеринарная станция")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Schema(description = "Короткое название филиала", example = "Территориальное ветеринарное управление № 5")
    public String getShortName() {
        return shortName;
    }
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Schema(description = "Адрес филиала", example = "Москва, ул. Образцова, 9, к1")
    @NotEmpty(message = "Адрес не может быть пустым")
    @Size(max = 255, message = "Длина адреса не должна превышать 255 символов")
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    @Schema(description = "Телефон филиала", example = "+7(999)123-45-67")
    @NotEmpty(message = "Телефон не может быть пустым")
    @Pattern(regexp = "^\\+?[0-9\\s\\-\\(\\)]{10,20}$", message = "Неверный формат номера телефона")
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Schema(description = "Почта филиала", example = "test@example.com")
    @Email(message = "Неверный формат электронной почты")
    @NotEmpty(message = "Email не может быть пустым")
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    @Schema(description = "Координаты филиала (долгота)")
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Schema(description = "Координаты филиала (широта)")
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Schema(description = "Список предосталвяемых услуг")
    public Set<AppointmentType> getServices() {
        return services;
    }
    public void setServices(Set<AppointmentType> services) {
        this.services = services;
    }
}
