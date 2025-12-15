package com.example.Blasira_Backend.dto.admin;

import com.example.Blasira_Backend.model.enums.DriverProfileStatus;
import lombok.Data;

/**
 * DTO pour qu'un administrateur mette à jour le statut d'un profil conducteur.
 */
@Data
public class UpdateDriverStatusRequest {
    private DriverProfileStatus status;
}
