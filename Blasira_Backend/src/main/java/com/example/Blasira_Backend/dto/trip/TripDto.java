package com.example.Blasira_Backend.dto.trip;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Objet de transfert de données pour afficher les informations de trajet.
 * Ceci est une représentation d'un Trip qui est sûre à exposer aux clients.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripDto {

    private Long id;
    private String driverName;
    private String departureAddress;
    private String destinationAddress;
    private LocalDateTime departureTime;
    private BigDecimal pricePerSeat;
    private Integer availableSeats;
    private String vehicleModel;

}
