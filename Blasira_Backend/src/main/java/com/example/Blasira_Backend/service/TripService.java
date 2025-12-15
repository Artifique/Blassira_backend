package com.example.Blasira_Backend.service;

import com.example.Blasira_Backend.dto.trip.CreateTripRequest;
import com.example.Blasira_Backend.dto.trip.MyTripDetailsDto;
import com.example.Blasira_Backend.dto.trip.TripDto;
import com.example.Blasira_Backend.model.enums.TripStatus;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for trip management.
 */
public interface TripService {

    /**
     * Creates a new trip for the currently authenticated user.
     *
     * @param request The request object containing trip details.
     * @param currentUser The currently authenticated user.
     * @return A DTO representing the newly created trip.
     */
    TripDto createTrip(CreateTripRequest request, UserDetails currentUser);

    /**
     * Searches for available trips based on specified criteria.
     *
     * @param departure The departure location.
     * @param destination The destination location.
     * @param date The date of the trip.
     * @return A list of available trips matching the criteria.
     */
    List<TripDto> searchTrips(String departure, String destination, LocalDate date);

    /**
     * Récupère tous les trajets créés par l'utilisateur actuellement authentifié (conducteur), incluant leurs réservations.
     *
     * @param currentUser Le conducteur faisant la requête, injecté par Spring Security.
     * @return Une liste des trajets du conducteur avec les informations détaillées des réservations.
     */
    List<MyTripDetailsDto> getMyTrips(UserDetails currentUser);

    /**
     * Met à jour le statut d'un trajet.
     *
     * @param tripId L'ID du trajet à mettre à jour.
     * @param newStatus Le nouveau statut du trajet.
     * @param currentUser L'utilisateur authentifié (doit être le conducteur du trajet).
     * @return Le DTO du trajet mis à jour.
     */
    TripDto updateTripStatus(Long tripId, TripStatus newStatus, UserDetails currentUser);
}
