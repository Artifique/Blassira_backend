package com.example.Blasira_Backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception lancée lorsqu'un utilisateur tente d'effectuer une action de conducteur
 * sans avoir un profil vérifié.
 */
@ResponseStatus(HttpStatus.FORBIDDEN) // Associe cette exception au code HTTP 403
public class DriverNotVerifiedException extends RuntimeException {
    public DriverNotVerifiedException(String message) {
        super(message);
    }
}
