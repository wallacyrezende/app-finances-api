FROM maven:3.8.4-openjdk-11-slim AS build
WORKDIR /app
COPY . /app
RUN mvn clean install
FROM openjdk:11-jdk-slim
WORKDIR /app

COPY --from=build /app/target/service-discovery-0.0.1-SNAPSHOT.jar service-discovery.jar

ENV SPRING_PROFILES_ACTIVE=dev
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "service-discovery.jar"]