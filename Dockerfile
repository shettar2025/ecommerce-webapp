# Use a minimal Java image
FROM eclipse-temurin:17-jdk

# Create a directory in the container for the application
WORKDIR /app

# Copy your already-built JAR into the container
# Replace 'ecommerce-0.0.1-SNAPSHOT.jar' with your actual jar file name
COPY /target/ecommerce-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080 (if your app listens on 8080)
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
