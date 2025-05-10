package dev.clinic.mainservice.models.entities;

import dev.clinic.mainservice.models.enums.AppointmentType;
import jakarta.persistence.*;
import org.locationtech.jts.geom.Point;

import java.util.List;
import java.util.Set;

/**
 * Сущность {@code Branches} представляет филиалы (подразделения).
 * <p>
 * Данный класс хранит общую информацию о подразделении, такие как
 * адрес, координаты, контактные данные и т.д. Каждый {@code Doctor}
 * привзяан к определенному филиалу. Т.е.
 * </p>
 *
 * @author Irvanev
 * @version 1.0
 */
@Entity
@Table(name = "branches")
public class Branches extends BaseEntity {

    /** Полное название филиала */
    private String name;

    /** Соцращенное название филиала */
    private String shortName;

    /** Адрес филиала */
    private String address;

    /** Номер телефона филиала */
    private String phone;

    /** Почта филиала */
    private String email;

    /** Координаты филиала */
    private Point coordinates;

    /** Список врачей, которые закреплены под филиалом */
    private List<Doctor> doctors;

    /** Список услуг, которые есть в филиалы */
    private Set<AppointmentType> services;

    /**
     * Конструктор по умолчанию, необходимый для работы JPA.
     */
    public Branches() {}

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

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

    @ElementCollection(targetClass = AppointmentType.class, fetch = FetchType.LAZY)
    @CollectionTable(
            name = "branches_services",
            joinColumns = @JoinColumn(name = "branch_id")
    )
    @Column(name = "service", nullable = false)
    @Enumerated(EnumType.STRING)
    public Set<AppointmentType> getServices() {
        return services;
    }
    public void setServices(Set<AppointmentType> services) {
        this.services = services;
    }
}
