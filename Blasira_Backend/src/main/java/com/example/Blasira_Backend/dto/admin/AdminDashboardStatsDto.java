package com.example.Blasira_Backend.dto.admin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminDashboardStatsDto {
    private long totalUsers;
    private long totalTrips;
    private long totalBookings;
}
