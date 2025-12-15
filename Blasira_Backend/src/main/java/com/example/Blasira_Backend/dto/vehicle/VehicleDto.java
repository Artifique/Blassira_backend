package com.example.Blasira_Backend.dto.vehicle;

import com.example.Blasira_Backend.model.enums.VehicleType;
import lombok.Builder;
import lombok.Data;

/**
 * DTO pour représenter un véhicule dans les réponses API.
 */
@Data
@Builder
public class VehicleDto {
    private Long id;
    private String make;
    private String model;
    private int year;
    private String licensePlate;
    private Integer capacity;
    private VehicleType type;
    private Long ownerId;
}
