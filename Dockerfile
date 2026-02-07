FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests
CMD ["java", "-cp", "target/java-notification-library-1.0-SNAPSHOT.jar:target/classes", "com.github.mael2001.examples.NotificationExamples"]