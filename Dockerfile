# Stage 1: Build the application
FROM maven:3.9.5-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN ./mvnw dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/microsite-0.0.1-SNAPSHOT.jar app.jar

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
