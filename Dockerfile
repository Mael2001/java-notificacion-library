# ---- Build stage ----
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app

# Cache deps
COPY pom.xml ./
RUN mvn -q -DskipTests dependency:go-offline

# Build
COPY src ./src
RUN mvn -q -DskipTests clean package

# Copy ALL needed deps for runtime (including compile-scoped), excluding test/provided
RUN mvn -q -DskipTests dependency:copy-dependencies \
  -DoutputDirectory=/app/deps \
  -DexcludeScope=test \
  -DexcludeScope=provided

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy compiled code + main jar + deps
COPY --from=build /app/target/classes /app/classes
COPY --from=build /app/target/java-notification-library-1.0-SNAPSHOT.jar /app/app.jar
COPY --from=build /app/deps /app/deps

# Optional: quick sanity check during build (can remove later)
# RUN ls -1 deps | grep slf4j || true

ENTRYPOINT ["java", "-cp", "classes:app.jar:deps/*", "com.github.mael2001.examples.NotificationExamples"]
