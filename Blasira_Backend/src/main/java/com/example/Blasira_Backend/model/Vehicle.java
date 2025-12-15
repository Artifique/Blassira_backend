package com.example.Blasira_Backend.model;

import com.example.Blasira_Backend.model.enums.VehicleType;
import com.example.Blasira_Backend.model.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private UserAccount owner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType type;

    @Column(nullable = false)
    private String make;

    @Column(nullable = false)
    private String model;

    private int year;

    @Column(nullable = false)
    private Integer capacity; // Ajout du champ capacity

    @Column(name = "license_plate", nullable = false, unique = true)
    private String licensePlate;
    
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING_REVIEW;
}
