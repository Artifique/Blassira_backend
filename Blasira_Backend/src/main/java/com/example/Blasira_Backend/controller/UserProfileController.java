package com.example.Blasira_Backend.controller;

import com.example.Blasira_Backend.dto.profile.ProfileDto;
import com.example.Blasira_Backend.dto.profile.UpdateUserProfileRequest;
import com.example.Blasira_Backend.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur pour la gestion des profils utilisateur.
 */
@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    /**
     * Récupère le profil de l'utilisateur actuellement authentifié.
     *
     * @param currentUser L'utilisateur authentifié.
     * @return Les informations de profil de l'utilisateur.
     */
    @GetMapping("/me")
    public ResponseEntity<ProfileDto> getMyProfile(@AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(userProfileService.getMyProfile(currentUser));
    }

    /**
     * Met à jour le profil de l'utilisateur actuellement authentifié.
     *
     * @param request Le corps de la requête contenant les données de profil mises à jour.
     * @param currentUser L'utilisateur authentifié.
     * @return Le profil utilisateur mis à jour.
     */
    @PutMapping("/me")
    public ResponseEntity<ProfileDto> updateMyProfile(
            @RequestBody UpdateUserProfileRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(userProfileService.updateMyProfile(request, currentUser));
    }
}
