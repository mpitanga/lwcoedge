#! /bin/sh

echo "Starting application api..."
nohup java -XX:+UseG1GC -Xmx32m -Xms1m -jar lwcoedge-application-api-0.0.1-SNAPSHOT.jar --server.port=8080 --logging.file=./log/lwcoedge-application-api.log --PortsConfig=./lwcoedge-components-port.json  >/dev/null 2>Error.err </dev/null & 
