#!/bin/bash
SRV_NAME="moviebuster-apigw"
docker stop $SRV_NAME
docker rm $SRV_NAME 
docker run -d -p 8080:8080 --link cb-rabbit --name $SRV_NAME moviebuster/$SRV_NAME:latest 
