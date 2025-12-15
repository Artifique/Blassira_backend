package com.example.Blasira_Backend.dto.trip;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Objet de transfert de données pour créer un nouveau trajet.
 * Il transporte les informations nécessaires du client vers le serveur.
 */
@Data
public class CreateTripRequest {

    /**
     * L'ID du véhicule à utiliser pour le trajet.
     */
    private Long vehicleId;

    /**
     * L'adresse de départ du trajet.
     */
    private String departureAddress;

    /**
     * L'adresse de destination du trajet.
     */
    private String destinationAddress;

    /**
     * La date et l'heure exactes de départ.
     */
    private LocalDateTime departureTime;

    /**
     * Le prix par place pour un passager.
     */
    private BigDecimal pricePerSeat;

    /**
     * Le nombre de places disponibles pour les passagers.
     */
    private Integer availableSeats;
}
