#! /bin/sh

echo "Starting resource provisioner..."
nohup java -XX:+UseG1GC -Xmx64m -Xms4m -jar lwcoedge-resourceprovisioner-0.0.1-SNAPSHOT.jar --server.port=10003 --logging.file=./log/lwcoedge-resourceprovisioner.log --PortsConfig=./lwcoedge-components-port.json  >/dev/null 2>Error.err </dev/null & 
