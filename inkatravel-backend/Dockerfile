# 1. Etapa de Construcción (CAMBIO: Usamos temurin-21)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos el archivo de configuración y descargamos dependencias
COPY pom.xml .

# Copiamos el código fuente
COPY src ./src

# Compilamos el proyecto y creamos el JAR (saltando tests para rapidez)
RUN mvn clean package -DskipTests

# 2. Etapa de Ejecución (CAMBIO: Usamos temurin-21)
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copiamos el JAR generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto 8080
EXPOSE 8080

# Comando para iniciar la app
ENTRYPOINT ["java", "-jar", "app.jar"]