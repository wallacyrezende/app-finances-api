FROM openjdk:11-jre-slim
VOLUME /tmp
EXPOSE 8765

ADD ./target/api-gateway-0.0.1-SNAPSHOT.jar api-gateway.jar
ENTRYPOINT ["java", "-jar", "api-gateway.jar"]

