# syntax=docker/dockerfile:1

# Build stage runs on the BUILD machine's own architecture (fast, no emulation),
# even when the final image targets arm64.
FROM --platform=$BUILDPLATFORM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY . .
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew :server:buildFatJar --no-daemon

# Runtime stage: only a JRE, runs as a non-root user.
FROM eclipse-temurin:25-jre
RUN useradd --system --uid 10001 kambio
WORKDIR /app
COPY --from=build /app/server/build/libs/kambio.jar app.jar
USER kambio
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]