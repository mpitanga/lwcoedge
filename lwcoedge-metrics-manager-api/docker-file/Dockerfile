FROM openjdk:11.0.3-jdk-slim

MAINTAINER Marcelo Pitanga <mpitanga@gmail.com>

# environment variables
ENV APP_DIR=/lwcoedge
ENV APP=lwcoedge-metrics-manager-api-0.0.1-SNAPSHOT.jar

#copy JAR and property files to the image
COPY $APP $APP_DIR/$APP

#set the working directory
WORKDIR $APP_DIR

ENTRYPOINT ["java", "-XX:+UseG1GC", "-Xmx64m", "-Xms8m", "-jar", "lwcoedge-metrics-manager-api-0.0.1-SNAPSHOT.jar"]
