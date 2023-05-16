FROM openjdk:11-jre-slim
VOLUME /tmp
EXPOSE 8443

ADD ./target/auth-server-0.0.1-SNAPSHOT.jar auth-server.jar
ENTRYPOINT ["java", "-jar", "auth-server.jar"]

