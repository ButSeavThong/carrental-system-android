package com.example.fazonapp.ui.admin;

import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.fazonapp.data.repository.CarRepository;
import com.example.fazonapp.dto.CarDTO;
import com.example.fazonapp.model.Car;
import com.example.fazonapp.utils.ModelMapper;
import java.util.List;

public class AdminCarViewModel extends ViewModel {
    private CarRepository carRepository;

    private MutableLiveData<List<CarDTO>> carsLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<String> successMessage = new MutableLiveData<>();

    public AdminCarViewModel() {
        carRepository = new CarRepository();
    }

    // Load all cars
    public void loadAllCars() {
        isLoading.setValue(true);

        carRepository.getAllCars(new CarRepository.CarsCallback() {
            @Override
            public void onSuccess(java.util.ArrayList<Car> cars) {
                isLoading.setValue(false);
                List<CarDTO> dtos = ModelMapper.toCarDTOList(cars);
                carsLiveData.setValue(dtos);
            }

            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    // Add new car
    public void addCar(String name, String brand, String type,
                       double pricePerDay, boolean availability, Uri imageUri) {
        isLoading.setValue(true);

        Car car = new Car();
        car.setName(name);
        car.setBrand(brand);
        car.setType(type);
        car.setPricePerDay(pricePerDay);
        car.setAvailability(availability);

        carRepository.addCar(car, imageUri, new CarRepository.CarCallback() {
            @Override
            public void onSuccess(Car car) {
                isLoading.setValue(false);
                successMessage.setValue("Car added successfully!");
                loadAllCars(); // Refresh the list
            }

            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    // Update existing car
    public void updateCar(String carId, String name, String brand, String type,
                          double pricePerDay, boolean availability, Uri newImageUri) {
        isLoading.setValue(true);

        Car car = new Car();
        car.setId(carId);
        car.setName(name);
        car.setBrand(brand);
        car.setType(type);
        car.setPricePerDay(pricePerDay);
        car.setAvailability(availability);

        carRepository.updateCar(car, newImageUri, new CarRepository.CarCallback() {
            @Override
            public void onSuccess(Car car) {
                isLoading.setValue(false);
                successMessage.setValue("Car updated successfully!");
                loadAllCars(); // Refresh the list
            }

            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    // Delete car
    public void deleteCar(String carId) {
        isLoading.setValue(true);

        carRepository.deleteCar(carId, new CarRepository.UpdateCallback() {
            @Override
            public void onSuccess() {
                isLoading.setValue(false);
                successMessage.setValue("Car deleted successfully!");
                loadAllCars(); // Refresh the list
            }

            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    // Getters for LiveData
    public LiveData<List<CarDTO>> getCarsLiveData() {
        return carsLiveData;
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