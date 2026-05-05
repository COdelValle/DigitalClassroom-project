# Stage 1: Build Stage
# Usa imagen Maven con JDK 21 para compilar la aplicación
FROM maven:3.9-eclipse-temurin-21 as builder

LABEL maintainers="DigitalClassroom Team"

# Establece el directorio de trabajo
WORKDIR /app

# Copia el archivo pom.xml
COPY pom.xml .

# Copia el código fuente
COPY src ./src

# Ejecuta Maven para compilar asegurando que falte si hay errores
RUN mvn clean package -DskipTests=true

# Stage 2: Runtime Stage
# Usa imagen lightweight de JDK 21 para ejecutar la aplicación
FROM eclipse-temurin:21-jre-alpine

LABEL maintainers="DigitalClassroom Team"

# Instala curl para health checks
RUN apk add --no-cache curl

# Establece el directorio de trabajo
WORKDIR /app

# Copia el JAR compilado desde el stage anterior
COPY --from=builder /app/target/student-manager-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto 8080 (puerto por defecto de Spring Boot)
EXPOSE 8080

# Health check para verificar que la aplicación está respondiendo
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
