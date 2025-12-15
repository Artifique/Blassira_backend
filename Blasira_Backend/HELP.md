# Architecture et Fonctionnement du Projet "Blasira Backend"

## 1. Vue d'ensemble

Ce projet est une application backend développée avec **Spring Boot**. Il expose une **API REST** pour une application de covoiturage (ou de transport). Il gère les utilisateurs, les trajets, les réservations, les paiements, les profils conducteurs, etc.

La technologie principale est Java 17 avec l'écosystème Spring :
*   **Spring Web** pour l'API REST.
*   **Spring Data JPA** pour la persistance des données avec une base de données MySQL.
*   **Spring Security** pour l'authentification et les autorisations, sécurisée par **JWT (JSON Web Tokens)**.
*   **Spring WebSocket** pour la communication en temps réel (probablement la messagerie).
*   **Flyway** pour la gestion des versions du schéma de la base de données.

## 2. Structure des Packages

Le code est organisé en couches, ce qui est une excellente pratique pour la séparation des responsabilités :

*   `config`: Contient les classes de configuration de Spring (Sécurité, Web, WebSocket). C'est ici qu'on branche et configure les différents modules.
*   `controller`: La porte d'entrée de l'API. Ces classes reçoivent les requêtes HTTP, valident les entrées de base et appellent la couche de service. **Elles ne contiennent aucune logique métier.**
*   `dto` (Data Transfer Object): Des classes simples qui définissent la "forme" des données échangées avec l'extérieur (ex: JSON dans les requêtes/réponses). Elles permettent de ne pas exposer la structure de votre base de données.
*   `exception`: (Actuellement peu peuplé) Destiné à contenir des classes d'exception personnalisées pour une meilleure gestion des erreurs.
*   `model`: Les entités JPA. Ce sont des représentations Java de vos tables de base de données. C'est le cœur de votre modèle de données.
*   `repository`: Des interfaces qui étendent `JpaRepository`. Spring Data JPA génère automatiquement le code pour interagir avec la base de données (CRUD, requêtes simples).
*   `service`: Le cerveau de l'application.
    *   `service` (interfaces): Définit les contrats des fonctionnalités (`TripService`, `BookingService`...).
    *   `service.implementation` (classes): Contient la logique métier, la validation des règles, la gestion des transactions et la coordination entre les différents repositories.

## 3. Flux de données : Exemple de la création d'un trajet

Pour comprendre comment les couches interagissent, suivons une requête `POST /api/trips` :

1.  **Requête HTTP** : Un utilisateur authentifié envoie une requête POST avec les détails du trajet en JSON.
2.  **`TripController`** :
    *   Le `@PostMapping` intercepte la requête.
    *   Spring désérialise le JSON en un objet `CreateTripRequest` (un DTO).
    *   Il récupère l'utilisateur authentifié grâce à `@AuthenticationPrincipal`.
    *   Il appelle la méthode `tripService.createTrip(...)`.
3.  **`TripServiceImpl`** :
    *   La méthode est annotée `@Transactional`. Si une erreur survient, toutes les modifications sur la base de données sont annulées.
    *   **Logique métier** : Le service vérifie que l'utilisateur est bien un conducteur vérifié et que le véhicule lui appartient. C'est ici que les règles de votre application sont appliquées.
    *   Il crée une nouvelle entité `Trip` et la sauvegarde en utilisant `tripRepository.save()`.
    *   Il convertit (mappe) l'entité `Trip` sauvegardée en un `TripDto` pour la réponse.
4.  **`TripRepository`** :
    *   Cette interface Spring Data JPA exécute la requête `INSERT` dans la base de données MySQL.
5.  **Réponse HTTP** :
    *   Le `TripController` reçoit le `TripDto` du service.
    *   Il le sérialise en JSON et renvoie une réponse HTTP `201 CREATED` au client.

## 4. Sécurité avec JWT

L'application utilise une authentification "stateless" (sans état), idéale pour les API.

1.  **Authentification** (`/api/auth/login`) :
    *   L'utilisateur envoie son email et mot de passe.
    *   `AuthController` utilise l'`AuthenticationManager` de Spring Security pour vérifier les identifiants.
    *   Si c'est correct, `JwtService` génère un token JWT (une longue chaîne de caractères chiffrée) qui contient des informations sur l'utilisateur (comme son email et ses rôles).
    *   Ce token est renvoyé à l'utilisateur.
2.  **Requêtes authentifiées** :
    *   Pour chaque requête suivante sur un endpoint sécurisé, le client doit inclure le token JWT dans l'en-tête `Authorization: Bearer <token>`.
    *   `JwtAuthenticationFilter` : Ce filtre, que nous avons placé avant les autres, intercepte chaque requête.
    *   Il extrait le token, le valide (vérifie sa signature et sa date d'expiration) et en extrait l'email de l'utilisateur.
    *   Il charge les détails de l'utilisateur (`UserDetailsService`) et configure le contexte de sécurité de Spring.
    *   La requête peut alors continuer vers le contrôleur, et Spring sait qui est l'utilisateur et quels sont ses rôles (`@AuthenticationPrincipal`, `@PreAuthorize`...).

## 5. Configuration et Base de Données

*   `application.properties` : Contient les informations de connexion à la base de données et les configurations de l'application.
*   **Flyway** : Au démarrage de l'application, Flyway regarde le dossier `resources/db/migration`. Il compare les fichiers SQL qui s'y trouvent avec sa table d'historique (`flyway_schema_history`) dans la base de données et applique les nouvelles migrations. C'est ce qui garantit que votre schéma de base de données est toujours à jour.
*   `ddl-auto=validate` : Après le passage de Flyway, Hibernate démarre et vérifie que vos entités Java (`@Entity`) correspondent bien au schéma de la base de données. S'il y a une incohérence, l'application ne démarre pas, vous protégeant contre les erreurs.