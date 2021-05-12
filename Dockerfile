FROM openjdk:11.0.7

LABEL maintainer="coderzc async-nio-concurrent"

VOLUME /tmp
ADD async-nio-concurrent-0.0.1-SNAPSHOT.jar async-nio-concurrent.jar

ENV mysql_hostname=mysql57

EXPOSE 8088 8081

ENTRYPOINT ["java", "-jar", "async-nio-concurrent.jar"]
