package com.example.Blasira_Backend.service;

import com.example.Blasira_Backend.dto.document.DocumentDto;
import com.example.Blasira_Backend.exception.UserNotFoundException;
import com.example.Blasira_Backend.model.Document;
import com.example.Blasira_Backend.model.UserAccount;
import com.example.Blasira_Backend.model.enums.DocumentType;
import com.example.Blasira_Backend.model.enums.DocumentStatus;
import com.example.Blasira_Backend.repository.DocumentRepository;
import com.example.Blasira_Backend.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException; // Added for loadDocumentAsResource

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserAccountRepository userAccountRepository;
    private final StorageService storageService; // Inject StorageService

    @Transactional
    public DocumentDto uploadDocument(Long userId, DocumentType documentType, MultipartFile file) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // 1. Store the file using StorageService
        String uniqueFilename = storageService.store(file); // Store the unique filename

        // 2. Save document metadata to database
        Document document = new Document();
        document.setUser(user);
        document.setDocumentType(documentType);
        document.setFilePath(uniqueFilename); // Store only the unique filename
        document.setStatus(DocumentStatus.PENDING); // Default status
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

        Document updatedDocument = documentRepository.save(document);
        return mapToDocumentDto(updatedDocument);
    }

    // New method to load document as a resource
    @Transactional(readOnly = true)
    public Resource loadDocumentAsResource(Long documentId) throws MalformedURLException {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found with ID: " + documentId));
        
        // Use the filename stored in the database to load the resource
        return storageService.loadAsResource(document.getFilePath());
    }

    private DocumentDto mapToDocumentDto(Document document) {
        return DocumentDto.builder()
                .id(document.getId())
                .userId(document.getUser().getId())
                .documentType(document.getDocumentType().name())
                .filePath(document.getFilePath()) // Now just the filename
                .status(document.getStatus().name())
                .rejectionReason(document.getRejectionReason())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }
}
