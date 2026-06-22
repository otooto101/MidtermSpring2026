# ---- Build stage ----
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copy wrapper + build config first for better layer caching
COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw

# Download dependencies (cached layer when pom.xml unchanged)
RUN ./mvnw -B -q dependency:resolve

# Copy sources and build the fat jar (skip tests in Docker build; tests run via mvn test)
COPY src ./src
RUN ./mvnw -B -q -DskipTests package

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/midterm-uno.jar app.jar

# Default: 3 bots, 1 game. Override at docker run time with extra args.
ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["--bots", "3", "--games", "1"]

