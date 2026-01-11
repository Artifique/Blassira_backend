# Dockerfile pour déploiement sur Render
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Installer Maven
RUN apk add --no-cache maven

# Copier les fichiers de configuration Maven
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw

# Télécharger les dépendances (cache layer)
RUN ./mvnw dependency:go-offline -B

# Copier le code source
COPY src ./src

# Construire l'application
RUN ./mvnw clean package -DskipTests

# Exposer le port
EXPOSE 8080

# Commande pour démarrer l'application
CMD ["java", "-jar", "target/Blasira_Backend-0.0.1-SNAPSHOT.jar"]
