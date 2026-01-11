# Dockerfile pour déploiement sur Render (optionnel)
# Render peut aussi utiliser directement Maven sans Docker
FROM openjdk:17-jdk-slim

WORKDIR /app

# Installer Maven si nécessaire (Render a déjà Maven)
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Copier les fichiers de configuration Maven
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw

# Télécharger les dépendances (cache layer)
RUN ./mvnw dependency:go-offline -B || mvn dependency:go-offline -B

# Copier le code source
COPY src ./src

# Construire l'application
RUN ./mvnw clean package -DskipTests || mvn clean package -DskipTests

# Exposer le port
EXPOSE 8080

# Commande pour démarrer l'application
CMD ["java", "-jar", "target/Blasira_Backend-0.0.1-SNAPSHOT.jar"]
