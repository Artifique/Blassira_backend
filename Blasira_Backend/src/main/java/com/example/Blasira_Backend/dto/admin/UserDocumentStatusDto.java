package com.example.Blasira_Backend.dto.admin;

import com.example.Blasira_Backend.model.enums.DocumentStatus;
import com.example.Blasira_Backend.model.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class UserDocumentStatusDto {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private List<String> roles; // Changed to List<String> for simplicity in DTO
    private Long documentId; // Can be null if no document uploaded
    private DocumentStatus documentStatus; // Status of the primary document or overall
    private LocalDateTime documentUploadDate; // Date of the primary document upload/update
}
