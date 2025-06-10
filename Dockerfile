# Build stage
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Copy only pom.xml first to leverage layer caching
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B

# Now copy all source code
COPY . .

RUN chmod +x ./mvnw
# Build the app
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

EXPOSE 8080

# Copy the JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]