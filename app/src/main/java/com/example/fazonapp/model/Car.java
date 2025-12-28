package com.example.fazonapp.model;

public class Car {
    private String id;
    private String name;
    private String brand;
    private String type; // SUV, Sedan, etc.
    private double pricePerDay;
    private boolean availability;
    private String imageUrl;

    public Car() {}

    public Car(String id, String name, String brand, String type,
               double pricePerDay, boolean availability, String imageUrl) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.type = type;
        this.pricePerDay = pricePerDay;
        this.availability = availability;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(double pricePerDay) { this.pricePerDay = pricePerDay; }

    public boolean isAvailability() { return availability; }
    public void setAvailability(boolean availability) { this.availability = availability; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}