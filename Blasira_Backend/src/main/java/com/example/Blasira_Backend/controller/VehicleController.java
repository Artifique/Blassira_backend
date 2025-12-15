package com.example.Blasira_Backend.controller;

import com.example.Blasira_Backend.dto.vehicle.CreateVehicleRequest;
import com.example.Blasira_Backend.dto.vehicle.VehicleDto;
import com.example.Blasira_Backend.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<VehicleDto> addVehicle(
            @RequestBody CreateVehicleRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {
        VehicleDto newVehicle = vehicleService.addVehicle(request, currentUser);
        return new ResponseEntity<>(newVehicle, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<List<VehicleDto>> getMyVehicles(@AuthenticationPrincipal UserDetails currentUser) {
        List<VehicleDto> vehicles = vehicleService.getMyVehicles(currentUser);
        return ResponseEntity.ok(vehicles);
    }

    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<Void> deleteVehicle(
            @PathVariable Long vehicleId,
            @AuthenticationPrincipal UserDetails currentUser) {
        vehicleService.deleteVehicle(vehicleId, currentUser);
        return ResponseEntity.noContent().build();
    }
}
