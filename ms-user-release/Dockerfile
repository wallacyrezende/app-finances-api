FROM openjdk:11-jre-slim
VOLUME /tmp
EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=dev

ADD ./target/ms-user-release-0.0.1-SNAPSHOT.jar ms-user-release.jar
ENTRYPOINT ["java", "-jar", "ms-user-release.jar"]