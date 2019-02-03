#! /bin/sh

echo "Starting lwcoedge-metrics-manager-api..."
nohup java -XX:+UseShenandoahGC -Xmx128m -Xms32m -XX:+UnlockExperimentalVMOptions -XX:ShenandoahUncommitDelay=2000 -XX:ShenandoahGuaranteedGCInterval=10000 -jar lwcoedge-metrics-manager-api-0.0.1-SNAPSHOT.jar --server.port=10500 --logging.file=./log/lwcoedge-metrics-manager-api.log >/dev/null 2>Error.err </dev/null &
