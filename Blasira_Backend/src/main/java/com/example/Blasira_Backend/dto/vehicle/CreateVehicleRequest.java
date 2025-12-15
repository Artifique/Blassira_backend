package com.example.Blasira_Backend.dto.vehicle;

import lombok.Data;

import com.example.Blasira_Backend.model.enums.VehicleType;

/**
 * DTO pour la création d'un nouveau véhicule.
 */
@Data
public class CreateVehicleRequest {
    private String make;
    private String model;
    private int year;
    private String licensePlate;
    private Integer capacity;
    private VehicleType type;
}
