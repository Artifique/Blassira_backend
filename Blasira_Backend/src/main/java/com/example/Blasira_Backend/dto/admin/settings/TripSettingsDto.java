package com.example.Blasira_Backend.dto.admin.settings;

import lombok.Data;

@Data
public class TripSettingsDto {
    private Integer maxPassengersPerTrip;
    private Integer minTripPrice;
    private Integer maxTripPrice;
    private Boolean autoApproveTrips;
}
