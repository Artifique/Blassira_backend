package com.example.Blasira_Backend.service;

import com.example.Blasira_Backend.dto.profile.ProfileDto;
import com.example.Blasira_Backend.dto.profile.UpdateUserProfileRequest;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Interface de service pour la gestion des profils utilisateur.
 */
public interface UserProfileService {

    /**
     * Récupère le profil de l'utilisateur actuellement authentifié.
     *
     * @param currentUser L'utilisateur actuellement authentifié.
     * @return Le profil complet de l'utilisateur.
     */
    ProfileDto getMyProfile(UserDetails currentUser);

    /**
     * Met à jour le profil de l'utilisateur actuellement authentifié.
     *
     * @param request L'objet de requête contenant les champs à mettre à jour.
     * @param currentUser L'utilisateur actuellement authentifié.
     * @return Le profil utilisateur mis à jour.
     */
    ProfileDto updateMyProfile(UpdateUserProfileRequest request, UserDetails currentUser);

}
