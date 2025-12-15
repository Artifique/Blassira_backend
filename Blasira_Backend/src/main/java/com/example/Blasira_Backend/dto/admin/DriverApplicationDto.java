package com.example.Blasira_Backend.dto.admin;

import com.example.Blasira_Backend.dto.document.DocumentDto; // Changed import
import com.example.Blasira_Backend.model.enums.DriverProfileStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * DTO for an admin to review a driver application.
 */
@Data
@Builder
public class DriverApplicationDto {
    private Long driverProfileId;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private DriverProfileStatus status;
    private List<DocumentDto> documents;
}
