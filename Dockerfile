# Build stage
FROM eclipse-temurin:21-jdk as builder

# 1. Copy build files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Make mvnw executable and BUILD THE DAMN THING
RUN chmod +x mvnw && \
    ./mvnw clean package -DskipTests && \
    ls -la /app/target/ # Debug: show me the damn files

# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

# Explicitly copy the jar (no wildcards)
COPY --from=builder /app/target/your-application-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]