#! /bin/sh

echo "Starting lwcoedge manager api..."
nohup java -XX:+UseG1GC -Xmx128m -Xms1m -jar lwcoedge-metrics-manager-api-0.0.1-SNAPSHOT.jar --server.port=10500 --logging.file=./log/lwcoedge-metrics-manager-api.log >/dev/null 2>Error.err </dev/null &
sleep 12

echo "Starting catalog manager..."
nohup java -XX:+UseG1GC -Xmx32m -Xms1m -jar lwcoedge-catalog-manager-0.0.1-SNAPSHOT.jar --server.port=10000 --logging.file=./log/lwcoedge-catalog-manager.log --CatalogDescriptors=./lwcoedge-catalogdescriptors.json >/dev/null 2>Error.err </dev/null & 
sleep 12

echo "Starting vn-instancecache..."
nohup java -XX:+UseG1GC -Xmx64m -Xms4m -jar lwcoedge-vn-instancecache-0.0.1-SNAPSHOT.jar --server.port=10001 --logging.file=./log/lwcoedge-vn-instancecache.log  >/dev/null 2>Error.err </dev/null & 
sleep 12

echo "Starting edgenode manager..."
nohup java -XX:+UseG1GC -Xmx64m -Xms4m -jar lwcoedge-edgenode-manager-0.0.1-SNAPSHOT.jar --server.port=10002 --logging.file=./log/lwcoedge-edgenode-manager.log --NeededResources=./lwcoedge-neededresources.json --EdgenodeConfig=./lwcoedge-edgenodeconfig.json --PortsConfig=./lwcoedge-components-port.json  >/dev/null 2>Error.err </dev/null & 
sleep 12

echo "Starting monitor..."
nohup java -XX:+UseG1GC -Xmx32m -Xms1m -jar lwcoedge-monitor-0.0.1-SNAPSHOT.jar --server.port=10005 --logging.file=./log/lwcoedge-monitor.log  >/dev/null 2>Error.err </dev/null & 
sleep 12

echo "Starting p2p collaboration..."
nohup java -XX:+UseG1GC -Xmx32m -Xms1m -jar lwcoedge-p2pcollaboration-0.0.1-SNAPSHOT.jar --server.port=10006 --logging.file=./log/lwcoedge-p2pcollaboration.log --PortsConfig=./lwcoedge-components-port.json  >/dev/null 2>Error.err </dev/null & 
sleep 12

echo "Starting p2p datasharing..."
nohup java -XX:+UseG1GC -Xmx32m -Xms1m -jar lwcoedge-p2pdatasharing-0.0.1-SNAPSHOT.jar --server.port=10007 --logging.file=./log/lwcoedge-p2pdatasharing.log --PortsConfig=./lwcoedge-components-port.json  >/dev/null 2>Error.err </dev/null & 
sleep 12

echo "Starting resource provisioner..."
nohup java -XX:+UseG1GC -Xmx64m -Xms4m -jar lwcoedge-resourceprovisioner-0.0.1-SNAPSHOT.jar --server.port=10003 --logging.file=./log/lwcoedge-resourceprovisioner.log --PortsConfig=./lwcoedge-components-port.json  >/dev/null 2>Error.err </dev/null & 
sleep 12

echo "Starting resource allocator..."
nohup java -XX:+UseG1GC -Xmx32m -Xms1m -jar lwcoedge-resourceallocator-0.0.1-SNAPSHOT.jar --server.port=10004 --logging.file=./log/lwcoedge-resourceallocator.log --PortsConfig=./lwcoedge-components-port.json  >/dev/null 2>Error.err </dev/null & 
sleep 12

echo "Starting application api..."
nohup java -XX:+UseG1GC -Xmx32m -Xms1m -jar lwcoedge-application-api-0.0.1-SNAPSHOT.jar --server.port=8080 --logging.file=./log/lwcoedge-application-api.log --PortsConfig=./lwcoedge-components-port.json  >/dev/null 2>Error.err </dev/null & 
sleep 12
