package com.example.Blasira_Backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception lancée lorsqu'un véhicule avec l'ID spécifié n'est pas trouvé.
 */
@ResponseStatus(HttpStatus.NOT_FOUND) // Associe cette exception au code HTTP 404
public class VehicleNotFoundException extends RuntimeException {
    public VehicleNotFoundException(String message) {
        super(message);
    }
}
