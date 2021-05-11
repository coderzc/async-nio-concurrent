FROM adoptopenjdk:11.0.11_9-jre-openj9-0.26.0

LABEL maintainer="coderzc async-nio-concurrent"

VOLUME /tmp
ADD target/async-nio-concurrent-0.0.1-SNAPSHOT.jar async-nio-concurrent.jar

ENV mysql_hostname=mysql57

EXPOSE 8081 8081
EXPOSE 8088 8088

ENTRYPOINT ["JAVA", "-jar", "async-nio-concurrent.jar"]
