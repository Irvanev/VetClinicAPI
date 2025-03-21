package dev.clinic.mainservice.models.entities;

import dev.clinic.mainservice.models.enums.RoleEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;

import java.util.List;

/**
 * Сущность {@code Role} представляет роль пользователя в системе.
 * <p>
 * Каждая роль определяется значением перечисления {@link RoleEnum} и может быть
 * связана с несколькими пользователями.
 * </p>
 *
 * <p>
 * Данный класс наследуется от {@link BaseEntity}, который содержит уникальный идентификатор.
 * </p>
 *
 * @author Irvanev
 * @version 1.0
 */
@Entity
public class Role extends BaseEntity {

    /** Название роли, представленное в виде перечисления {@link RoleEnum}. */
    private RoleEnum name;

    /** Список пользователей, имеющих данную роль. */
    private List<User> users;

    @Enumerated(EnumType.STRING)
    public RoleEnum getName() {
        return name;
    }
    public void setName(RoleEnum name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "role")
    public List<User> getUsers() {
        return users;
    }
    public void setUsers(List<User> users) {
        this.users = users;
    }
}