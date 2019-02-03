#! /bin/sh

echo "Starting resource allocator..."
nohup java -XX:+UseG1GC -Xmx32m -Xms1m -jar lwcoedge-resourceallocator-0.0.1-SNAPSHOT.jar --server.port=10004 --logging.file=./log/lwcoedge-resourceallocator.log --PortsConfig=./lwcoedge-components-port.json  >/dev/null 2>Error.err </dev/null & 
