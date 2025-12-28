package com.example.fazonapp.data.repository;

import android.net.Uri;
import com.example.fazonapp.data.firebase.FirebaseManager;
import com.example.fazonapp.model.Car;
import com.example.fazonapp.utils.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import androidx.annotation.NonNull;

public class CarRepository {
    private DatabaseReference database;
    private StorageReference storage;

    public CarRepository() {
        FirebaseManager manager = FirebaseManager.getInstance();
        this.database = manager.getDatabase();
        this.storage = manager.getStorage();
    }

    // Add new car with image
    public void addCar(Car car, Uri imageUri, CarCallback callback) {
        // Generate unique ID for car
        String carId = database.child(Constants.CARS_NODE).push().getKey();
        car.setId(carId);

        if (imageUri != null) {
            // Upload image first
            uploadCarImage(carId, imageUri, new ImageCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    car.setImageUrl(imageUrl);
                    saveCarToDatabase(car, callback);
                }

                @Override
                public void onError(String error) {
                    callback.onError(error);
                }
            });
        } else {
            // No image, save directly
            saveCarToDatabase(car, callback);
        }
    }

    // Upload car image to Firebase Storage
    private void uploadCarImage(String carId, Uri imageUri, ImageCallback callback) {
        StorageReference imageRef = storage.child("cars/" + carId + ".jpg");

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get download URL
                    imageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> callback.onSuccess(uri.toString()))
                            .addOnFailureListener(e -> callback.onError(e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Save car to database
    private void saveCarToDatabase(Car car, CarCallback callback) {
        database.child(Constants.CARS_NODE)
                .child(car.getId())
                .setValue(car)
                .addOnSuccessListener(aVoid -> callback.onSuccess(car))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Get all cars
    public void getAllCars(CarsCallback callback) {
        database.child(Constants.CARS_NODE)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        java.util.ArrayList<Car> cars = new java.util.ArrayList<>();
                        for (DataSnapshot carSnapshot : snapshot.getChildren()) {
                            Car car = carSnapshot.getValue(Car.class);
                            if (car != null) {
                                cars.add(car);
                            }
                        }
                        callback.onSuccess(cars);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    // Get available cars only
    public void getAvailableCars(CarsCallback callback) {
        database.child(Constants.CARS_NODE)
                .orderByChild("availability")
                .equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        java.util.ArrayList<Car> cars = new java.util.ArrayList<>();
                        for (DataSnapshot carSnapshot : snapshot.getChildren()) {
                            Car car = carSnapshot.getValue(Car.class);
                            if (car != null) {
                                cars.add(car);
                            }
                        }
                        callback.onSuccess(cars);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    // Get car by ID
    public void getCarById(String carId, CarCallback callback) {
        database.child(Constants.CARS_NODE)
                .child(carId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Car car = snapshot.getValue(Car.class);
                            callback.onSuccess(car);
                        } else {
                            callback.onError("Car not found");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    // Update car
    public void updateCar(Car car, Uri newImageUri, CarCallback callback) {
        if (newImageUri != null) {
            // Upload new image first
            uploadCarImage(car.getId(), newImageUri, new ImageCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    car.setImageUrl(imageUrl);
                    saveCarToDatabase(car, callback);
                }

                @Override
                public void onError(String error) {
                    callback.onError(error);
                }
            });
        } else {
            // No new image, update directly
            saveCarToDatabase(car, callback);
        }
    }

    // Delete car
    public void deleteCar(String carId, UpdateCallback callback) {
        // Delete from database
        database.child(Constants.CARS_NODE)
                .child(carId)
                .removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Delete image from storage
                    storage.child("cars/" + carId + ".jpg")
                            .delete()
                            .addOnSuccessListener(aVoid1 -> callback.onSuccess())
                            .addOnFailureListener(e -> callback.onSuccess()); // Still success even if image delete fails
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Update car availability
    public void updateCarAvailability(String carId, boolean available, UpdateCallback callback) {
        database.child(Constants.CARS_NODE)
                .child(carId)
                .child("availability")
                .setValue(available)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Callback interfaces
    public interface CarCallback {
        void onSuccess(Car car);
        void onError(String error);
    }

    public interface CarsCallback {
        void onSuccess(java.util.ArrayList<Car> cars);
        void onError(String error);
    }

    public interface ImageCallback {
        void onSuccess(String imageUrl);
        void onError(String error);
    }

    public interface UpdateCallback {
        void onSuccess();
        void onError(String error);
    }
}