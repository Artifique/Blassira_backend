package com.example.Blasira_Backend.service.implementation;

import com.example.Blasira_Backend.dto.booking.BookingDto;
import com.example.Blasira_Backend.dto.trip.CreateTripRequest;
import com.example.Blasira_Backend.dto.trip.MyTripDetailsDto;
import com.example.Blasira_Backend.dto.trip.TripDto;
import com.example.Blasira_Backend.exception.DriverNotVerifiedException;
import com.example.Blasira_Backend.exception.UserNotFoundException;
import com.example.Blasira_Backend.exception.VehicleNotFoundException;
import com.example.Blasira_Backend.model.*;
import com.example.Blasira_Backend.model.enums.DriverProfileStatus;
import com.example.Blasira_Backend.model.enums.TripStatus;
import com.example.Blasira_Backend.repository.*;
import com.example.Blasira_Backend.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implémentation du service gérant la logique métier des trajets (Trips).
 * L'annotation @Service indique à Spring que cette classe est un composant de service.
 * L'annotation @RequiredArgsConstructor de Lombok génère un constructeur avec toutes les dépendances finales.
 */
@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    // Injection des dépendances nécessaires via le constructeur (géré par Lombok)
    private final TripRepository tripRepository;
    private final UserAccountRepository userAccountRepository;
    private final VehicleRepository vehicleRepository;
    private final BookingRepository bookingRepository;
    private final DriverProfileRepository driverProfileRepository;

    /**
     * Crée un nouveau trajet. Cette méthode est transactionnelle, ce qui signifie que toutes les opérations
     * sur la base de données seront annulées si une erreur survient.
     *
     * @param request Les données pour la création du trajet.
     * @param currentUser L'utilisateur actuellement authentifié (le futur conducteur).
     * @return Le trajet créé, sous forme de DTO.
     */
    @Override
    @Transactional // Assure l'atomicité de l'opération. Soit tout réussit, soit tout est annulé.
    public TripDto createTrip(CreateTripRequest request, UserDetails currentUser) {
        // 1. Récupérer l'entité complète de l'utilisateur à partir de son email (username).
        UserAccount user = userAccountRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Utilisateur authentifié non trouvé en base de données."));

        DriverProfile driverProfile = driverProfileRepository.findById(user.getId())
                .orElseThrow(() -> new DriverNotVerifiedException("Driver profile not found for authenticated user."));
        if (driverProfile == null || driverProfile.getStatus() != DriverProfileStatus.VERIFIED) {
            // C'est une règle métier critique pour la sécurité et la confiance de la plateforme.
            throw new DriverNotVerifiedException("Seuls les conducteurs vérifiés peuvent créer un trajet.");
        }

        // 3. Récupérer le véhicule et vérifier qu'il appartient bien à l'utilisateur.
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new VehicleNotFoundException("Véhicule non trouvé avec l'ID : " + request.getVehicleId()));
        if (!vehicle.getOwner().getId().equals(user.getId())) {
            // Règle de sécurité pour empêcher un conducteur d'utiliser le véhicule de quelqu'un d'autre.
            throw new IllegalStateException("Vous ne pouvez créer un trajet qu'avec votre propre véhicule.");
        }

        // 4. Créer et peupler la nouvelle entité Trip.
        Trip newTrip = new Trip();
        newTrip.setDriver(driverProfile);
        newTrip.setVehicle(vehicle);
        newTrip.setDepartureAddress(request.getDepartureAddress());
        newTrip.setDestinationAddress(request.getDestinationAddress());
        newTrip.setDepartureTime(request.getDepartureTime());
        newTrip.setPricePerSeat(request.getPricePerSeat());
        newTrip.setAvailableSeats(request.getAvailableSeats());
        // Le statut par défaut est déjà "PLANNED" dans l'entité, donc pas besoin de le définir ici.

        // 5. Sauvegarder l'entité en base de données.
        Trip savedTrip = tripRepository.save(newTrip);

        // 6. Mapper l'entité sauvegardée vers un DTO pour la réponse de l'API.
        // C'est une bonne pratique pour ne pas exposer directement les entités de la base de données.
        return mapToTripDto(savedTrip);
    }

    /**
     * Recherche des trajets basés sur des critères. La transaction est en lecture seule pour de meilleures performances.
     *
     * @param departure Le lieu de départ.
     * @param destination Le lieu d'arrivée.
     * @param date La date du trajet.
     * @return Une liste de trajets correspondants.
     */
    @Override
    @Transactional(readOnly = true)
    public List<TripDto> searchTrips(String departure, String destination, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Trip> trips = tripRepository.searchTrips(departure, destination, startOfDay, endOfDay);

        // Conversion de la liste d'entités Trip en liste de TripDto.
        return trips.stream()
                .map(this::mapToTripDto)
                .collect(Collectors.toList());
    }

    /**
     * Récupère tous les trajets créés par le conducteur actuellement authentifié.
     *
     * @param currentUser L'utilisateur authentifié.
     * @return Une liste de ses trajets avec les détails des réservations.
     */
    @Override
    @Transactional(readOnly = true)
    public List<MyTripDetailsDto> getMyTrips(UserDetails currentUser) {
        UserAccount user = userAccountRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Utilisateur authentifié non trouvé en base de données."));

        DriverProfile driverProfile = user.getDriverProfile();
        if (driverProfile == null) {
            // Si l'utilisateur n'est pas un conducteur, il n'a pas de trajets.
            return Collections.emptyList();
        }

        List<Trip> trips = tripRepository.findByDriver(driverProfile);

        return trips.stream()
                .map(this::mapToMyTripDetailsDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TripDto updateTripStatus(Long tripId, TripStatus newStatus, UserDetails currentUser) {
        UserAccount user = userAccountRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Utilisateur authentifié non trouvé."));

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trajet non trouvé avec l'ID : " + tripId));

        if (!trip.getDriver().getUserAccount().getId().equals(user.getId())) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à modifier le statut de ce trajet.");
        }

        trip.setStatus(newStatus);
        Trip updatedTrip = tripRepository.save(trip);
        return mapToTripDto(updatedTrip);
    }

    // --- Méthodes privées de mapping (conversion Entité -> DTO) ---

    /**
     * Convertit une entité Trip en DTO détaillé MyTripDetailsDto, incluant les réservations.
     */
    private MyTripDetailsDto mapToMyTripDetailsDto(Trip trip) {
        // Récupère toutes les réservations pour ce trajet.
        List<Booking> bookings = bookingRepository.findByTrip(trip);
        List<BookingDto> bookingDtos = bookings.stream()
                .map(this::mapToBookingDto)
                .collect(Collectors.toList());

        return MyTripDetailsDto.builder()
                .id(trip.getId())
                .departureAddress(trip.getDepartureAddress())
                .destinationAddress(trip.getDestinationAddress())
                .departureTime(trip.getDepartureTime())
                .pricePerSeat(trip.getPricePerSeat())
                .availableSeats(trip.getAvailableSeats())
                .vehicleModel(trip.getVehicle().getMake() + " " + trip.getVehicle().getModel())
                .bookings(bookingDtos)
                .build();
    }

    /**
     * Convertit une entité Trip en DTO public TripDto.
     */
    private TripDto mapToTripDto(Trip trip) {
        return TripDto.builder()
                .id(trip.getId())
                .driverName(trip.getDriver().getUserAccount().getUserProfile().getFirstName())
                .departureAddress(trip.getDepartureAddress())
                .destinationAddress(trip.getDestinationAddress())
                .departureTime(trip.getDepartureTime())
                .pricePerSeat(trip.getPricePerSeat())
                .availableSeats(trip.getAvailableSeats())
                .vehicleModel(trip.getVehicle().getMake() + " " + trip.getVehicle().getModel())
                .build();
    }

    /**
     * Convertit une entité Booking en BookingDto.
     */
    private BookingDto mapToBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .tripId(booking.getTrip().getId())
                .passengerName(booking.getPassenger().getFirstName())
                .bookedSeats(booking.getBookedSeats())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus())
                .build();
    }
}