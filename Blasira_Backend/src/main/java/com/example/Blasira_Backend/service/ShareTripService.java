package com.example.Blasira_Backend.service;

import com.example.Blasira_Backend.model.SharedTripLink;
import com.example.Blasira_Backend.model.Trip;
import com.example.Blasira_Backend.repository.SharedTripLinkRepository;
import com.example.Blasira_Backend.repository.TripRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShareTripService {

    private final SharedTripLinkRepository sharedTripLinkRepository;
    private final TripRepository tripRepository;

    public SharedTripLink createShareLink(Long tripId, Long userId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new EntityNotFoundException("Trip not found"));

        // Valider que l'utilisateur demandant le lien est le conducteur
        if (!trip.getDriver().getUserAccount().getId().equals(userId)) {
            throw new SecurityException("User is not the driver of the specified trip.");
        }

        SharedTripLink link = new SharedTripLink();
        link.setTrip(trip);
        link.setToken(UUID.randomUUID().toString());
        link.setExpiresAt(LocalDateTime.now().plusHours(24)); // Le lien est valide pendant 24 heures

        return sharedTripLinkRepository.save(link);
    }
}
