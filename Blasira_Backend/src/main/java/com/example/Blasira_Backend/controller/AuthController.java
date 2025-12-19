package com.example.Blasira_Backend.controller;

import com.example.Blasira_Backend.dto.auth.*;
import com.example.Blasira_Backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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

    /**
     * Endpoint pour demander un OTP pour un numéro de téléphone donné.
     * Pour le développement, le code OTP est retourné dans la réponse.
     * @param request Le DTO contenant le numéro de téléphone.
     * @return Une réponse contenant le code OTP généré.
     */
    @PostMapping("/request-otp")
    public ResponseEntity<Map<String, String>> requestOtp(@RequestBody OtpRequest request) {
        String otp = authService.requestOtp(request.getPhoneNumber());
        Map<String, String> response = new HashMap<>();
        response.put("otp", otp);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint pour vérifier un OTP.
     * @param request Le DTO contenant le numéro de téléphone et le code OTP.
     * @return Une réponse indiquant si la vérification a réussi (true) ou échoué (false).
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<Boolean> verifyOtp(@RequestBody OtpVerificationRequest request) {
        boolean isValid = authService.verifyOtp(request.getPhoneNumber(), request.getOtp());
        return ResponseEntity.ok(isValid);
    }
}
