package dev.clinic.mainservice.dtos.branches;

import io.swagger.v3.oas.annotations.media.Schema;
import org.locationtech.jts.geom.Point;

@Schema(description = "Получение филиала(отделения)")
public class BranchResponse {
    private Long id;
    private String address;
    private String phone;
    private String email;
    private Point coordinates;

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

    @Schema(description = "Координаты филиала")
    public Point getCoordinates() {
        return coordinates;
    }
    public void setCoordinates(Point coordinates) {
        this.coordinates = coordinates;
    }
}
