# Java 17 image
FROM eclipse-temurin:17-jdk

# Container daxilində iş qovluğu
WORKDIR /app

# Jar faylı container-a köçür
COPY target/api-gateway-service-0.0.1-SNAPSHOT.jar app.jar

# Port aç
EXPOSE 8080

# Run əmri
ENTRYPOINT ["java", "-jar", "app.jar"]
