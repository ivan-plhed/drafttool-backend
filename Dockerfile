# Build stage - keep it simple stupid
FROM eclipse-temurin:21-jdk as builder

# 1. Copy ONLY what's needed for dependency resolution
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make mvnw executable (this is what you were missing)
RUN chmod +x mvnw

# Download dependencies first (separate layer for caching)
RUN ./mvnw dependency:go-offline -B

# 2. Now copy the rest
COPY src src

# Build the damn thing
RUN ./mvnw clean package -DskipTests

# Runtime stage - lean and mean
FROM eclipse-temurin:21-jre

# No fancy user shit that breaks things
WORKDIR /app

# Copy the jar (wildcard works fine 99% of time)
COPY --from=builder /app/target/*.jar app.jar

# Expose port (mostly documentation)
EXPOSE 8080

# Run it
ENTRYPOINT ["java", "-jar", "app.jar"]