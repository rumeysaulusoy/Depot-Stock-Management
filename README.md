# Depot Stock Management
 This is a Spring Boot application for managing depot stocks and transferring product stocks between depots.

Prerequisites
Java 8 or later
Maven
Docker
PostgreSQL

API Documentation
Access the Swagger UI documentation at http://localhost:8080/swagger-ui.html after starting the application.

Database
The application uses PostgreSQL. Database schema details can be found in src/main/resources/db/schema.sql.

Testing
Run unit tests: mvn test

Docker
Build and run the Docker container:

Build the Docker image: docker build -t depot-stock-management .
Run the Docker container: docker run -p 8080:8080 depot-stock-management


