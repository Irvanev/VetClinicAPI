package dev.clinic.mainservice.models.entities;

import jakarta.persistence.*;

/**
 * Сущность {@code Doctor} представляет врача, который наследуется от абстрактного класса пользователя {@code User}.
 * <p>
 * Данный класс включает в себя все поля {@code User} и дополняющие поля {@code Doctor}
 * такие как: специализация, адрес, образование, фото и идентификатор расписания.
 * </p>
 *
 * @author Irvanev
 * @version 1.0
 */
@Entity
@Table(name = "doctors")
public class Doctor extends User {

    /** Специализация врача */
    private String specialization;

    /** Адрес регистрации */
    private String address;

    /** Образование врача */
    private String education;

    /** Ссылка на фотографию врача */
    private String photo;

    /** Расписание врача */
    private Branches branch;

    /**
     * Конструктор по умолчанию, необходимый для работы JPA.
     */
    public Doctor() {}

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    public Branches getBranch() {
        return branch;
    }

    public void setBranch(Branches branch) {
        this.branch = branch;
    }
}
