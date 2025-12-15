package com.example.Blasira_Backend.controller;

import com.example.Blasira_Backend.dto.auth.JwtAuthenticationResponse;
import com.example.Blasira_Backend.dto.auth.LoginRequest;
import com.example.Blasira_Backend.dto.auth.SignUpRequest;
import com.example.Blasira_Backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur gérant les endpoints publics pour l'authentification.
 * Permet aux utilisateurs de s'inscrire et de se connecter.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint pour l'inscription d'un nouvel utilisateur.
     * @param request Le DTO contenant les informations nécessaires à l'inscription (email, mot de passe, etc.).
     * @return Une réponse avec le token JWT si l'inscription réussit.
     */
    @PostMapping("/signup")
    public ResponseEntity<JwtAuthenticationResponse> signup(@RequestBody SignUpRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    /**
     * Endpoint pour la connexion d'un utilisateur existant.
     * @param request Le DTO contenant les identifiants (email, mot de passe).
     * @return Une réponse avec le token JWT si la connexion réussit.
     */
    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
