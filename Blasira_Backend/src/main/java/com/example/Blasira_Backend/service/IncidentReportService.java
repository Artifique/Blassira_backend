package com.example.Blasira_Backend.service;

import com.example.Blasira_Backend.dto.incident.CreateIncidentReportRequest;
import com.example.Blasira_Backend.dto.incident.IncidentReportDto;
import com.example.Blasira_Backend.exception.UserProfileNotFoundException;
import com.example.Blasira_Backend.model.Booking;
import com.example.Blasira_Backend.model.IncidentReport;
import com.example.Blasira_Backend.model.UserProfile;
import com.example.Blasira_Backend.model.enums.IncidentReportStatus;
import com.example.Blasira_Backend.repository.BookingRepository;
import com.example.Blasira_Backend.repository.IncidentReportRepository;
import com.example.Blasira_Backend.repository.UserProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IncidentReportService {

    private final IncidentReportRepository incidentReportRepository;
    private final UserProfileRepository userProfileRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public IncidentReport createIncidentReport(Long reporterId, CreateIncidentReportRequest request) {
        UserProfile reporter = userProfileRepository.findById(reporterId)
                .orElseThrow(() -> new UserProfileNotFoundException("Reporter not found"));

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        // Valider que le rapporteur faisait partie de la réservation (soit passager soit conducteur)
        boolean isPassenger = booking.getPassenger().getId().equals(reporterId);
        boolean isDriver = booking.getTrip().getDriver().getUserAccount().getId().equals(reporterId);

        if (!isPassenger && !isDriver) {
            throw new SecurityException("Reporter was not part of the specified booking.");
        }

        IncidentReport incidentReport = new IncidentReport();
        incidentReport.setReporter(reporter);
        incidentReport.setBooking(booking);
        incidentReport.setReason(request.getReason());
        incidentReport.setDescription(request.getDescription());
        incidentReport.setSeverity(com.example.Blasira_Backend.model.enums.IncidentSeverity.LOW);

        return incidentReportRepository.save(incidentReport);
    }

    @Transactional(readOnly = true)
    public List<IncidentReportDto> getAllIncidentReports() {
        return incidentReportRepository.findAll().stream()
                .map(this::mapToIncidentReportDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public IncidentReportDto updateIncidentReportStatus(Long reportId, IncidentReportStatus newStatus) {
        IncidentReport report = incidentReportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Incident Report not found with ID: " + reportId));

        report.setStatus(newStatus);
        IncidentReport updatedReport = incidentReportRepository.save(report);
        return mapToIncidentReportDto(updatedReport);
    }

    private IncidentReportDto mapToIncidentReportDto(IncidentReport report) {
        UserProfile driverProfile = report.getBooking().getTrip().getDriver().getUserAccount().getUserProfile();
        return IncidentReportDto.builder()
                .id(report.getId())
                .bookingId(report.getBooking().getId())
                .reporterId(report.getReporter().getId())
                .reporterFirstName(report.getReporter().getFirstName())
                .reporterLastName(report.getReporter().getLastName())
                .driverFirstName(driverProfile != null ? driverProfile.getFirstName() : null)
                .driverLastName(driverProfile != null ? driverProfile.getLastName() : null)
                .reason(report.getReason())
                .description(report.getDescription())
                .status(report.getStatus())
                .severity(report.getSeverity())
                .createdAt(report.getCreatedAt())
                .build();
    }
}
