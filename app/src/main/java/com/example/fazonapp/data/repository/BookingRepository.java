package com.example.fazonapp.data.repository;

import com.example.fazonapp.data.firebase.FirebaseManager;
import com.example.fazonapp.model.Booking;
import com.example.fazonapp.utils.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;

public class BookingRepository {
    private DatabaseReference database;

    public BookingRepository() {
        this.database = FirebaseManager.getInstance().getDatabase();
    }

    // Create new booking
    public void createBooking(Booking booking, BookingCallback callback) {
        // Generate unique ID
        String bookingId = database.child(Constants.BOOKINGS_NODE).push().getKey();
        booking.setId(bookingId);

        database.child(Constants.BOOKINGS_NODE)
                .child(bookingId)
                .setValue(booking)
                .addOnSuccessListener(aVoid -> {
                    // Update car availability to false
                    updateCarAvailability(booking.getCarId(), false);
                    callback.onSuccess(booking);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Get bookings by user ID
    public void getBookingsByUserId(String userId, BookingsCallback callback) {
        database.child(Constants.BOOKINGS_NODE)
                .orderByChild("userId")
                .equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        java.util.ArrayList<Booking> bookings = new java.util.ArrayList<>();
                        for (DataSnapshot bookingSnapshot : snapshot.getChildren()) {
                            Booking booking = bookingSnapshot.getValue(Booking.class);
                            if (booking != null) {
                                bookings.add(booking);
                            }
                        }
                        callback.onSuccess(bookings);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    // Get all bookings (Admin)
    public void getAllBookings(BookingsCallback callback) {
        database.child(Constants.BOOKINGS_NODE)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        java.util.ArrayList<Booking> bookings = new java.util.ArrayList<>();
                        for (DataSnapshot bookingSnapshot : snapshot.getChildren()) {
                            Booking booking = bookingSnapshot.getValue(Booking.class);
                            if (booking != null) {
                                bookings.add(booking);
                            }
                        }
                        callback.onSuccess(bookings);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    // Update booking status
    public void updateBookingStatus(String bookingId, String carId,
                                    String status, UpdateCallback callback) {
        database.child(Constants.BOOKINGS_NODE)
                .child(bookingId)
                .child("status")
                .setValue(status)
                .addOnSuccessListener(aVoid -> {
                    // If rejected, make car available again
                    if (status.equals(Constants.STATUS_REJECTED)) {
                        updateCarAvailability(carId, true);
                    }
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Delete booking
    public void deleteBooking(String bookingId, String carId, UpdateCallback callback) {
        database.child(Constants.BOOKINGS_NODE)
                .child(bookingId)
                .removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Make car available again
                    updateCarAvailability(carId, true);
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Helper method to update car availability
    private void updateCarAvailability(String carId, boolean available) {
        database.child(Constants.CARS_NODE)
                .child(carId)
                .child("availability")
                .setValue(available);
    }

    // Get booking by ID
    public void getBookingById(String bookingId, BookingCallback callback) {
        database.child(Constants.BOOKINGS_NODE)
                .child(bookingId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Booking booking = snapshot.getValue(Booking.class);
                            callback.onSuccess(booking);
                        } else {
                            callback.onError("Booking not found");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    // Callback interfaces
    public interface BookingCallback {
        void onSuccess(Booking booking);
        void onError(String error);
    }

    public interface BookingsCallback {
        void onSuccess(java.util.ArrayList<Booking> bookings);
        void onError(String error);
    }

    public interface UpdateCallback {
        void onSuccess();
        void onError(String error);
    }
}

