#! /bin/sh

echo "Starting virtual node (temperature)..."
nohup java -XX:+UseG1GC -Xmx64m -Xms1m -jar lwcoedge-vn-sensing-0.0.1-SNAPSHOT.jar  --server.port=11001 --datasharing.port=10007 --managerapi.port=10500 --logging.file=./log/lwcoedge-vn-sensing-UFRJ.UbicompLab.humidity.log --VN={"id":"en1.UFRJ.UbicompLab.humidity","datatype":{"id":{"where":"UFRJ","who":"UbicompLab","what":"humidity"},"description":null,"type":"SIMPLE","element":[{"value":"DeviceID2"}]},"data":{},"neighbors":[],"port":11001}
