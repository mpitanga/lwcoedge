#!/bin/sh

ip=$(ip route get 8.8.8.8 | sed -n '/src/{s/.*src *//p;q}')

echo "Starting lwcoedge manager api..."
docker run -d -p 10500:10500 --name lwcoedge-metrics-manager-api --hostname $ip lwcoedge-metrics-manager-api "--server.port=10500"
sleep 6

echo "Starting catalog manager..."
docker run -d -p 10000:10000 --name lwcoedge-catalog-manager --hostname $ip lwcoedge-catalog-manager "--server.port=10000" "--CatalogDescriptors=/lwcoedge/lwcoedge-catalogdescriptors.json" 
sleep 10

echo "Starting vn-instancecache..."
docker run -d -p 10001:10001 --name lwcoedge-vn-instancecache --hostname $ip lwcoedge-vn-instancecache "--server.port=10001" 
sleep 6

echo "Starting edgenode manager..."
docker run -d -p 10002:10002 --name lwcoedge-edgenode-manager --hostname $ip lwcoedge-edgenode-manager "--server.port=10002" "--NeededResources=/lwcoedge/lwcoedge-neededresources.json" "--EdgenodeConfig=/lwcoedge/lwcoedge-edgenodeconfig.json" "--PortsConfig=/lwcoedge/lwcoedge-components-port.json"
sleep 10

echo "Starting monitor..."
docker run -d -p 10005:10005 --name lwcoedge-monitor --hostname $ip lwcoedge-monitor "--server.port=10005" 
sleep 6

echo "Starting p2pcollaboration..."
docker run -d -p 10006:10006 --name lwcoedge-p2pcollaboration --hostname $ip lwcoedge-p2pcollaboration "--server.port=10006" "--PortsConfig=/lwcoedge/lwcoedge-components-port.json"
sleep 10

echo "Starting p2pdatasharing..."
docker run -d -p 10007:10007 --name lwcoedge-p2pdatasharing --hostname $ip lwcoedge-p2pdatasharing "--server.port=10007" "--PortsConfig=/lwcoedge/lwcoedge-components-port.json"
sleep 10
