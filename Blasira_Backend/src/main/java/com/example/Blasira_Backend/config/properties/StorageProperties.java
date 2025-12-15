package com.example.Blasira_Backend.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propriétés de configuration pour le stockage de fichiers.
 * Ceci permet au répertoire d'upload d'être configuré dans application.properties.
 */
@Data
@ConfigurationProperties("storage")
public class StorageProperties {

    /**
     * L'emplacement pour stocker les fichiers uploadés.
     */
    private String location = "uploads";

}
