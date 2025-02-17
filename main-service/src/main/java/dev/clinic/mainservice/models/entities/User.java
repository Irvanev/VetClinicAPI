package dev.clinic.mainservice.models.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Сущность {@code User} представляет пользователя системы.
 * <p>
 * Данный класс хранит информацию о пользователе, включая его контактные данные,
 * данные для аутентификации, флаг подтверждения регистрации, а также сведения о
 * связанных сущностях, таких как список питомцев и роль.
 * </p>
 * <p>
 * Наследует {@link BaseEntity}, в котором храниться унификальный идентификатор.
 * </p>
 *
 * @author Irvanev
 * @version 1.0
 */
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    /** Электронная почта пользователя, используется также как логин. */
    private String email;

    /** Полное имя пользователя. */
    private String fullName;

    /** Номер телефона пользователя. */
    private String numberPhone;

    /** Хэшированный пароль пользователя. */
    private String password;

    /** Дата рождения пользователя. */
    private Date dateOfBirth;

    /** Флаг, указывающий, активен ли пользователь. */
    private boolean enabled;

    /** Код подтверждения регистрации, отправляемый на email. */
    private String verificationCode;

    /** Дата и время истечения срока действия кода подтверждения. */
    private LocalDateTime verificationCodeExpiresAt;

    /** Список питомцев, принадлежащих пользователю. */
    private List<Pet> pets;

    /** Роль пользователя в системе. */
    private Role role;

    /**
     * Конструктор по умолчанию, необходимый для работы JPA.
     */
    protected User() {}

    /**
     * Конструктор, создающий нового пользователя с указанными email и паролем.
     *
     * @param email    электронная почта пользователя
     * @param password пароль пользователя (должен быть захэширован)
     */
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNumberPhone() {
        return numberPhone;
    }
    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @OneToMany(mappedBy = "owner", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    public List<Pet> getPets() {
        return pets;
    }
    public void setPets(List<Pet> pets) {
        this.pets = pets;
    }

    @ManyToOne
    @JoinColumn(name = "role_id")
    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getVerificationCode() {
        return verificationCode;
    }
    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public LocalDateTime getVerificationCodeExpiresAt() {
        return verificationCodeExpiresAt;
    }
    public void setVerificationCodeExpiresAt(LocalDateTime verificationCodeExpiresAt) {
        this.verificationCodeExpiresAt = verificationCodeExpiresAt;
    }
}