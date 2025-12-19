package com.example.Blasira_Backend.repository;

import com.example.Blasira_Backend.model.Booking;
import com.example.Blasira_Backend.model.Trip;
import com.example.Blasira_Backend.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT SUM(b.totalPrice) FROM Booking b WHERE b.status = 'CONFIRMED_BY_DRIVER'")
    BigDecimal sumTotalRevenueConfirmedBookings();

    List<Booking> findByPassenger(UserProfile userProfile);

    List<Booking> findByTrip(Trip trip);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Booking> findTop5ByOrderByCreatedAtDesc();
}
