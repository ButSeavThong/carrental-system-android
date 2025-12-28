package com.example.fazonapp.model;

public class Booking {
    private String id;
    private String userId;
    private String carId;
    private String carName; // For display purposes
    private long startDate; // Timestamp
    private long endDate; // Timestamp
    private double totalPrice;
    private String status; // Pending, Approved, Rejected

    public Booking() {}

    public Booking(String id, String userId, String carId, String carName,
                   long startDate, long endDate, double totalPrice, String status) {
        this.id = id;
        this.userId = userId;
        this.carId = carId;
        this.carName = carName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCarId() { return carId; }
    public void setCarId(String carId) { this.carId = carId; }

    public String getCarName() { return carName; }
    public void setCarName(String carName) { this.carName = carName; }

    public long getStartDate() { return startDate; }
    public void setStartDate(long startDate) { this.startDate = startDate; }

    public long getEndDate() { return endDate; }
    public void setEndDate(long endDate) { this.endDate = endDate; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
