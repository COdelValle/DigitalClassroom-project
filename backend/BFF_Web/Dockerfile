# Multi-stage Dockerfile for BFF Web
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 as builder
WORKDIR /app
COPY pom.xml ./pom.xml
COPY BFF_Web/pom.xml ./BFF_Web/pom.xml
COPY BFF_Web/src ./BFF_Web/src
WORKDIR /app/BFF_Web
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/BFF_Web/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
