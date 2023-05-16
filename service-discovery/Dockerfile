FROM openjdk:11-jre-slim
VOLUME /tmp
EXPOSE 8761

ADD ./target/service-discovery-0.0.1-SNAPSHOT.jar service-discovery.jar
ENTRYPOINT ["java", "-jar", "service-discovery.jar"]

