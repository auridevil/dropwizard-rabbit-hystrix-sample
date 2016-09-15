#!/bin/bash
SRV_NAME="demobuser-movie"
docker stop $SRV_NAME
docker rm $SRV_NAME 
docker run -d -p 8089:8089 --link cb-rabbit --link cb-mysql:mysql --name $SRV_NAME moviebuster/$SRV_NAME:latest 
