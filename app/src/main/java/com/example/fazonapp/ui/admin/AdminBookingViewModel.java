package com.example.fazonapp.ui.admin;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.fazonapp.data.repository.BookingRepository;
import com.example.fazonapp.dto.BookingDTO;
import com.example.fazonapp.utils.Constants;
import com.example.fazonapp.utils.ModelMapper;
import java.util.List;

public class AdminBookingViewModel extends ViewModel {
    private BookingRepository bookingRepository;

    private MutableLiveData<List<BookingDTO>> bookingsLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<String> successMessage = new MutableLiveData<>();

    public AdminBookingViewModel() {
        bookingRepository = new BookingRepository();
    }

    // Load all bookings
    public void loadAllBookings() {
        isLoading.setValue(true);

        bookingRepository.getAllBookings(new BookingRepository.BookingsCallback() {
            @Override
            public void onSuccess(java.util.ArrayList<com.example.fazonapp.model.Booking> bookings) {
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

    // Approve booking
    public void approveBooking(String bookingId, String carId) {
        updateBookingStatus(bookingId, carId, Constants.STATUS_APPROVED);
    }

    // Reject booking
    public void rejectBooking(String bookingId, String carId) {
        updateBookingStatus(bookingId, carId, Constants.STATUS_REJECTED);
    }

    // Update booking status
    private void updateBookingStatus(String bookingId, String carId, String status) {
        isLoading.setValue(true);

        bookingRepository.updateBookingStatus(bookingId, carId, status,
                new BookingRepository.UpdateCallback() {
                    @Override
                    public void onSuccess() {
                        isLoading.setValue(false);
                        successMessage.setValue("Booking " + status.toLowerCase() + " successfully!");
                        loadAllBookings(); // Refresh the list
                    }

                    @Override
                    public void onError(String error) {
                        isLoading.setValue(false);
                        errorMessage.setValue(error);
                    }
                });
    }

    // Delete booking
    public void deleteBooking(String bookingId, String carId) {
        isLoading.setValue(true);

        bookingRepository.deleteBooking(bookingId, carId, new BookingRepository.UpdateCallback() {
            @Override
            public void onSuccess() {
                isLoading.setValue(false);
                successMessage.setValue("Booking deleted successfully!");
                loadAllBookings(); // Refresh the list
            }

            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    // Filter bookings by status
    public void filterBookingsByStatus(String status, List<BookingDTO> allBookings) {
        if (status.equals("All")) {
            bookingsLiveData.setValue(allBookings);
            return;
        }

        List<BookingDTO> filtered = new java.util.ArrayList<>();
        for (BookingDTO booking : allBookings) {
            if (booking.getStatus().equals(status)) {
                filtered.add(booking);
            }
        }
        bookingsLiveData.setValue(filtered);
    }

    // Getters for LiveData
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