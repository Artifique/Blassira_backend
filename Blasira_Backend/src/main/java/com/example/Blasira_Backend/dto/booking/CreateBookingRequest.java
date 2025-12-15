package com.example.Blasira_Backend.dto.booking;

import lombok.Data;

/**
 * Objet de transfert de données pour demander une nouvelle réservation.
 */
@Data
public class CreateBookingRequest {

    /**
     * L'identifiant unique du trajet à réserver.
     */
    private Long tripId;

    /**
     * Le nombre de places que le passager souhaite réserver.
     */
    private Integer numberOfSeats;

    /**
     * Un code promotionnel optionnel à appliquer à la réservation.
     */
    private String promoCode;
}
