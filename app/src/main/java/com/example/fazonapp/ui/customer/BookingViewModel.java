package com.example.fazonapp.ui.customer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.fazonapp.data.repository.AuthRepository;
import com.example.fazonapp.data.repository.BookingRepository;
import com.example.fazonapp.dto.BookingDTO;
import com.example.fazonapp.model.Booking;
import com.example.fazonapp.utils.Constants;
import com.example.fazonapp.utils.DateUtils;
import com.example.fazonapp.utils.ModelMapper;
import java.util.List;

public class BookingViewModel extends ViewModel {
    private BookingRepository bookingRepository;
    private AuthRepository authRepository;

    private MutableLiveData<List<BookingDTO>> bookingsLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<String> successMessage = new MutableLiveData<>();

    public BookingViewModel() {
        bookingRepository = new BookingRepository();
        authRepository = new AuthRepository();
    }

    // Create new booking
    public void createBooking(String carId, String carName, long startDate, long endDate, double pricePerDay) {
        // Validation
        if (startDate >= endDate) {
            errorMessage.setValue("End date must be after start date");
            return;
        }

        if (startDate < DateUtils.getCurrentTimestamp()) {
            errorMessage.setValue("Start date cannot be in the past");
            return;
        }

        isLoading.setValue(true);

        String userId = authRepository.getCurrentUserId();
        long days = DateUtils.getDaysBetween(startDate, endDate);
        double totalPrice = days * pricePerDay;

        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setCarId(carId);
        booking.setCarName(carName);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setTotalPrice(totalPrice);
        booking.setStatus(Constants.STATUS_PENDING);

        bookingRepository.createBooking(booking, new BookingRepository.BookingCallback() {
            @Override
            public void onSuccess(Booking booking) {
                isLoading.setValue(false);
                successMessage.setValue("Booking created successfully!");
            }

            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    // Load user's bookings
    public void loadMyBookings() {
        isLoading.setValue(true);
        String userId = authRepository.getCurrentUserId();

        bookingRepository.getBookingsByUserId(userId, new BookingRepository.BookingsCallback() {
            @Override
            public void onSuccess(java.util.ArrayList<Booking> bookings) {
                isLoading.setValue(false);
                List<BookingDTO> dtos = ModelMapper.toBookingDTOList(bookings);
                bookingsLiveData.setValue(dtos);
            }

            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    // Getters
    public LiveData<List<BookingDTO>> getBookingsLiveData() {
        return bookingsLiveData;
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
