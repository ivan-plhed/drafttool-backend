# Build stage
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Copy Maven Wrapper and pom.xml first
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the app
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

EXPOSE 8080

# Copy the JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]