FROM maven:3.8.4-openjdk-11-slim AS build
WORKDIR /app
COPY . /app
RUN mvn clean install
FROM openjdk:11-jdk-slim
WORKDIR /app

COPY --from=build /app/target/api-gateway-0.0.1-SNAPSHOT.jar /app/api-gateway.jar

ENV SPRING_PROFILES_ACTIVE=dev
EXPOSE 8765
ENTRYPOINT ["java", "-jar", "api-gateway.jar"]