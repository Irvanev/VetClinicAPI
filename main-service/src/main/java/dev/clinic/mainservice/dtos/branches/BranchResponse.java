package dev.clinic.mainservice.dtos.branches;

import dev.clinic.mainservice.models.enums.AppointmentType;
import io.swagger.v3.oas.annotations.media.Schema;
import org.locationtech.jts.geom.Point;

import java.util.Set;

@Schema(description = "Получение филиала(отделения)")
public class BranchResponse {
    private String name;
    private String shortName;
    private Long id;
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

    @Schema(description = "Уникальный идентификатор филиала", example = "1")
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Schema(description = "Адрес филиала", example = "Москва, ул. Образцова, 9, к1")
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    @Schema(description = "Номер телефона филиала", example = "79990001122")
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Schema(description = "Электронная почта филиала", example = "test@example.com")
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
