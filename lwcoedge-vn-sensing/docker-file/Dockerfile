FROM openjdk:10-jdk-slim

MAINTAINER Marcelo Pitanga <mpitanga@gmail.com>
LABEL Description="LW-CoS Virtual Node - Sensing"

# environment variables
ENV APP_DIR=/lwcos/vn-se
ENV APP=lwcos-vn-se-0.0.1.jar

#copy JAR and property files to the image
COPY lwcos-vn-se-0.0.1-SNAPSHOT.jar $APP_DIR/$APP

#set the working directory
WORKDIR $APP_DIR

#kick off the micro service
EXPOSE 1001

#ENTRYPOINT java -Xmx64m -Xms8m -XX:+UseG1GC -jar $APP
ENTRYPOINT ["java", "-XX:+UseG1GC", "-jar", "lwcos-vn-se-0.0.1.jar"]
