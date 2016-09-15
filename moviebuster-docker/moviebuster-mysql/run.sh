#!/bin/bash
SRV_NAME="cb-mysql"
SRV_DATABASE="moviebuster"
SRV_USER="moviebuster"
SRV_PASSWORD="moviebuster"
SRV_ROOT_PASSWORD="qwertyuiop"

docker stop $SRV_NAME
docker rm $SRV_NAME

docker run -P --name $SRV_NAME -e MYSQL_ROOT_PASSWORD=$SRV_ROOT_PASSWORD -e MYSQL_DATABASE=$SRV_DATABASE -e MYSQL_USER=$SRV_USER -e MYSQL_PASSWORD=$SRV_PASSWORD --hostname $SRV_NAME -d mysql:5.6 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
