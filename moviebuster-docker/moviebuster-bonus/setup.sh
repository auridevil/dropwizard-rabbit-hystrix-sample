#!/bin/bash
SRV_NAME="demobuser-bonus"
docker stop $SRV_NAME
docker rm --force $SRV_NAME
docker rmi --force moviebuster/$SRV_NAME

pushd ../../moviebuster-bonus
mvn clean install package
popd
tar cvzf moviebuster-bonus.tar.gz ../../moviebuster-bonus
docker build -t moviebuster/$SRV_NAME .
rm -rf moviebuster-bonus.tar.gz

./run.sh


