package dev.clinic.mainservice.models.entities;

import jakarta.persistence.*;
import org.locationtech.jts.geom.Point;

import java.util.List;

/**
 * Сущность {@code Branches} представляет филиалы (подразделения).
 * <p>
 * Данный класс хранит общую информацию о подразделении, такие как
 * адрес, координаты, контактные данные и т.д.
 * </p>
 *
 * @author Irvanev
 * @version 1.0
 */
@Entity
public class Branches extends BaseEntity {
    private String address;
    private String phone;
    private String email;
    private Point coordinates; // !TODO

    private List<Doctor> doctors;

    public Branches() {}

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    //@Column(columnDefinition = "geometry(Point, 4326) using coordinates::geometry(Point, 4326)")
    @Column(columnDefinition = "geometry(Point, 4326)")
    public Point getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Point coordinates) {
        this.coordinates = coordinates;
    }

    @OneToMany(mappedBy = "branch", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    public List<Doctor> getDoctors() {
        return doctors;
    }

    public void setDoctors(List<Doctor> doctors) {
        this.doctors = doctors;
    }
}
