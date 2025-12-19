package com.example.Blasira_Backend.repository;

import com.example.Blasira_Backend.model.DriverProfile;
import com.example.Blasira_Backend.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Trip> findAndLockById(Long id);

    @Query("SELECT t FROM Trip t WHERE " +
           "LOWER(t.departureAddress) LIKE LOWER(CONCAT('%', :departure, '%')) AND " +
           "LOWER(t.destinationAddress) LIKE LOWER(CONCAT('%', :destination, '%')) AND " +
           "t.departureTime BETWEEN :startOfDay AND :endOfDay AND " +
           "t.status = 'PLANNED'")
    List<Trip> searchTrips(@Param("departure") String departure,
                           @Param("destination") String destination,
                           @Param("startOfDay") LocalDateTime startOfDay,
                           @Param("endOfDay") LocalDateTime endOfDay);

    List<Trip> findByDriver(DriverProfile driver);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Trip> findTop5ByOrderByCreatedAtDesc();
}
