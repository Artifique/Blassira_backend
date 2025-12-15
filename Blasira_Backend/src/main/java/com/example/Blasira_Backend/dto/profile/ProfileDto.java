package com.example.Blasira_Backend.dto.profile;

import com.example.Blasira_Backend.model.enums.DriverProfileStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

/**
 * DTO représentant le profil complet d'un utilisateur pour la visualisation.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileDto {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String bio;
    private String profilePictureUrl;
    private Double note;
    private Long nombreDeTrajet;
    private DriverProfileStatus driverStatus;
}
