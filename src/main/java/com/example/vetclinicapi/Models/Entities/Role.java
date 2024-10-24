package com.example.vetclinicapi.Models.Entities;

import com.example.vetclinicapi.Models.Enums.RoleEnum;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Role extends BaseEntity {
    private RoleEnum name;

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
