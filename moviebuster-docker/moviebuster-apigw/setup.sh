#!/bin/bash
SRV_NAME="moviebuster-apigw"
docker stop $SRV_NAME
docker rm --force $SRV_NAME
docker rmi --force moviebuster/$SRV_NAME

pushd ../../moviebuster-apigw
mvn clean install package
popd
tar cvzf moviebuster-apigw.tar.gz ../../moviebuster-apigw 
docker build -t moviebuster/$SRV_NAME .
rm -rf moviebuster-apigw.tar.gz

./run.sh


