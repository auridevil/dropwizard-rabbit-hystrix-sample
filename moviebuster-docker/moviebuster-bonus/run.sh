#!/bin/bash
SRV_NAME="demobuser-bonus"
docker stop $SRV_NAME
docker rm $SRV_NAME 
docker run -d -p 8086:8086 --link cb-rabbit --link cb-mysql:mysql --name $SRV_NAME moviebuster/$SRV_NAME:latest 
