package com.example.Blasira_Backend.repository;

import com.example.Blasira_Backend.model.Booking;
import com.example.Blasira_Backend.model.Trip;
import com.example.Blasira_Backend.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT SUM(b.totalPrice) FROM Booking b WHERE b.status = 'CONFIRMED_BY_DRIVER'")
    BigDecimal sumTotalRevenueConfirmedBookings();

    /**
     * Finds all bookings made by a specific passenger.
     * @param userProfile The user profile of the passenger.
     * @return A list of bookings for that passenger.
     */
    List<Booking> findByPassenger(UserProfile userProfile);

    /**
     * Finds all bookings for a specific trip.
     * @param trip The trip.
     * @return A list of bookings for that trip.
     */
    List<Booking> findByTrip(Trip trip);
}
