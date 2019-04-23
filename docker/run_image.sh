#!/bin/sh

ip=$(ip route get 8.8.8.8 | sed -n '/src/{s/.*src *//p;q}')

echo "Starting $1..."
docker run -d -p $2:$2 --name $1 --hostname $ip $1 "--server.port=$2" "--PortsConfig=/lwcoedge/lwcoedge-components-port.json" $3 $4 $5
