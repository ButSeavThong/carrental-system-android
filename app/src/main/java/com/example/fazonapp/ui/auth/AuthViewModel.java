package com.example.fazonapp.ui.auth;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.fazonapp.data.repository.AuthRepository;
import com.example.fazonapp.data.repository.UserRepository;
import com.example.fazonapp.dto.RegisterDTO;
import com.example.fazonapp.model.User;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class AuthViewModel extends ViewModel {
    private AuthRepository authRepository;
    private UserRepository userRepository;

    // LiveData for observing authentication state
    private MutableLiveData<AuthResult> authResult = new MutableLiveData<>();
    private MutableLiveData<User> currentUser = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public AuthViewModel() {
        authRepository = new AuthRepository();
        userRepository = new UserRepository();
    }

    // Login
    public void login(String email, String password) {
        // Validation
        if (!validateLoginInput(email, password)) {
            return;
        }

        isLoading.setValue(true);

        authRepository.loginUser(email, password, task -> {
            isLoading.setValue(false);

            if (task.isSuccessful()) {
                String userId = authRepository.getCurrentUserId();
                loadUserData(userId);
            } else {
                String errorMessage = getErrorMessage(task.getException());
                authResult.setValue(new AuthResult(false, errorMessage));
            }
        });
    }

    // Register
    public void register(RegisterDTO registerDTO) {
        // Validation
        if (!registerDTO.isValid()) {
            authResult.setValue(new AuthResult(false, "Please fill all fields correctly"));
            return;
        }

        isLoading.setValue(true);

        User user = registerDTO.toUser();

        authRepository.registerUser(
                registerDTO.getEmail(),
                registerDTO.getPassword(),
                user,
                task -> {
                    isLoading.setValue(false);

                    if (task.isSuccessful()) {
                        authResult.setValue(new AuthResult(true, "Registration successful!"));
                        loadUserData(user.getId());
                    } else {
                        String errorMessage = getErrorMessage(task.getException());
                        authResult.setValue(new AuthResult(false, errorMessage));
                    }
                }
        );
    }

    // Load user data after successful authentication
    private void loadUserData(String userId) {
        userRepository.getUserById(userId, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                currentUser.setValue(user);
                authResult.setValue(new AuthResult(true, "Login successful!", user.getRole()));
            }

            @Override
            public void onError(String error) {
                authResult.setValue(new AuthResult(false, error));
            }
        });
    }

    // Validate login input
    private boolean validateLoginInput(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            authResult.setValue(new AuthResult(false, "Email is required"));
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            authResult.setValue(new AuthResult(false, "Invalid email format"));
            return false;
        }

        if (password == null || password.length() < 6) {
            authResult.setValue(new AuthResult(false, "Password must be at least 6 characters"));
            return false;
        }

        return true;
    }

    // Get user-friendly error messages
    private String getErrorMessage(Exception exception) {
        if (exception instanceof FirebaseAuthWeakPasswordException) {
            return "Password is too weak";
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            return "Invalid email or password";
        } else if (exception instanceof FirebaseAuthUserCollisionException) {
            return "Email already exists";
        } else if (exception != null) {
            return exception.getMessage();
        }
        return "Authentication failed";
    }

    // Check if user is already logged in
    public boolean isUserLoggedIn() {
        return authRepository.isUserLoggedIn();
    }

    // Getters for LiveData
    public LiveData<AuthResult> getAuthResult() {
        return authResult;
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    // Result class
    public static class AuthResult {
        private boolean success;
        private String message;
        private String userRole;

        public AuthResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public AuthResult(boolean success, String message, String userRole) {
            this.success = success;
            this.message = message;
            this.userRole = userRole;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getUserRole() { return userRole; }
    }
}