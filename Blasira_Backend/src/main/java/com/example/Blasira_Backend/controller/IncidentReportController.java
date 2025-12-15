package com.example.Blasira_Backend.controller;

import com.example.Blasira_Backend.dto.incident.CreateIncidentReportRequest;
import com.example.Blasira_Backend.dto.incident.IncidentReportDto;
import com.example.Blasira_Backend.model.IncidentReport;
import com.example.Blasira_Backend.model.UserAccount;
import com.example.Blasira_Backend.service.IncidentReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentReportController {

    private final IncidentReportService incidentReportService;

    @PostMapping
    public ResponseEntity<IncidentReportDto> createIncidentReport(
            @Valid @RequestBody CreateIncidentReportRequest request,
            @AuthenticationPrincipal UserAccount currentUser) {

        IncidentReport report = incidentReportService.createIncidentReport(currentUser.getId(), request);
        return new ResponseEntity<>(toDto(report), HttpStatus.CREATED);
    }

    private IncidentReportDto toDto(IncidentReport report) {
        return IncidentReportDto.builder()
                .id(report.getId())
                .bookingId(report.getBooking().getId())
                .reporterId(report.getReporter().getId())
                .reporterFirstName(report.getReporter().getFirstName())
                .reason(report.getReason())
                .description(report.getDescription())
                .status(report.getStatus())
                .createdAt(report.getCreatedAt())
                .build();
    }
}
