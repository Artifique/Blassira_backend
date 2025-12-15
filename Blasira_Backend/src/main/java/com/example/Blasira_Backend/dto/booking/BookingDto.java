package com.example.Blasira_Backend.dto.booking;

import com.example.Blasira_Backend.model.enums.BookingStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Objet de transfert de données pour afficher les informations de réservation.
 */
@Data
@Builder
public class BookingDto {

    private Long id;
    private Long tripId;
    private String passengerName;
    private Integer bookedSeats;
    private BigDecimal totalPrice;
    private BookingStatus status;

}
