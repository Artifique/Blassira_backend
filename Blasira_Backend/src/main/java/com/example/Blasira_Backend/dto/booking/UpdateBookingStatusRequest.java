package com.example.Blasira_Backend.dto.booking;

import com.example.Blasira_Backend.model.enums.BookingStatus;
import lombok.Data;

/**
 * DTO for updating the status of a booking.
 */
@Data
public class UpdateBookingStatusRequest {

    /**
     * The new status for the booking.
     */
    private BookingStatus status;
}
