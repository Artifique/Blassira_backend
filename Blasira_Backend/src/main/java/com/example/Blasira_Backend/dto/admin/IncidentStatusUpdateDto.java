package com.example.Blasira_Backend.dto.admin;

import com.example.Blasira_Backend.model.enums.IncidentReportStatus;
import lombok.Data;

@Data
public class IncidentStatusUpdateDto {
    private IncidentReportStatus status;
    private String resolutionNotes;
}
