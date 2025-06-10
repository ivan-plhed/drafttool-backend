# Build stage
FROM eclipse-temurin:21-jdk-jammy AS builder

# Create a dedicated user for security
RUN useradd -m appuser
WORKDIR /app
RUN chown appuser:appuser /app
USER appuser

# Cache Maven dependencies
COPY --chown=appuser:appuser mvnw .
COPY --chown=appuser:appuser .mvn .mvn
COPY --chown=appuser:appuser pom.xml .

# Download dependencies (cache this layer)
RUN ./mvnw verify --fail-never -DskipTests

# Copy source code
COPY --chown=appuser:appuser src src

# Build the application (produces a single JAR with dependencies)
RUN ./mvnw clean package -DskipTests -DfinalName=app

# Runtime stage
FROM eclipse-temurin:21-jre-jammy

# Security: non-root user
RUN useradd -m appuser
WORKDIR /app
RUN chown appuser:appuser /app
USER appuser

# Copy the built JAR
COPY --from=builder --chown=appuser:appuser /app/target/app.jar .

# Health check
HEALTHCHECK --interval=30s --timeout=5s \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

# Optimized JVM options
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]