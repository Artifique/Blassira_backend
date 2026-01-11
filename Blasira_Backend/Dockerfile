# Dockerfile pour déploiement sur Render
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Installer Maven
RUN apk add --no-cache maven

# Copier les fichiers de configuration Maven
# Si le Dockerfile est dans BL/Blasira_Backend/, copier depuis le répertoire parent
COPY Blasira_Backend/pom.xml .
COPY Blasira_Backend/.mvn .mvn
COPY Blasira_Backend/mvnw .
RUN chmod +x mvnw

# Télécharger les dépendances (cache layer)
RUN ./mvnw dependency:go-offline -B

# Copier le code source
COPY Blasira_Backend/src ./src

# Construire l'application
RUN ./mvnw clean package -DskipTests

# Exposer le port
EXPOSE 8080

# Commande pour démarrer l'application
CMD ["java", "-jar", "target/Blasira_Backend-0.0.1-SNAPSHOT.jar"]
