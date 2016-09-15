#!/bin/bash
SRV_NAME="cb-rabbit"
SRV_USER="moviebuster"
SRV_PWD="moviebuster"
EARLANG_COOKIE="cookieearlang"
docker stop $SRV_NAME
docker rm $SRV_NAME
docker run -p 5671:5671 -p 15671:15671 -p 5672:5672 -p 15672:15672 -d --hostname $SRV_NAME --name $SRV_NAME -e RABBITMQ_DEFAULT_USER=$SRV_USER -e RABBITMQ_DEFAULT_PASS=$SRV_PWD -e RABBITMQ_ERLANG_COOKIE=$EARLANG_COOKIE -e RABBITMQ_DEFAULT_VHOST=$SRV_NAME rabbitmq:3-management
