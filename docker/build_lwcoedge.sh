#!/bin/bash

echo "Removing old image $1..."
docker rmi -f $1
echo "Building $1..."
cd $1
docker build -t $1 --label $1 .
cd ..
