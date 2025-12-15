package com.example.Blasira_Backend.dto.admin;

import com.example.Blasira_Backend.model.enums.VerificationStatus;
import lombok.Data;

@Data
public class UpdateVehicleStatusDto {
    private VerificationStatus status;
    private String reason;
}
