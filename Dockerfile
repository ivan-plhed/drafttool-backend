# Build stage
FROM eclipse-temurin:21-jdk as builder

# 1. Set up workspace and make mvnw executable first
WORKDIR /workspace
COPY mvnw* ./
COPY .mvn/ .mvn/
RUN chmod +x mvnw  # THIS IS THE CRUCIAL FIX

# 2. Cache dependencies
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B

# 3. Build application
COPY src src
RUN ./mvnw package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the application jar (handle both regular and Spring Boot jars)
COPY --from=builder /workspace/target/*.jar ./app.jar

# Production settings
EXPOSE 8080
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/app.jar"]