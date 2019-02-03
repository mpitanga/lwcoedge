#! /bin/sh

echo "Starting vn-instancecache..."
nohup java -XX:+UseG1GC -Xmx64m -Xms4m -jar lwcoedge-vn-instancecache-0.0.1-SNAPSHOT.jar --server.port=10001 --logging.file=./log/lwcoedge-vn-instancecache.log  >/dev/null 2>Error.err </dev/null & 
