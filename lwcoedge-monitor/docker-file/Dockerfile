FROM openjdk:12-alpine

MAINTAINER Marcelo Pitanga <mpitanga@gmail.com>

# environment variables
ENV APP_DIR=/lwcoedge
ENV APP=lwcoedge-monitor-0.0.1-SNAPSHOT.jar

#copy JAR and property files to the image
COPY lwcoedge-monitor-0.0.1-SNAPSHOT.jar $APP_DIR/$APP

#set the working directory
WORKDIR $APP_DIR

ENTRYPOINT ["java", "-XX:+UseG1GC", "-jar", "lwcoedge-monitor-0.0.1-SNAPSHOT.jar"]
