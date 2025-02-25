package dev.clinic.mainservice.dtos.auth;

public class SignUpRequest {
    private String fullName;
    private String email;
    private String numberPhone;
    private String password;

    private SignUpRequest() {}

    public SignUpRequest(String fullName, String email, String numberPhone, String password) {
        this.fullName = fullName;
        this.email = email;
        this.numberPhone = numberPhone;
        this.password = password;
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

    @Override
    public String toString() {
        return "SignUpRequest{" +
                "fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", numberPhone='" + numberPhone + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
