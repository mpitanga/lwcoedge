#! /bin/sh

echo "Starting edgenode manager..."
nohup java -XX:+UseG1GC -Xmx64m -Xms4m -jar lwcoedge-edgenode-manager-0.0.1-SNAPSHOT.jar --server.port=10002 --logging.file=./log/lwcoedge-edgenode-manager.log --NeededResources=./lwcoedge-neededresources.json --EdgenodeConfig=./lwcoedge-edgenodeconfig.json --PortsConfig=./lwcoedge-components-port.json  >/dev/null 2>Error.err </dev/null & 
