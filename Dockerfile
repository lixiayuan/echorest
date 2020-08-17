FROM maven:3.6.3-jdk-8 as BUILD

COPY src /usr/src/myapp/src
COPY pom.xml /usr/src/myapp
#RUN mvn -f /usr/src/myapp/pom.xml clean test -Dspring.profiles.active=test
RUN mvn -f /usr/src/myapp/pom.xml -Dtomcat.util.http.parser.HttpParser.requestTargetAllow=[] package

#release container
FROM openjdk:8-jre-alpine

ARG BUILD

COPY --from=BUILD /usr/src/myapp/target/* /home/

EXPOSE 8080
JAVA_OPTS="$JAVA_OPTS -Dtomcat.util.http.parser.HttpParser.requestTargetAllow={}
CMD ["java", "-jar", "/home/rest.service-0.0.1-SNAPSHOT.jar"]