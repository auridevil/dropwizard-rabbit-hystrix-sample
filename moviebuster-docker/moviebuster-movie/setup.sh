#!/bin/bash
SRV_NAME="demobuser-movie"
docker stop $SRV_NAME
docker rm --force $SRV_NAME
docker rmi --force moviebuster/$SRV_NAME

pushd ../../moviebuster-movie
mvn clean install package
popd
tar cvzf moviebuster-movie.tar.gz ../../moviebuster-movie
docker build -t moviebuster/$SRV_NAME .
rm -rf moviebuster-movie.tar.gz

./run.sh


