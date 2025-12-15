package com.example.Blasira_Backend.service;

import com.example.Blasira_Backend.dto.vehicle.CreateVehicleRequest;
import com.example.Blasira_Backend.dto.vehicle.VehicleDto;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface VehicleService {
    VehicleDto addVehicle(CreateVehicleRequest request, UserDetails currentUser);
    List<VehicleDto> getMyVehicles(UserDetails currentUser);
    void deleteVehicle(Long vehicleId, UserDetails currentUser);
    List<VehicleDto> getAllVehicles();
}
