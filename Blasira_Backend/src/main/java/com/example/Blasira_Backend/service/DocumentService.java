package com.example.Blasira_Backend.service;

import com.example.Blasira_Backend.dto.document.DocumentDto;
import com.example.Blasira_Backend.dto.admin.UpdateDocumentStatusRequest;
import com.example.Blasira_Backend.exception.UserNotFoundException;
import com.example.Blasira_Backend.model.Document;
import com.example.Blasira_Backend.model.UserAccount;
import com.example.Blasira_Backend.model.enums.DocumentType;
import com.example.Blasira_Backend.model.enums.DocumentStatus; // NEW
import com.example.Blasira_Backend.repository.DocumentRepository;
import com.example.Blasira_Backend.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import jakarta.persistence.EntityNotFoundException; // NEW

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserAccountRepository userAccountRepository;
    // Inject a StorageService here if using external storage (e.g., S3, Cloudinary)
    // private final StorageService storageService;

    // Placeholder for local storage directory (should be configured externally)
    private final String uploadDir = "uploads/documents/";

    @Transactional
    public DocumentDto uploadDocument(Long userId, DocumentType documentType, MultipartFile file) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // 1. Store the file
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);

        try {
            // Ensure the upload directory exists
            Files.createDirectories(filePath.getParent());
            Files.copy(file.getInputStream(), filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }

        // 2. Save document metadata to database
        Document document = new Document();
        document.setUser(user);
        document.setDocumentType(documentType);
        document.setFilePath(filePath.toString()); // Store local path for now
        document.setStatus(DocumentStatus.PENDING); // Default status NEW
        Document savedDocument = documentRepository.save(document);

        // 3. Update user's overall verification status to PENDING
        user.setVerificationStatus("PENDING"); // This is a string, consider making it an enum for consistency
        userAccountRepository.save(user);

        return mapToDocumentDto(savedDocument);
    }

    @Transactional
    public DocumentDto updateDocumentStatus(Long documentId, DocumentStatus newStatus, String rejectionReason) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found with ID: " + documentId));

        document.setStatus(newStatus);
        document.setRejectionReason(rejectionReason); // Set null if not rejected

        // If a document is approved or rejected, potentially update the user's overall verification status
        // This logic might be better placed in AdminService depending on the overall verification flow.
        if (newStatus == DocumentStatus.APPROVED || newStatus == DocumentStatus.REJECTED) {
            // Example: trigger re-evaluation of user's overall verification status
            // For now, we'll leave this to AdminService to manage the overall user verification flow
        }

        Document updatedDocument = documentRepository.save(document);
        return mapToDocumentDto(updatedDocument);
    }

    private DocumentDto mapToDocumentDto(Document document) {
        return DocumentDto.builder()
                .id(document.getId())
                .userId(document.getUser().getId())
                .documentType(document.getDocumentType().name())
                .filePath(document.getFilePath())
                .status(document.getStatus().name()) // Use .name() for enum
                .rejectionReason(document.getRejectionReason())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }
}
