package com.example.Blasira_Backend.dto.driver;

import com.example.Blasira_Backend.model.enums.DocumentType;
import com.example.Blasira_Backend.model.enums.VerificationStatus;
import lombok.Builder;
import lombok.Data;

/**
 * DTO for representing an uploaded document.
 */
@Data
@Builder
public class DocumentDto {
    private Long id;
    private DocumentType documentType;
    private String fileUrl;
    private VerificationStatus status;
}
