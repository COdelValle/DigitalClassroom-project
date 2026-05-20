# Multi-stage Dockerfile for Assessment Manager
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 as builder
WORKDIR /app
COPY pom.xml ./pom.xml
COPY Assessment_Manager/pom.xml ./Assessment_Manager/pom.xml
COPY Assessment_Manager/src ./Assessment_Manager/src
WORKDIR /app/Assessment_Manager
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/Assessment_Manager/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]