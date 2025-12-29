package com.example.fazonapp.dto;


import com.example.fazonapp.model.User;

public class UserDTO {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String nationality;

    // Empty constructor
    public UserDTO() {}

    // Constructor from User model
    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.role = user.getRole();
        this.nationality = user.getNationality();
        // Notice: We DON'T include the ID here for security
    }

    // Convert DTO to User model
    public User toUser(String id) {
        User user = new User();
        user.setId(id);
        user.setName(this.name);
        user.setEmail(this.email);
        user.setPhone(this.phone);
        user.setRole(this.role);
        user.setNationality(this.nationality);
        return user;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }


    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
}