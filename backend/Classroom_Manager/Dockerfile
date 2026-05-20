# Multi-stage Dockerfile for Classroom Manager
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 as builder
WORKDIR /app
COPY pom.xml ./pom.xml
COPY Classroom_Manager/pom.xml ./Classroom_Manager/pom.xml
COPY Classroom_Manager/src ./Classroom_Manager/src
WORKDIR /app/Classroom_Manager
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/Classroom_Manager/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
