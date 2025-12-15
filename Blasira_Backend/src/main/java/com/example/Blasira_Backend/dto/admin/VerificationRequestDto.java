package com.example.Blasira_Backend.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class VerificationRequestDto {
    private Long userId;
    private String userName;
    private String userEmail;
    private String userPhone;
    private String userType; // e.g., "student", "driver", or based on roles
    private LocalDateTime submissionDate;
    private String status; // Overall verification status of the user
    private List<DocumentStatusDto> documents; // List of documents with their status

    @Data
    @Builder
    public static class DocumentStatusDto {
        private String documentType;
        private String status;
        private String filePath; // URL or path to the document file for viewing
    }
}
