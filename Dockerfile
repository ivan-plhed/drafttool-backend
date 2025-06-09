FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY . .
RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline -B
RUN ./mvnw clean package -DskipTests
# Copy built JAR
COPY /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar app.jar"]