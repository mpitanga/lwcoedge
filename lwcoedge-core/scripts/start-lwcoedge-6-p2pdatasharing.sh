#! /bin/sh

echo "Starting p2p datasharing..."
nohup java -XX:+UseG1GC -Xmx32m -Xms1m -jar lwcoedge-p2pdatasharing-0.0.1-SNAPSHOT.jar --server.port=10007 --logging.file=./log/lwcoedge-p2pdatasharing.log --PortsConfig=./lwcoedge-components-port.json  >/dev/null 2>Error.err </dev/null & 
