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


    /**
     * Recherche des trajets basés sur le lieu de départ, le lieu de destination et un jour spécifique.
     * La requête est insensible à la casse et recherche des correspondances partielles dans les adresses.
     * @param departure La chaîne de lieu de départ à rechercher.
     * @param destination La chaîne de lieu de destination à rechercher.
     * @param startOfDay Le début du jour dans lequel rechercher.
     * @param endOfDay La fin du jour dans lequel rechercher.
     * @return Une liste de trajets correspondants.
     */
    @Query("SELECT t FROM Trip t WHERE " +
           "LOWER(t.departureAddress) LIKE LOWER(CONCAT('%', :departure, '%')) AND " +
           "LOWER(t.destinationAddress) LIKE LOWER(CONCAT('%', :destination, '%')) AND " +
           "t.departureTime BETWEEN :startOfDay AND :endOfDay AND " +
           "t.status = 'PLANNED'")
    List<Trip> searchTrips(@Param("departure") String departure,
                           @Param("destination") String destination,
                           @Param("startOfDay") LocalDateTime startOfDay,
                           @Param("endOfDay") LocalDateTime endOfDay);

    /**
     * Trouve tous les trajets créés par un conducteur spécifique.
     * @param driver Le profil du conducteur.
     * @return Une liste de trajets pour ce conducteur.
     */
    List<Trip> findByDriver(DriverProfile driver);
}
