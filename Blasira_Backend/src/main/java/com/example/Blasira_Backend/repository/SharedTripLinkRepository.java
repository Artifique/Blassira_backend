package com.example.Blasira_Backend.repository;

import com.example.Blasira_Backend.model.SharedTripLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SharedTripLinkRepository extends JpaRepository<SharedTripLink, Long> {
    Optional<SharedTripLink> findByToken(String token);
}
