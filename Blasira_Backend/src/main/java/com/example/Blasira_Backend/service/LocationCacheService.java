package com.example.Blasira_Backend.service;

import com.example.Blasira_Backend.dto.location.LocationUpdateDto;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LocationCacheService {

    private final Map<Long, LocationUpdateDto> locationCache = new ConcurrentHashMap<>();

    public void updateLocation(Long tripId, LocationUpdateDto location) {
        locationCache.put(tripId, location);
    }

    public Optional<LocationUpdateDto> getLocation(Long tripId) {
        return Optional.ofNullable(locationCache.get(tripId));
    }

    public void clearLocation(Long tripId) {
        locationCache.remove(tripId);
    }
}
