package com.example.Blasira_Backend.service;

import com.example.Blasira_Backend.dto.admin.DriverApplicationDto;
import com.example.Blasira_Backend.dto.document.DocumentDto; // Changed import
import com.example.Blasira_Backend.model.enums.DocumentType;
import com.example.Blasira_Backend.model.enums.DriverProfileStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Interface de service pour les actions spécifiques aux conducteurs.
 */
public interface DriverService {

    void applyToBecomeDriver(UserDetails currentUser);

    DocumentDto uploadDocument(MultipartFile file, DocumentType documentType, UserDetails currentUser);

    List<DriverApplicationDto> getPendingApplications();

    /**
     * Met à jour le statut d'une candidature de conducteur (ex: VERIFIED ou REJECTED).
     *
     * @param driverProfileId L'ID du profil conducteur à mettre à jour.
     * @param newStatus Le nouveau statut à définir.
     */
    void updateDriverApplicationStatus(Long driverProfileId, DriverProfileStatus newStatus);

    DriverProfileStatus getDriverStatus(UserDetails currentUser);

}
