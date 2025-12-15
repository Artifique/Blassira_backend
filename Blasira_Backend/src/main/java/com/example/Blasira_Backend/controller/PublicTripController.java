package com.example.Blasira_Backend.controller;

import com.example.Blasira_Backend.dto.location.LocationUpdateDto;
import com.example.Blasira_Backend.model.SharedTripLink;
import com.example.Blasira_Backend.repository.SharedTripLinkRepository;
import com.example.Blasira_Backend.service.LocationCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/public/trips")
@RequiredArgsConstructor
public class PublicTripController {

    private final SharedTripLinkRepository sharedTripLinkRepository;
    private final LocationCacheService locationCacheService;

    @GetMapping("/share/{token}")
    public ResponseEntity<LocationUpdateDto> getSharedTripLocation(@PathVariable String token) {
        SharedTripLink link = sharedTripLinkRepository.findByToken(token)
                .orElseThrow(() -> new SecurityException("Invalid or expired share token."));

        if (link.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new SecurityException("Invalid or expired share token.");
        }

        return locationCacheService.getLocation(link.getTrip().getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
