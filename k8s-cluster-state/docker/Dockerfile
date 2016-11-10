FROM openjdk:8

ADD k8s-cluster-state.jar /
ADD application.properties /

ENTRYPOINT java -jar k8s-cluster-state.jar --spring.config.location=file:/application.properties
