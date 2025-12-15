package com.example.Blasira_Backend.controller;

import com.example.Blasira_Backend.dto.location.LocationUpdateDto;
import com.example.Blasira_Backend.model.Trip;
import com.example.Blasira_Backend.repository.TripRepository;
import com.example.Blasira_Backend.service.LocationCacheService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class LocationController {

    private final LocationCacheService locationCacheService;
    private final TripRepository tripRepository;

    /**
     * Gère les mises à jour de localisation d'un conducteur pour un trajet spécifique.
     * Le conducteur envoie un message à /app/trip/{tripId}/location.
     * Le message est ensuite diffusé à tous les abonnés de /topic/trip/{tripId}/location.
     *
     * @param tripId  L'ID du trajet.
     * @param location La mise à jour de localisation du conducteur.
     * @return La mise à jour de localisation, qui est diffusée aux abonnés.
     */
    @MessageMapping("/trip/{tripId}/location")
    @SendTo("/topic/trip/{tripId}/location")
    public LocationUpdateDto updateLocation(
            @DestinationVariable Long tripId,
            LocationUpdateDto location,
            Principal principal) {

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new EntityNotFoundException("Trip not found"));

        // Vérification de sécurité : S'assurer que l'utilisateur envoyant la localisation est le conducteur du trajet
        if (!trip.getDriver().getUserAccount().getEmail().equals(principal.getName())) {
            throw new AccessDeniedException("User is not the driver of this trip.");
        }

        locationCacheService.updateLocation(tripId, location);

        return location;
    }
}
