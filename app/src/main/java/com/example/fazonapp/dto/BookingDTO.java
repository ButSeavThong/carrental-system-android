package com.example.fazonapp.dto;

import com.example.fazonapp.model.Booking;
import com.example.fazonapp.utils.DateUtils;

public class BookingDTO {
    private String id;
    private String carName;
    private String carBrand;
    private String startDate; // Formatted date string
    private String endDate; // Formatted date string
    private double totalPrice;
    private String status;
    private long numberOfDays;

    public BookingDTO() {}

    // Constructor from Booking model
    public BookingDTO(Booking booking) {
        this.id = booking.getId();
        this.carName = booking.getCarName();
        this.startDate = DateUtils.formatDate(booking.getStartDate());
        this.endDate = DateUtils.formatDate(booking.getEndDate());
        this.totalPrice = booking.getTotalPrice();
        this.status = booking.getStatus();
        this.numberOfDays = DateUtils.getDaysBetween(
                booking.getStartDate(),
                booking.getEndDate()
        );
        // Notice: We DON'T include userId and carId for security
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCarName() { return carName; }
    public void setCarName(String carName) { this.carName = carName; }

    public String getCarBrand() { return carBrand; }
    public void setCarBrand(String carBrand) { this.carBrand = carBrand; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getNumberOfDays() { return numberOfDays; }
    public void setNumberOfDays(long numberOfDays) { this.numberOfDays = numberOfDays; }
}