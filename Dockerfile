FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Copy everything and make mvnw executable
COPY . .
RUN chmod +x mvnw  # <-- THIS FIXES THE PERMISSION ERROR

# Then run Maven
RUN ./mvnw clean package -DskipTests

# Continue with runtime image...
FROM eclipse-temurin:21-jre
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]