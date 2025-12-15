package com.example.Blasira_Backend.repository;

import com.example.Blasira_Backend.model.UserAccount;
import com.example.Blasira_Backend.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByOwner(UserAccount owner);
}
