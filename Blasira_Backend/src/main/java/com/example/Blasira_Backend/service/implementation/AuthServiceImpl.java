package com.example.Blasira_Backend.service.implementation;

import com.example.Blasira_Backend.dto.auth.JwtAuthenticationResponse;
import com.example.Blasira_Backend.dto.auth.LoginRequest;
import com.example.Blasira_Backend.dto.auth.SignUpRequest;
import com.example.Blasira_Backend.dto.message.SendMessageRequest;
import com.example.Blasira_Backend.model.DriverProfile;
import com.example.Blasira_Backend.model.UserAccount;
import com.example.Blasira_Backend.model.UserProfile;
import com.example.Blasira_Backend.model.enums.DriverProfileStatus;
import com.example.Blasira_Backend.model.enums.Role;
import com.example.Blasira_Backend.repository.DriverProfileRepository;
import com.example.Blasira_Backend.repository.UserAccountRepository;
import com.example.Blasira_Backend.repository.UserProfileRepository;
import com.example.Blasira_Backend.service.AuthService;
import com.example.Blasira_Backend.service.JwtService;
import com.example.Blasira_Backend.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implémentation du service d'authentification.
 * Gère la logique métier pour l'inscription et la connexion des utilisateurs.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserAccountRepository userAccountRepository;
    private final UserProfileRepository userProfileRepository;
    private final DriverProfileRepository driverProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final MessageService messageService;

    /**
     * Gère l'inscription d'un nouvel utilisateur.
     * Crée le compte utilisateur, le profil public, et le profil conducteur initial.
     * @param request Les données d'inscription.
     * @return Une réponse contenant le token JWT pour le nouvel utilisateur.
     */
    @Override
    @Transactional // Assure que toutes les créations sont faites en une seule transaction.
    public JwtAuthenticationResponse signup(SignUpRequest request) {
        // 1. Créer l'entité UserAccount, qui gère les informations de connexion.
        var user = new UserAccount();
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        // Hacher le mot de passe avant de le sauvegarder est une étape de sécurité cruciale.
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.getRoles().add(Role.ROLE_USER); // Attribuer le rôle utilisateur par défaut.
        user = userAccountRepository.save(user);

        // 2. Créer le profil public de l'utilisateur.
        var userProfile = new UserProfile();
        userProfile.setUserAccount(user); // Set the UserAccount object
        userProfile.setFirstName(request.getFirstName());
        userProfile.setLastName(request.getLastName());
        userProfileRepository.save(userProfile);

        // 3. Créer un profil conducteur initial pour l'utilisateur.
        // Cela simplifie la logique future si l'utilisateur décide de devenir conducteur.
        var driverProfile = new DriverProfile();
        driverProfile.setUserAccount(user); // Lie également ce profil au compte.
        driverProfile.setStatus(DriverProfileStatus.NOT_SUBMITTED); // Statut initial.
        driverProfileRepository.save(driverProfile);

        // 4. Notifier l'administrateur de la nouvelle inscription.
        notifyAdminOfNewSignup(user);

        // 5. Générer un token JWT pour que l'utilisateur soit automatiquement connecté après l'inscription.
        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }

    /**
     * Gère la connexion d'un utilisateur existant.
     * @param request Les identifiants de connexion.
     * @return Une réponse contenant un nouveau token JWT.
     */
    @Override
    public JwtAuthenticationResponse login(LoginRequest request) {
        // 1. Utiliser l'AuthenticationManager de Spring Security pour valider les identifiants.
        // S'ils sont incorrects, une exception sera levée par Spring, ce qui arrêtera le processus.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getPhoneNumber(), request.getPassword()));

        // 2. Si l'authentification réussit, récupérer les détails de l'utilisateur.
        var user = userAccountRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new IllegalArgumentException("Numéro de téléphone ou mot de passe invalide."));

        // 3. Générer et retourner un nouveau token JWT.
        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }

    private void notifyAdminOfNewSignup(UserAccount newUser) {
        List<UserAccount> admins = userAccountRepository.findByRolesContains(Role.ROLE_ADMIN);
        if (admins.isEmpty()) {
            // Logique de fallback si aucun admin n'est trouvé (par exemple, loguer un avertissement)
            return;
        }
        // Pour cet exemple, nous envoyons la notification au premier admin trouvé.
        UserAccount admin = admins.get(0);
        
        SendMessageRequest messageRequest = new SendMessageRequest();
        messageRequest.setRecipientId(admin.getId());
        messageRequest.setContent("Nouvel utilisateur inscrit : " + newUser.getEmail());
        
        // L'admin s'envoie un message à lui-même comme notification.
        messageService.sendMessage(admin.getId(), messageRequest);
    }
}
