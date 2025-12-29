package com.example.fazonapp.ui.admin;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.fazonapp.data.repository.UserRepository;
import com.example.fazonapp.dto.UserDTO;
import com.example.fazonapp.utils.ModelMapper;
import java.util.List;

public class AdminUserViewModel extends ViewModel {
    private UserRepository userRepository;

    private MutableLiveData<List<UserDTO>> usersLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<String> successMessage = new MutableLiveData<>();

    public AdminUserViewModel() {
        userRepository = new UserRepository();
    }

    // Load all users
    public void loadAllUsers() {
        isLoading.setValue(true);

        userRepository.getAllUsers(new UserRepository.UsersCallback() {
            @Override
            public void onSuccess(java.util.ArrayList<com.example.fazonapp.model.User> users) {
                isLoading.setValue(false);
                List<UserDTO> dtos = ModelMapper.toUserDTOList(users);
                usersLiveData.setValue(dtos);
            }

            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    // Delete user
    public void deleteUser(String userId) {
        isLoading.setValue(true);

        userRepository.deleteUser(userId, new UserRepository.UpdateCallback() {
            @Override
            public void onSuccess() {
                isLoading.setValue(false);
                successMessage.setValue("User deleted successfully!");
                loadAllUsers(); // Refresh the list
            }

            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    // Update user role (promote/demote)
    public void updateUserRole(String userId, String currentRole) {
        isLoading.setValue(true);

        userRepository.getUserById(userId, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(com.example.fazonapp.model.User user) {
                // Toggle role
                String newRole = user.getRole().equals("ADMIN") ? "CUSTOMER" : "ADMIN";
                user.setRole(newRole);

                userRepository.updateUser(userId, user, new UserRepository.UpdateCallback() {
                    @Override
                    public void onSuccess() {
                        isLoading.setValue(false);
                        successMessage.setValue("User role updated to " + newRole);
                        loadAllUsers(); // Refresh the list
                    }

                    @Override
                    public void onError(String error) {
                        isLoading.setValue(false);
                        errorMessage.setValue(error);
                    }
                });
            }

            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    // Getters for LiveData
    public LiveData<List<UserDTO>> getUsersLiveData() {
        return usersLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }
}