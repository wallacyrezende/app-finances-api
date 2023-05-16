FROM openjdk:11-jre-slim
VOLUME /tmp
EXPOSE 8888

ENV SPRING_PROFILES_ACTIVE=dev

ADD ./target/config-server-0.0.1-SNAPSHOT.jar config-server.jar
ENTRYPOINT ["java", "-jar", "config-server.jar"]