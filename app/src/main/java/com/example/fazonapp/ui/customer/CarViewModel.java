package com.example.fazonapp.ui.customer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.fazonapp.data.repository.CarRepository;
import com.example.fazonapp.dto.CarDTO;
import com.example.fazonapp.model.Car;
import com.example.fazonapp.utils.ModelMapper;
import java.util.ArrayList;
import java.util.List;

public class CarViewModel extends ViewModel {
    private CarRepository carRepository;
    private MutableLiveData<List<CarDTO>> carsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<CarDTO>> filteredCarsLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private List<CarDTO> allCars = new ArrayList<>();

    public CarViewModel() {
        carRepository = new CarRepository();
    }

    // Load all available cars
    public void loadAvailableCars() {
        isLoading.setValue(true);

        carRepository.getAvailableCars(new CarRepository.CarsCallback() {
            @Override
            public void onSuccess(ArrayList<Car> cars) {
                isLoading.setValue(false);
                allCars = ModelMapper.toCarDTOList(cars);
                carsLiveData.setValue(allCars);
                filteredCarsLiveData.setValue(allCars);
            }

            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    // Filter cars by type
    public void filterCarsByType(String type) {
        if (type == null || type.equals("All")) {
            filteredCarsLiveData.setValue(allCars);
            return;
        }

        List<CarDTO> filtered = new ArrayList<>();
        for (CarDTO car : allCars) {
            if (car.getType().equalsIgnoreCase(type)) {
                filtered.add(car);
            }
        }
        filteredCarsLiveData.setValue(filtered);
    }

    // Get car by ID
    public void getCarById(String carId, CarCallback callback) {
        carRepository.getCarById(carId, new CarRepository.CarCallback() {
            @Override
            public void onSuccess(Car car) {
                callback.onSuccess(ModelMapper.toCarDTO(car));
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    // Getters
    public LiveData<List<CarDTO>> getCarsLiveData() {
        return carsLiveData;
    }

    public LiveData<List<CarDTO>> getFilteredCarsLiveData() {
        return filteredCarsLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    // Callback interface
    public interface CarCallback {
        void onSuccess(CarDTO car);
        void onError(String error);
    }
}