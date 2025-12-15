package com.example.Blasira_Backend.controller;

import com.example.Blasira_Backend.dto.trip.CreateTripRequest;
import com.example.Blasira_Backend.dto.trip.MyTripDetailsDto;
import com.example.Blasira_Backend.dto.trip.TripDto;
import com.example.Blasira_Backend.model.enums.TripStatus;
import com.example.Blasira_Backend.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Contrôleur gérant les requêtes API liées aux trajets.
 */
@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @PostMapping
    public ResponseEntity<TripDto> createTrip(
            @RequestBody CreateTripRequest createTripRequest,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        TripDto createdTrip = tripService.createTrip(createTripRequest, currentUser);
        return new ResponseEntity<>(createdTrip, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TripDto>> searchTrips(
            @RequestParam String departure,
            @RequestParam String destination,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<TripDto> trips = tripService.searchTrips(departure, destination, date);
        return ResponseEntity.ok(trips);
    }

    /**
     * Récupère tous les trajets créés par l'utilisateur actuellement authentifié (conducteur), incluant leurs réservations.
     *
     * @param currentUser Le conducteur faisant la requête, injecté par Spring Security.
     * @return Une liste des trajets du conducteur avec les informations détaillées des réservations.
     */
    @GetMapping("/my-trips")
    public ResponseEntity<List<MyTripDetailsDto>> getMyTrips(@AuthenticationPrincipal UserDetails currentUser) {
        List<MyTripDetailsDto> trips = tripService.getMyTrips(currentUser);
        return ResponseEntity.ok(trips);
    }

    @PatchMapping("/{tripId}/status")
    public ResponseEntity<TripDto> updateTripStatus(
            @PathVariable Long tripId,
            @RequestParam TripStatus newStatus,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        TripDto updatedTrip = tripService.updateTripStatus(tripId, newStatus, currentUser);
        return ResponseEntity.ok(updatedTrip);
    }
}
