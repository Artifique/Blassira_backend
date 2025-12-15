package com.example.Blasira_Backend.service.implementation;

import com.example.Blasira_Backend.dto.booking.BookingDto;
import com.example.Blasira_Backend.dto.booking.CreateBookingRequest;
import com.example.Blasira_Backend.model.Booking;
import com.example.Blasira_Backend.model.PromoCode;
import com.example.Blasira_Backend.model.Trip;
import com.example.Blasira_Backend.model.UserAccount;
import com.example.Blasira_Backend.model.enums.BookingStatus;
import com.example.Blasira_Backend.model.enums.TripStatus;
import com.example.Blasira_Backend.repository.BookingRepository;
import com.example.Blasira_Backend.repository.TripRepository;
import com.example.Blasira_Backend.repository.UserAccountRepository;
import com.example.Blasira_Backend.service.BookingService;
import com.example.Blasira_Backend.service.PromoCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implémentation de l'interface BookingService.
 */
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final TripRepository tripRepository;
    private final UserAccountRepository userAccountRepository;
    private final PromoCodeService promoCodeService;

    @Override
    @Transactional
    public BookingDto createBooking(CreateBookingRequest request, UserDetails currentUser) {
        UserAccount passenger = userAccountRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found."));

        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new IllegalArgumentException("Trip not found."));

        if (trip.getStatus() != TripStatus.PLANNED) {
            throw new IllegalStateException("This trip is not available for booking.");
        }
        if (trip.getDriver().getUserAccount().getId().equals(passenger.getId())) {
            throw new IllegalStateException("You cannot book a seat on your own trip.");
        }
        if (trip.getAvailableSeats() < request.getNumberOfSeats()) {
            throw new IllegalStateException("Not enough available seats on this trip.");
        }

        Booking newBooking = new Booking();
        newBooking.setPassenger(passenger.getUserProfile());
        newBooking.setTrip(trip);
        newBooking.setBookedSeats(request.getNumberOfSeats());

        BigDecimal originalPrice = trip.getPricePerSeat().multiply(new BigDecimal(request.getNumberOfSeats()));
        BigDecimal finalPrice = originalPrice;

        if (request.getPromoCode() != null && !request.getPromoCode().isEmpty()) {
            PromoCode promoCode = promoCodeService.validateAndApplyPromoCode(request.getPromoCode(), originalPrice);
            finalPrice = promoCodeService.calculateDiscountedPrice(promoCode, originalPrice);
            newBooking.setPromoCode(promoCode);
            newBooking.setDiscountAmount(originalPrice.subtract(finalPrice));
        }

        newBooking.setTotalPrice(finalPrice);
        newBooking.setStatus(BookingStatus.REQUESTED_BY_PASSENGER);

        Booking savedBooking = bookingRepository.save(newBooking);

        return mapToBookingDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingDto updateBookingStatus(Long bookingId, BookingStatus newStatus, UserDetails driverDetails) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));
        UserAccount driver = userAccountRepository.findByEmail(driverDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("Authenticated driver not found."));

        if (!booking.getTrip().getDriver().getUserAccount().getId().equals(driver.getId())) {
            throw new SecurityException("You are not authorized to update this booking.");
        }

        if (newStatus == BookingStatus.CONFIRMED_BY_DRIVER) {
            if (booking.getStatus() != BookingStatus.REQUESTED_BY_PASSENGER) {
                throw new IllegalStateException("This booking is not awaiting confirmation.");
            }
            // Verrouiller le trajet pour éviter les conditions de course sur les places disponibles
            Trip trip = tripRepository.findAndLockById(booking.getTrip().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Trip not found."));

            if (trip.getAvailableSeats() < booking.getBookedSeats()) {
                throw new IllegalStateException("Not enough seats available to confirm this booking.");
            }
            trip.setAvailableSeats(trip.getAvailableSeats() - booking.getBookedSeats());
            tripRepository.save(trip);
        }

        booking.setStatus(newStatus);
        Booking updatedBooking = bookingRepository.save(booking);

        return mapToBookingDto(updatedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getMyBookings(UserDetails currentUser) {
        UserAccount passenger = userAccountRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found."));

        List<Booking> bookings = bookingRepository.findByPassenger(passenger.getUserProfile());

        return bookings.stream()
                .map(this::mapToBookingDto)
                .collect(Collectors.toList());
    }

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