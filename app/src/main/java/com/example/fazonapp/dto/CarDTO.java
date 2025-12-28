package com.example.fazonapp.dto;

import com.example.fazonapp.model.Car;

public class CarDTO {
    private String id; // We include ID for cars as it's not sensitive
    private String name;
    private String brand;
    private String type;
    private double pricePerDay;
    private boolean availability;
    private String imageUrl;

    public CarDTO() {}

    // Constructor from Car model
    public CarDTO(Car car) {
        this.id = car.getId();
        this.name = car.getName();
        this.brand = car.getBrand();
        this.type = car.getType();
        this.pricePerDay = car.getPricePerDay();
        this.availability = car.isAvailability();
        this.imageUrl = car.getImageUrl();
    }

    // Convert DTO to Car model
    public Car toCar() {
        Car car = new Car();
        car.setId(this.id);
        car.setName(this.name);
        car.setBrand(this.brand);
        car.setType(this.type);
        car.setPricePerDay(this.pricePerDay);
        car.setAvailability(this.availability);
        car.setImageUrl(this.imageUrl);
        return car;
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
