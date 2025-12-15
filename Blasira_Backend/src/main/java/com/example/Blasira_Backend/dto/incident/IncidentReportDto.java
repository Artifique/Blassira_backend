package com.example.Blasira_Backend.dto.incident;

import com.example.Blasira_Backend.model.enums.IncidentReportStatus;
import com.example.Blasira_Backend.model.enums.IncidentSeverity;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class IncidentReportDto {
    private Long id;
    private Long bookingId;
    private Long reporterId;
    private String reporterFirstName;
    private String reporterLastName;
    private String driverFirstName;
    private String driverLastName;
    private String reason;
    private String description;
    private IncidentReportStatus status;
    private IncidentSeverity severity;
    private LocalDateTime createdAt;
}
