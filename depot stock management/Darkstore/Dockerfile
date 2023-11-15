# Use the official OpenJDK as the base image
FROM openjdk:11-jre-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/Darkstore-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your Spring Boot application is running on
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "app.jar"]