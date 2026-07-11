# ---------- Stage 1: build ----------
FROM eclipse-temurin:25-jdk AS builder

WORKDIR /app

COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

RUN ./gradlew dependencies --no-daemon

COPY src ./src

RUN ./gradlew bootJar --no-daemon

# ---------- Stage 2: runtime ----------
FROM eclipse-temurin:25-jre

WORKDIR /app

RUN useradd --system --no-create-home appuser
USER appuser

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]