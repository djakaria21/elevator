# Use official Gradle image with Java 17
FROM gradle:7.6.1-jdk17

# Set working directory
WORKDIR /app

# Copy gradle files first for caching
COPY build.gradle .
COPY settings.gradle .
COPY gradlew .
COPY gradle/ gradle/

# Copy source code
COPY src/ src/

# Run tests
CMD ["./gradlew", "test"]
