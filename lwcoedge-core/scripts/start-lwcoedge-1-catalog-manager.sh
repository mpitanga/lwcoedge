#! /bin/sh

echo "Starting catalog manager..."
nohup java -XX:+UseG1GC -Xmx32m -Xms1m -jar lwcoedge-catalog-manager-0.0.1-SNAPSHOT.jar --server.port=10000 --logging.file=./log/lwcoedge-catalog-manager.log --CatalogDescriptors=./lwcoedge-catalogdescriptors.json >/dev/null 2>Error.err </dev/null & 
