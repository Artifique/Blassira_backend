package com.example.Blasira_Backend.repository;

import com.example.Blasira_Backend.model.AppConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppConfigRepository extends JpaRepository<AppConfig, Long> {
    // La configuration est généralement une entité singleton, donc un seul enregistrement est attendu.
    // Vous pouvez ajouter une méthode pour récupérer la seule instance si nécessaire.
    AppConfig findTopByOrderByIdAsc();
}
