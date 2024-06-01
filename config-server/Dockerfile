FROM maven:3.8.4-openjdk-11-slim AS build
WORKDIR /app
COPY . /app
RUN mvn clean install
FROM openjdk:11-jdk-slim
WORKDIR /app

COPY --from=build /app/target/config-server-0.0.1-SNAPSHOT.jar config-server.jar

ENV SPRING_PROFILES_ACTIVE=dev
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "config-server.jar"]