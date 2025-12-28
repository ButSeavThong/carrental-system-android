package com.example.fazonapp.data.repository;



import androidx.annotation.NonNull;
import com.example.fazonapp.data.firebase.FirebaseManager;
import com.example.fazonapp.model.User;
import com.example.fazonapp.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class AuthRepository {
    private FirebaseAuth auth;
    private DatabaseReference database;

    public AuthRepository() {
        FirebaseManager manager = FirebaseManager.getInstance();
        this.auth = manager.getAuth();
        this.database = manager.getDatabase();
    }

    // Register new user
    public void registerUser(String email, String password, User user,
                             OnCompleteListener<AuthResult> listener) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Get the generated user ID from Firebase Auth
                        String userId = task.getResult().getUser().getUid();
                        user.setId(userId);

                        // Save user data to Realtime Database
                        database.child(Constants.USERS_NODE)
                                .child(userId)
                                .setValue(user)
                                .addOnCompleteListener(dbTask -> {
                                    listener.onComplete(task);
                                });
                    } else {
                        listener.onComplete(task);
                    }
                });
    }

    // Login user
    public void loginUser(String email, String password,
                          OnCompleteListener<AuthResult> listener) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(listener);
    }

    // Logout user
    public void logoutUser() {
        auth.signOut();
    }

    // Get current user ID
    public String getCurrentUserId() {
        return auth.getCurrentUser() != null ?
                auth.getCurrentUser().getUid() : null;
    }

    // Check if user is logged in
    public boolean isUserLoggedIn() {
        return auth.getCurrentUser() != null;
    }
}
