package com.example.Blasira_Backend.service;

import com.example.Blasira_Backend.dto.review.CreateReviewRequest;
import com.example.Blasira_Backend.model.*;
import com.example.Blasira_Backend.model.enums.BookingStatus;
import com.example.Blasira_Backend.model.enums.ReviewType;
import com.example.Blasira_Backend.model.enums.TripStatus;
import com.example.Blasira_Backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final TripRepository tripRepository;
    private final DriverProfileRepository driverProfileRepository;

    @Transactional
    public Review createDriverReview(Long tripId, Long passengerId, CreateReviewRequest request) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new EntityNotFoundException("Trip not found"));

        // 1. Valider que le trajet est terminé
        if (trip.getStatus() != TripStatus.COMPLETED) {
            throw new IllegalStateException("Cannot review a trip that is not completed.");
        }

        // 2. Valider que l'évaluateur était un passager
        Booking booking = bookingRepository.findByTrip(trip).stream()
                .filter(b -> b.getPassenger().getId().equals(passengerId) && b.getStatus() == BookingStatus.CONFIRMED_BY_DRIVER)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("User was not a passenger on this trip."));

        // 3. Valider que l'utilisateur n'a pas déjà évalué cette réservation
        if (reviewRepository.existsByBooking(booking)) {
            throw new IllegalStateException("This booking has already been reviewed.");
        }

        DriverProfile driverProfile = trip.getDriver();
        if (driverProfile == null) {
            throw new IllegalStateException("Cannot review a trip with no associated driver.");
        }

        Review review = new Review();
        review.setBooking(booking);
        review.setAuthor(booking.getPassenger());
        review.setRecipient(driverProfile.getUserAccount().getUserProfile());
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setReviewType(ReviewType.PASSENGER_TO_DRIVER);

        Review savedReview = reviewRepository.save(review);

        // 4. Mettre à jour la note moyenne du conducteur
        updateDriverAverageRating(driverProfile, savedReview.getRating());

        return savedReview;
    }

    private void updateDriverAverageRating(DriverProfile driverProfile, int newRating) {
        driverProfile.setReviewCount(driverProfile.getReviewCount() + 1);
        driverProfile.setRatingSum(driverProfile.getRatingSum() + newRating);

        double average = (double) driverProfile.getRatingSum() / driverProfile.getReviewCount();
        driverProfile.setAverageRating(average);

        driverProfileRepository.save(driverProfile);
    }
}
