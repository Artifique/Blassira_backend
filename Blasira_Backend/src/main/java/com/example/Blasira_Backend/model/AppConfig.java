package com.example.Blasira_Backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "app_config")
public class AppConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal baseFare;

    @Column(nullable = false)
    private BigDecimal pricePerKm;

    @Column(nullable = false)
    private String defaultCurrency;

    @Column(nullable = false)
    private boolean driverValidationRequired;

    // Constructeur par défaut pour JPA
    public AppConfig() {
        // Initialisation des valeurs par défaut si nécessaire
        this.baseFare = BigDecimal.valueOf(0.0);
        this.pricePerKm = BigDecimal.valueOf(0.0);
        this.defaultCurrency = "EUR";
        this.driverValidationRequired = true;
    }
}
