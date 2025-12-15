package com.example.Blasira_Backend.dto.admin;

import com.example.Blasira_Backend.model.enums.DocumentStatus;
import lombok.Data;

@Data
public class UpdateDocumentStatusRequest {
    private DocumentStatus status;
    private String rejectionReason; // Optionnel, si le statut est REJECTED
}
