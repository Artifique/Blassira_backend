package com.example.Blasira_Backend.controller;

import com.example.Blasira_Backend.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException; // Added for Files.probeContentType
import java.net.MalformedURLException;
import java.nio.file.Files; // Added for Files.probeContentType
import java.nio.file.Paths; // Added for Files.probeContentType

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping("/{documentId}/view")
    public ResponseEntity<Resource> viewDocument(@PathVariable Long documentId) throws MalformedURLException {
        Resource resource = documentService.loadDocumentAsResource(documentId);

        String contentType = null;
        try {
            // Try to determine file's content type from its path
            contentType = Files.probeContentType(Paths.get(resource.getURI()));
        } catch (IOException ex) {
            // Fallback to default content type if unable to determine
            contentType = "application/octet-stream";
        }

        // If content type is still null (e.g., file doesn't exist or probeContentType fails), set a default
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
