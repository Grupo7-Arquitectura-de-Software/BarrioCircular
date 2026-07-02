FROM maven:3.9.11-eclipse-temurin-17 AS build

WORKDIR /workspace

COPY pom.xml mvnw ./
COPY .mvn .mvn

RUN chmod +x mvnw \
    && ./mvnw -B dependency:go-offline

COPY src src

RUN ./mvnw -B clean package -DskipTests


FROM eclipse-temurin:17-jre-alpine AS runtime

WORKDIR /app

RUN addgroup -S spring \
    && adduser -S spring -G spring

COPY --from=build --chown=spring:spring \
    /workspace/target/backend-0.0.1-SNAPSHOT.jar app.jar

USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
