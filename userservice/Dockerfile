# Use a lightweight JDK base image
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file into the container
# Replace `orderservice-0.0.1-SNAPSHOT.jar` with your actual JAR name
COPY target/userservice-0.0.1-SNAPSHOT.jar app.jar

# Expose the port (match the `server.port` in your properties file, e.g., 8081)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]