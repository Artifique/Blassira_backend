package com.example.Blasira_Backend.dto.incident;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateIncidentReportRequest {

    @NotNull
    private Long bookingId;

    @NotBlank
    private String reason;

    private String description;
}
