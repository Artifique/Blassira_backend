package com.example.Blasira_Backend.config;

import com.example.Blasira_Backend.config.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration principale de Spring Security.
 * C'est ici que la sécurité de l'application (authentification, autorisations) est définie.
 */
@Configuration
@EnableWebSecurity // Active la sécurité web pour une application Spring.
@RequiredArgsConstructor // Génère un constructeur pour toutes les dépendances finales (final).
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Value("${cors.allowed.origins:*}")
    private String allowedOrigins;

    /**
     * Définit la chaîne de filtres de sécurité qui s'applique à toutes les requêtes HTTP.
     * C'est le point central pour configurer les règles de sécurité.
     * @param http L'objet HttpSecurity à configurer.
     * @return La chaîne de filtres de sécurité construite.
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Désactivation du CSRF (Cross-Site Request Forgery)
                // C'est nécessaire pour les API REST stateless où l'authentification n'est pas basée sur les sessions de navigateur.
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())

                // 2. Définition des règles d'autorisation pour les requêtes HTTP.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Permet un accès public (sans authentification) aux endpoints d'authentification,
                        // aux fichiers uploadés et aux endpoints publics.
                        .requestMatchers("/api/auth/**", "/uploads/**", "/public/**", "/api/images/**").permitAll()
                        // Exige le rôle "ADMIN" pour tous les endpoints sous "/api/admin/".
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // Exige une authentification pour toutes les autres requêtes.
                        .anyRequest().authenticated()
                )

                // 3. Configuration de la gestion de session.
                // On utilise une politique STATELESS car l'authentification se fait via un token JWT à chaque requête.
                // Le serveur ne maintient aucun état de session pour l'utilisateur.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. Spécification du fournisseur d'authentification personnalisé.
                .authenticationProvider(authenticationProvider())

                // 5. Ajout de notre filtre JWT personnalisé avant le filtre d'authentification standard de Spring.
                // Ce filtre interceptera chaque requête pour valider le token JWT.
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Configuration CORS via variable d'environnement
        if ("*".equals(allowedOrigins)) {
            // Mode développement : autoriser toutes les origines
            configuration.setAllowedOrigins(List.of("*"));
        } else {
            // Mode production : origines spécifiques
            configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
            configuration.setAllowCredentials(true);
        }
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    /**
     * Crée et configure le fournisseur d'authentification (AuthenticationProvider).
     * Il utilise le UserDetailsService pour charger les détails de l'utilisateur
     * et le PasswordEncoder pour vérifier les mots de passe.
     * @return Le fournisseur d'authentification configuré.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // Service pour trouver l'utilisateur par son nom.
        authProvider.setPasswordEncoder(passwordEncoder()); // Algorithme pour hacher et vérifier les mots de passe.
        return authProvider;
    }

    /**
     * Expose l'AuthenticationManager de Spring Security en tant que Bean.
     * Nécessaire pour pouvoir l'injecter et l'utiliser dans notre logique d'authentification (ex: dans AuthController).
     * @param config La configuration d'authentification de Spring.
     * @return L'AuthenticationManager.
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Définit l'algorithme de hachage de mot de passe à utiliser dans l'application.
     * BCrypt est un standard de l'industrie, fort et sécurisé.
     * @return Un encodeur de mot de passe.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}