package com.example.Blasira_Backend.exception.handler;

import com.example.Blasira_Backend.exception.DriverNotVerifiedException;
import com.example.Blasira_Backend.exception.UserNotFoundException;
import com.example.Blasira_Backend.exception.VehicleNotFoundException;
import org.springframework.dao.DataIntegrityViolationException; // Import ajouté
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Gestionnaire d'exceptions global pour l'application.
 * L'annotation @RestControllerAdvice permet de centraliser la gestion des exceptions
 * pour tous les contrôleurs de l'application. Il intercepte les exceptions lancées
 * et les transforme en réponses HTTP structurées.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gère l'exception personnalisée pour un utilisateur non trouvé.
     * @return Une ResponseEntity avec un statut 404 (Not Found).
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFound(UserNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

    /**
     * Gère l'exception personnalisée pour un conducteur non vérifié.
     * @return Une ResponseEntity avec un statut 403 (Forbidden).
     */
    @ExceptionHandler(DriverNotVerifiedException.class)
    public ResponseEntity<Object> handleDriverNotVerified(DriverNotVerifiedException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.FORBIDDEN);
    }

    /**
     * Gère l'exception personnalisée pour un véhicule non trouvé.
     * @return Une ResponseEntity avec un statut 404 (Not Found).
     */
    @ExceptionHandler(VehicleNotFoundException.class)
    public ResponseEntity<Object> handleVehicleNotFound(VehicleNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

    /**
     * Gère les exceptions liées à un argument illégal ou inapproprié.
     * Typiquement utilisé pour des erreurs de validation métier simples.
     * @return Une ResponseEntity avec un statut 400 (Bad Request).
     */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Object> handleBadRequestExceptions(RuntimeException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    /**
     * Gère les exceptions de violation d'intégrité des données (par exemple, NOT NULL, UNIQUE).
     * Convertit l'erreur de base de données en une réponse 400 (Bad Request) plus appropriée.
     * @return Une ResponseEntity avec un statut 400 (Bad Request).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        String message = "Une violation d'intégrité des données s'est produite. Cela peut être dû à des données manquantes ou en double. Détails: " + ex.getRootCause().getMessage();
        return buildErrorResponse(new RuntimeException(message), HttpStatus.BAD_REQUEST);
    }

    /**
     * Gère toutes les autres exceptions non capturées par des gestionnaires plus spécifiques.
     * C'est un filet de sécurité pour éviter de fuiter des stack traces à l'utilisateur
     * et pour fournir une réponse d'erreur standard pour les erreurs inattendues.
     * @return Une ResponseEntity avec un statut 500 (Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        // Il est recommandé de logger l'exception complète ici pour le débogage.
        // log.error("Unexpected error occurred", ex);
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Construit une réponse d'erreur JSON standardisée.
     * @param ex L'exception source.
     * @param status Le statut HTTP à retourner.
     * @return Une ResponseEntity contenant le corps de l'erreur.
     */
    private ResponseEntity<Object> buildErrorResponse(Exception ex, HttpStatus status) {
        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", ex.getMessage()
        );
        return new ResponseEntity<>(body, status);
    }
}
