package com.example.Blasira_Backend.repository;

import com.example.Blasira_Backend.model.DriverProfile;
import com.example.Blasira_Backend.model.enums.DriverProfileStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DriverProfileRepository extends JpaRepository<DriverProfile, Long> {

    /**
     * Finds all driver profiles with a given status.
     * @param status The status to search for.
     * @return A list of driver profiles.
     */
    List<DriverProfile> findByStatus(DriverProfileStatus status);
}
