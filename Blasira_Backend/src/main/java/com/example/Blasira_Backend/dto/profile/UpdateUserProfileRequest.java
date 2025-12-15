package com.example.Blasira_Backend.dto.profile;

import lombok.Data;

/**
 * DTO pour qu'un utilisateur mette à jour ses informations de profil.
 */
@Data
public class UpdateUserProfileRequest {
    private String firstName;
    private String lastName;
    private String bio;
    // Nous pouvons ajouter les mises à jour de photo de profil plus tard
}
