package com.example.Blasira_Backend.controller;

import com.example.Blasira_Backend.dto.booking.BookingDto;
import com.example.Blasira_Backend.dto.booking.CreateBookingRequest;
import com.example.Blasira_Backend.dto.booking.UpdateBookingStatusRequest;
import com.example.Blasira_Backend.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur gérant les requêtes API liées aux réservations.
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    /**
     * Crée une demande de réservation pour un trajet. Ceci est un endpoint protégé.
     *
     * @param request Le corps de la requête contenant l'ID du trajet et le nombre de places.
     * @param currentUser L'utilisateur actuellement authentifié, injecté par Spring Security.
     * @return Une ResponseEntity contenant le BookingDto créé et le statut HTTP 201 (Created).
     */
    @PostMapping
    public ResponseEntity<BookingDto> createBooking(
            @RequestBody CreateBookingRequest request,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        BookingDto createdBooking = bookingService.createBooking(request, currentUser);
        return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
    }

    /**
     * Met à jour le statut d'une réservation (ex: confirme ou rejette).
     * Ceci est un endpoint protégé destiné au conducteur du trajet.
     *
     * @param bookingId L'ID de la réservation à mettre à jour.
     * @param request Le corps de la requête contenant le nouveau statut.
     * @param driverDetails Le conducteur faisant la requête, injecté par Spring Security.
     * @return La réservation mise à jour.
     */
    @PatchMapping("/{bookingId}/status")
    public ResponseEntity<BookingDto> updateBookingStatus(
            @PathVariable Long bookingId,
            @RequestBody UpdateBookingStatusRequest request,
            @AuthenticationPrincipal UserDetails driverDetails
    ) {
        BookingDto updatedBooking = bookingService.updateBookingStatus(bookingId, request.getStatus(), driverDetails);
        return ResponseEntity.ok(updatedBooking);
    }

    /**
     * Récupère toutes les réservations effectuées par l'utilisateur actuellement authentifié.
     *
     * @param currentUser L'utilisateur faisant la requête, injecté par Spring Security.
     * @return Une liste des réservations de l'utilisateur.
     */
    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingDto>> getMyBookings(@AuthenticationPrincipal UserDetails currentUser) {
        List<BookingDto> bookings = bookingService.getMyBookings(currentUser);
        return ResponseEntity.ok(bookings);
    }
}
