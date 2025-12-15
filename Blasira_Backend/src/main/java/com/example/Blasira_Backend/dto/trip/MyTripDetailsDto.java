package com.example.Blasira_Backend.dto.trip;

import com.example.Blasira_Backend.dto.booking.BookingDto;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Un DTO détaillé pour un conducteur visualisant son propre trajet, incluant les réservations associées.
 */
@Data
@Builder
public class MyTripDetailsDto {

    private Long id;
    private String departureAddress;
    private String destinationAddress;
    private LocalDateTime departureTime;
    private BigDecimal pricePerSeat;
    private Integer availableSeats;
    private String vehicleModel;
    private List<BookingDto> bookings;

}
