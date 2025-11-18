# 1. Etapa de Construcción (Usamos una imagen con Maven y Java 17)
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copiamos el archivo de configuración y descargamos dependencias
COPY pom.xml .
# (Opcional) Descargar dependencias para cachear esta capa
# RUN mvn dependency:go-offline

# Copiamos el código fuente
COPY src ./src

# Compilamos el proyecto y creamos el JAR (saltando tests para rapidez)
RUN mvn clean package -DskipTests

# 2. Etapa de Ejecución (Usamos una imagen ligera solo con Java)
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copiamos el JAR generado en la etapa anterior
# Asegúrate de que el nombre coincida con el que genera tu pom.xml
# Usualmente es inkatravel-backend-0.0.1-SNAPSHOT.jar
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto 8080
EXPOSE 8080

# Comando para iniciar la app
ENTRYPOINT ["java", "-jar", "app.jar"]