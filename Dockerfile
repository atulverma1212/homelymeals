# Multi-stage build for Spring Boot application

# Build stage
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies separately to leverage Docker cache
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create a non-root user to run the application
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Environment variables (these will be provided at runtime)
ENV DATASOURCE_URL=jdbc:postgresql://db:5432/homely \
    DATASOURCE_USERNAME=postgres \
    DATASOURCE_PWD=postgres \
    AWS_SECRET_KEY=your_aws_secret_key \
    AWS_ACCESS_KEY=your_aws_access_key \
    JWT_SECRET=your_jwt_secret

# Expose the port the app runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]