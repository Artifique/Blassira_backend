package com.example.Blasira_Backend.config;

import com.example.Blasira_Backend.repository.*;
import com.example.Blasira_Backend.service.TripService;
import com.example.Blasira_Backend.service.implementation.TripServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final TripRepository tripRepository;
    private final UserAccountRepository userAccountRepository;
    private final VehicleRepository vehicleRepository;
    private final BookingRepository bookingRepository;
    private final DriverProfileRepository driverProfileRepository;

    @Bean
    public TripService tripService() {
        return new TripServiceImpl(tripRepository, userAccountRepository, vehicleRepository, bookingRepository, driverProfileRepository);
    }
}
