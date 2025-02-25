package dev.clinic.mainservice.dtos.users;

import dev.clinic.mainservice.dtos.pets.PetResponse;

import java.util.List;

public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String password;
    private String phoneNumber;
    private List<PetResponse> pets;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<PetResponse> getPets() {
        return pets;
    }

    public void setPets(List<PetResponse> pets) {
        this.pets = pets;
    }
}
