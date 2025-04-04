package dev.clinic.mainservice.dtos.users;

import dev.clinic.mainservice.dtos.pets.PetResponse;

import java.util.List;

public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String role;
    private String email;
    private String phoneNumber;
    private List<PetResponse> pets;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
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
