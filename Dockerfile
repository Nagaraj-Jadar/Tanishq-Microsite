# Stage 1: Build
FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app

# Copy Maven wrapper files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# ✅ Add this line to fix "Permission denied"
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the app
RUN ./mvnw package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
