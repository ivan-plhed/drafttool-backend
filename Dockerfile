# Build stage
FROM eclipse-temurin:21-jdk as builder

# 1. Create optimal layer caching for dependencies
WORKDIR /workspace
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B

# 2. Build application
COPY src src
RUN ./mvnw package -DskipTests

# 3. Extract the built JAR path from Maven output
RUN JAR_FILE=$(find /workspace/target -name '*.jar' -not -name '*-sources.jar' -not -name 'original-*.jar*' | head -n 1) && \
    echo "JAR_PATH=${JAR_FILE}" > /workspace/jar-path.env

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Get the JAR path from build stage
COPY --from=builder /workspace/jar-path.env .
RUN source /app/jar-path.env && \
    echo "Using JAR file: ${JAR_PATH}" && \
    cp ${JAR_PATH} /app/app.jar && \
    rm /app/jar-path.env

# Production-ready settings
EXPOSE 8080
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"
HEALTHCHECK --interval=30s --timeout=5s CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/app.jar"]