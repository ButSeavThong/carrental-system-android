package com.example.fazonapp.utils;


import com.example.fazonapp.dto.BookingDTO;
import com.example.fazonapp.dto.CarDTO;
import com.example.fazonapp.dto.UserDTO;
import com.example.fazonapp.model.Booking;
import com.example.fazonapp.model.Car;
import com.example.fazonapp.model.User;
import java.util.ArrayList;
import java.util.List;

public class ModelMapper {

    // Convert User to UserDTO
    public static UserDTO toUserDTO(User user) {
        return user != null ? new UserDTO(user) : null;
    }

    // Convert List of Users to List of UserDTOs
    public static List<UserDTO> toUserDTOList(List<User> users) {
        List<UserDTO> dtos = new ArrayList<>();
        for (User user : users) {
            dtos.add(new UserDTO(user));
        }
        return dtos;
    }

    // Convert Car to CarDTO
    public static CarDTO toCarDTO(Car car) {
        return car != null ? new CarDTO(car) : null;
    }

    // Convert List of Cars to List of CarDTOs
    public static List<CarDTO> toCarDTOList(List<Car> cars) {
        List<CarDTO> dtos = new ArrayList<>();
        for (Car car : cars) {
            dtos.add(new CarDTO(car));
        }
        return dtos;
    }

    // Convert Booking to BookingDTO
    public static BookingDTO toBookingDTO(Booking booking) {
        return booking != null ? new BookingDTO(booking) : null;
    }

    // Convert List of Bookings to List of BookingDTOs
    public static List<BookingDTO> toBookingDTOList(List<Booking> bookings) {
        List<BookingDTO> dtos = new ArrayList<>();
        for (Booking booking : bookings) {
            dtos.add(new BookingDTO(booking));
        }
        return dtos;
    }
}