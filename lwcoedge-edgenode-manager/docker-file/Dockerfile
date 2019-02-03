FROM openjdk:12-alpine

MAINTAINER Marcelo Pitanga <mpitanga@gmail.com>

# environment variables
ENV APP_DIR=/lwcoedge
ENV APP=lwcoedge-edgenode-manager-0.0.1-SNAPSHOT.jar
ENV JSON1=lwcoedge-neededresources.json
ENV JSON2=lwcoedge-edgenodeconfig.json
ENV JSON3=lwcoedge-components-port.json 

#copy JAR and property files to the image
COPY lwcoedge-edgenode-manager-0.0.1-SNAPSHOT.jar $APP_DIR/$APP
COPY $JSON1 $APP_DIR/$JSON1
COPY $JSON2 $APP_DIR/$JSON2
COPY $JSON3 $APP_DIR/$JSON3

#set the working directory
WORKDIR $APP_DIR

ENTRYPOINT ["java", "-XX:+UseG1GC", "-jar", "lwcoedge-edgenode-manager-0.0.1-SNAPSHOT.jar"]
