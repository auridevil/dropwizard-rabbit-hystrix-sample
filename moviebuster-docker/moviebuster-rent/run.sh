#!/bin/bash
SRV_NAME="demobuser-rent"
docker stop $SRV_NAME
docker rm $SRV_NAME 
docker run -d -p 8092:8092 --link cb-rabbit --link cb-mysql:mysql --name $SRV_NAME moviebuster/$SRV_NAME:latest 
