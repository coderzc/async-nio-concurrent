FROM openjdk:8-jdk-alpine

LABEL maintainer="coderzc async-nio-concurrent"

VOLUME /tmp
ADD target/async-nio-concurrent-0.0.1-SNAPSHOT.jar async-nio-concurrent.jar

EXPOSE 8081 8081
EXPOSE 8088 8088

ENTRYPOINT ["JAVA", "-jar", "async-nio-concurrent.jar"]
