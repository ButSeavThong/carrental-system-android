package com.example.fazonapp.utils;

// Why Constants? Prevents typos and makes code easier to update.
public class Constants {
    // Firebase Database Nodes
    public static final String USERS_NODE = "users";
    public static final String CARS_NODE = "cars";
    public static final String BOOKINGS_NODE = "bookings";

    // User Roles
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_CUSTOMER = "CUSTOMER";

    // Booking Status
    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_APPROVED = "Approved";
    public static final String STATUS_REJECTED = "Rejected";

    // Nationality
    public static final String NATIONALITY_CAMBODIAN = "Cambodian";
    public static final String NATIONALITY_FOREIGNER = "Foreigner";
}
