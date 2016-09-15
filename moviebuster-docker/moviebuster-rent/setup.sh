#!/bin/bash
SRV_NAME="demobuser-rent"
docker stop $SRV_NAME
docker rm --force $SRV_NAME
docker rmi --force moviebuster/$SRV_NAME

pushd ../../moviebuster-rent
mvn clean install package
popd
tar cvzf moviebuster-rent.tar.gz ../../moviebuster-rent
docker build -t moviebuster/$SRV_NAME .
rm -rf moviebuster-rent.tar.gz

./run.sh


