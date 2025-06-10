# Build stage
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Copy mvnw and .mvn first
COPY mvnw .
COPY .mvn ./.mvn

# Now copy pom.xml
COPY pom.xml .

# Make mvnw executable and download dependencies
RUN chmod +x ./mvnw && \
    ./mvnw dependency:go-offline -B

# Copy full source code
COPY . .

# Build the app
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

EXPOSE 8080

# Copy the JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]