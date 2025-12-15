package com.example.Blasira_Backend.dto.document;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UploadDocumentRequest {
    @NotBlank
    private String documentType; // e.g., ID_CARD, INSTITUTIONAL_EMAIL, DRIVERS_LICENSE
}
