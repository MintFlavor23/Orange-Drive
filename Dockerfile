# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml from backend directory
COPY backend/mvnw .
COPY backend/mvnw.cmd .
COPY backend/.mvn .mvn
COPY backend/pom.xml .

# Make Maven wrapper executable
RUN chmod +x ./mvnw

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# Copy source code from backend directory
COPY backend/src src

# Build the application
RUN ./mvnw clean package -DskipTests

# Create uploads directory
RUN mkdir -p uploads

# Expose port
EXPOSE 8080

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV UPLOAD_DIR=/app/uploads

# Run the application
CMD ["java", "-jar", "target/safeDrive-0.0.1-SNAPSHOT.jar"]
