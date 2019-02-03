#! /bin/sh

echo "Starting monitor..."
nohup java -XX:+UseG1GC -Xmx32m -Xms1m -jar lwcoedge-monitor-0.0.1-SNAPSHOT.jar --server.port=10005 --logging.file=./log/lwcoedge-monitor.log  >/dev/null 2>Error.err </dev/null & 
