# Build stage
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Copy with proper permissions
COPY . .
RUN chmod +x mvnw

# Build with Maven (cache dependencies first for faster rebuilds)
RUN ./mvnw dependency:go-offline -B
RUN ./mvnw clean package -DskipTests
# Copy built JAR
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar app.jar"]