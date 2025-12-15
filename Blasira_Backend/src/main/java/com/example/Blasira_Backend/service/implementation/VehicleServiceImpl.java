package com.example.Blasira_Backend.service.implementation;

import com.example.Blasira_Backend.dto.vehicle.CreateVehicleRequest;
import com.example.Blasira_Backend.dto.vehicle.VehicleDto;
import com.example.Blasira_Backend.exception.UserNotFoundException;
import com.example.Blasira_Backend.model.UserAccount;
import com.example.Blasira_Backend.model.Vehicle;
import com.example.Blasira_Backend.repository.UserAccountRepository;
import com.example.Blasira_Backend.repository.VehicleRepository;
import com.example.Blasira_Backend.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserAccountRepository userAccountRepository;

    @Override
    @Transactional
    public VehicleDto addVehicle(CreateVehicleRequest request, UserDetails currentUser) {
        UserAccount owner = userAccountRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé."));

        Vehicle vehicle = new Vehicle();
        vehicle.setMake(request.getMake());
        vehicle.setModel(request.getModel());
        vehicle.setLicensePlate(request.getLicensePlate());
        vehicle.setCapacity(request.getCapacity());
        vehicle.setType(request.getType()); // Ajout du type
        vehicle.setYear(request.getYear()); // Ajout de l'année
        vehicle.setOwner(owner);

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return mapToVehicleDto(savedVehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleDto> getMyVehicles(UserDetails currentUser) {
        UserAccount owner = userAccountRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé."));

        List<Vehicle> vehicles = vehicleRepository.findByOwner(owner);
        return vehicles.stream()
                .map(this::mapToVehicleDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteVehicle(Long vehicleId, UserDetails currentUser) {
        UserAccount owner = userAccountRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé."));

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Véhicule non trouvé avec l'ID: " + vehicleId));

        if (!vehicle.getOwner().getId().equals(owner.getId())) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à supprimer ce véhicule.");
        }

        vehicleRepository.delete(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleDto> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        return vehicles.stream()
                .map(this::mapToVehicleDto)
                .collect(Collectors.toList());
    }

    private VehicleDto mapToVehicleDto(Vehicle vehicle) {
        return VehicleDto.builder()
                .id(vehicle.getId())
                .make(vehicle.getMake())
                .model(vehicle.getModel())
                .licensePlate(vehicle.getLicensePlate())
                .capacity(vehicle.getCapacity())
                .type(vehicle.getType())
                .year(vehicle.getYear())
                .ownerId(vehicle.getOwner().getId())
                .build();
    }
}
