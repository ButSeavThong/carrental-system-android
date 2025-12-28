package com.example.fazonapp.data.repository;


import com.example.fazonapp.data.firebase.FirebaseManager;
import com.example.fazonapp.model.User;
import com.example.fazonapp.utils.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;

public class UserRepository {
    private DatabaseReference database;

    public UserRepository() {
        this.database = FirebaseManager.getInstance().getDatabase();
    }

    // Get user by ID
    public void getUserById(String userId, UserCallback callback) {
        database.child(Constants.USERS_NODE)
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);
                            callback.onSuccess(user);
                        } else {
                            callback.onError("User not found");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    // Update user profile
    public void updateUser(String userId, User user, UpdateCallback callback) {
        database.child(Constants.USERS_NODE)
                .child(userId)
                .setValue(user)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Get all users (Admin only)
    public void getAllUsers(UsersCallback callback) {
        database.child(Constants.USERS_NODE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        java.util.ArrayList<User> users = new java.util.ArrayList<>();
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            User user = userSnapshot.getValue(User.class);
                            if (user != null) {
                                users.add(user);
                            }
                        }
                        callback.onSuccess(users);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    // Delete user (Admin only)
    public void deleteUser(String userId, UpdateCallback callback) {
        database.child(Constants.USERS_NODE)
                .child(userId)
                .removeValue()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Callback interfaces
    public interface UserCallback {
        void onSuccess(User user);
        void onError(String error);
    }

    public interface UsersCallback {
        void onSuccess(java.util.ArrayList<User> users);
        void onError(String error);
    }

    public interface UpdateCallback {
        void onSuccess();
        void onError(String error);
    }
}