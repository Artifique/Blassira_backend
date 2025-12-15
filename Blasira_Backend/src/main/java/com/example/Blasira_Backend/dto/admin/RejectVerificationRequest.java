package com.example.Blasira_Backend.dto.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RejectVerificationRequest {
    @NotBlank
    private String reason;
}
