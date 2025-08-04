# -- Étape 1: Construction de l'application Java
# Utilise une image Java JDK pour compiler et construire le projet
FROM maven:3.8.7-openjdk-21 AS build

# Définit le répertoire de travail dans le conteneur
WORKDIR /app

# Copie les fichiers de configuration du projet
COPY pom.xml .

# Télécharge les dépendances pour la construction (mise en cache des couches de Docker)
RUN mvn dependency:go-offline -B

# Copie le code source de l'application
COPY src ./src

# Compile et package l'application en un fichier JAR
RUN mvn package -DskipTests

# -- Étape 2: Création de l'image finale
# Utilise une image JRE plus petite et sécurisée pour exécuter l'application
FROM eclipse-temurin:21-jre-jammy

# Définit un argument pour le chemin du fichier JAR
ARG JAR_FILE=target/atlantique-0.0.1-SNAPSHOT.jar

# Copie le fichier JAR depuis la première étape vers l'image finale
COPY --from=build /app/${JAR_FILE} app.jar

# Définit le port sur lequel l'application s'exécute
EXPOSE 7070

# Point d'entrée de l'application
ENTRYPOINT ["java", "-jar", "/app.jar"]