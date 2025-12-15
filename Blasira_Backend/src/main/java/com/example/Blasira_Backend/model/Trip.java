package com.example.Blasira_Backend.model;

import com.example.Blasira_Backend.model.enums.TripStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private DriverProfile driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(name = "departure_address", nullable = false)
    private String departureAddress;

    @Column(name = "destination_address", nullable = false)
    private String destinationAddress;
    
    // Considérer l'utilisation d'un type Point approprié pour les requêtes GIS si nécessaire
    @Column(name = "departure_coordinates") 
    private String departureCoordinates; 
    @Column(name = "destination_coordinates")
    private String destinationCoordinates;

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @Column(name = "price_per_seat", nullable = false)
    private BigDecimal pricePerSeat;

    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripStatus status = TripStatus.PLANNED;
}
