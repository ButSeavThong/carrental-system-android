package com.example.fazonapp.dto;


import com.example.fazonapp.model.User;

public class RegisterDTO {
    private String name;
    private String email;
    private String password;
    private String phone;
    private String nationality;

    public RegisterDTO() {}

    public RegisterDTO(String name, String email, String password,
                       String phone, String nationality) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.nationality = nationality;
    }

    // Convert to User (role will be set as CUSTOMER by default)
    public User toUser() {
        User user = new User();
        user.setName(this.name);
        user.setEmail(this.email);
        user.setPhone(this.phone);
        user.setNationality(this.nationality);
        user.setRole("CUSTOMER"); // Default role
        return user;
    }

    // Validation methods
    public boolean isValid() {
        return name != null && !name.isEmpty() &&
                email != null && !email.isEmpty() &&
                password != null && password.length() >= 6 &&
                phone != null && !phone.isEmpty() &&
                nationality != null && !nationality.isEmpty();
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
}
