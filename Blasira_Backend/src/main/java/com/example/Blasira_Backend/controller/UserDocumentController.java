package com.example.Blasira_Backend.controller;

import com.example.Blasira_Backend.dto.document.DocumentDto;
import com.example.Blasira_Backend.model.UserAccount;
import com.example.Blasira_Backend.model.enums.DocumentType; // NEW
import com.example.Blasira_Backend.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/me/documents")
@RequiredArgsConstructor
public class UserDocumentController {

    private final DocumentService documentService;

    @PostMapping
    public ResponseEntity<DocumentDto> uploadDocument(
            @RequestParam("documentType") String documentType,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserAccount currentUser) {

        DocumentDto uploadedDocument = documentService.uploadDocument(currentUser.getId(), DocumentType.valueOf(documentType), file);
        return new ResponseEntity<>(uploadedDocument, HttpStatus.CREATED);
    }
}
