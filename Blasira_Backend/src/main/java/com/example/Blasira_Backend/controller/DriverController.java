package com.example.Blasira_Backend.controller;

import com.example.Blasira_Backend.dto.document.DocumentDto; // Changed import
import com.example.Blasira_Backend.model.enums.DocumentType;
import com.example.Blasira_Backend.model.enums.DriverProfileStatus;
import com.example.Blasira_Backend.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Contrôleur pour les actions spécifiques aux conducteurs, comme postuler pour devenir conducteur.
 */
@RestController
@RequestMapping("/api/driver")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    /**
     * Endpoint pour qu'un utilisateur postule pour devenir conducteur.
     * Cette action initialise son profil conducteur et le met dans un état en attente.
     *
     * @param currentUser L'utilisateur authentifié faisant la requête.
     * @return Un message de succès.
     */
    @PostMapping("/apply")
    public ResponseEntity<?> applyToBecomeDriver(@AuthenticationPrincipal UserDetails currentUser) {
        driverService.applyToBecomeDriver(currentUser);
        return ResponseEntity.ok("Application to become a driver has been submitted successfully. It is now pending review.");
    }

    /**
     * Télécharge un document de vérification (ex: permis de conduire).
     *
     * @param file Le fichier de document en cours de téléchargement.
     * @param type Le type du document.
     * @param currentUser L'utilisateur authentifié.
     * @return Un DTO de l'enregistrement de document créé.
     */
    @PostMapping("/documents")
    public ResponseEntity<DocumentDto> uploadDocument( // Changed return type
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") DocumentType type,
            @AuthenticationPrincipal UserDetails currentUser) {
        DocumentDto documentDto = driverService.uploadDocument(file, type, currentUser);
        return ResponseEntity.ok(documentDto);
    }

    /**
     * Récupère le statut du profil conducteur de l'utilisateur actuellement authentifié.
     *
     * @param currentUser L'utilisateur authentifié.
     * @return Le statut du profil conducteur.
     */
    @GetMapping("/me/status")
    public ResponseEntity<DriverProfileStatus> getMyDriverStatus(@AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(driverService.getDriverStatus(currentUser));
    }
}
