# Use OpenJDK image
FROM openjdk:17-jdk-slim

LABEL maintainer="alessandra_muhlbeier"

# Set the working directory
WORKDIR /app

# Copy built JAR file into the container
COPY target/walletapi-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the app will run on
EXPOSE 8080

# Run the JAR file when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]
