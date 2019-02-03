#! /bin/sh

echo "Starting p2p collaboration..."
nohup java -XX:+UseG1GC -Xmx32m -Xms1m -jar lwcoedge-p2pcollaboration-0.0.1-SNAPSHOT.jar --server.port=10006 --logging.file=./log/lwcoedge-p2pcollaboration.log --PortsConfig=./lwcoedge-components-port.json  >/dev/null 2>Error.err </dev/null & 
