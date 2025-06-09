# Build stage
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Copy with proper permissions
COPY . .
RUN chmod +x mvnw

# Build with Maven (cache dependencies first for faster rebuilds)
RUN ./mvnw dependency:go-offline -B
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy built JAR
COPY --from=builder /app/target/*.jar app.jar

# Security best practice
RUN useradd -m appuser && chown -R appuser:appuser /app
USER appuser

# Health check (adjust endpoint if not using Spring Boot Actuator)
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Optimized JVM flags for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]