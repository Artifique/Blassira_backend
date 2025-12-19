package com.example.Blasira_Backend.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class AdminDashboardStatsDto {
    private long totalUsers;
    private long totalTrips;
    private long totalBookings;
    private Map<LocalDate, Long> dailyActivity; // Ex: date -> count of new users/trips
    private Map<String, Map<String, Long>> monthlyTrends; // Ex: "2025-11" -> {"newUsers": 100, "newTrips": 50}
    private List<String> recentActivities; // Ex: "New user John Doe registered", "Trip from A to B published"
}
