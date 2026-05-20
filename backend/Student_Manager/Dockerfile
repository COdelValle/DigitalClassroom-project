# Multi-stage Dockerfile for Student Manager
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 as builder
WORKDIR /app
COPY pom.xml ./pom.xml
COPY Student_Manager/pom.xml ./Student_Manager/pom.xml
COPY Student_Manager/src ./Student_Manager/src
WORKDIR /app/Student_Manager
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/Student_Manager/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]