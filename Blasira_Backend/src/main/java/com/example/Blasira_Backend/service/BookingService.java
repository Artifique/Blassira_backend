package com.example.Blasira_Backend.service;

import com.example.Blasira_Backend.dto.booking.BookingDto;
import com.example.Blasira_Backend.dto.booking.CreateBookingRequest;
import com.example.Blasira_Backend.model.enums.BookingStatus;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * Service interface for booking management.
 */
public interface BookingService {

    /**
     * Creates a booking request for a trip on behalf of the current user.
     *
     * @param request The booking request details.
     * @param currentUser The user making the booking.
     * @return A DTO representing the newly created booking request.
     */
    BookingDto createBooking(CreateBookingRequest request, UserDetails currentUser);

    /**
     * Updates the status of a booking (e.g., confirms or rejects it).
     *
     * @param bookingId The ID of the booking to update.
     * @param newStatus The new status to set.
     * @param driverDetails The details of the user attempting the update (should be the driver).
     * @return The updated booking as a DTO.
     */
    BookingDto updateBookingStatus(Long bookingId, BookingStatus newStatus, UserDetails driverDetails);

    /**
     * Retrieves all bookings made by the currently authenticated user.
     *
     * @param currentUser The currently authenticated user.
     * @return A list of the user's bookings.
     */
    List<BookingDto> getMyBookings(UserDetails currentUser);

}
