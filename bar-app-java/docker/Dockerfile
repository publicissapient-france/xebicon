FROM openjdk:8

ADD bar-app.jar /
ADD application.properties /

ENTRYPOINT java -jar bar-app.jar --spring.config.location=file:/application.properties
