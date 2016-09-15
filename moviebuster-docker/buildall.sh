#!/bin/bash
echo "Starting Casumobuster ms Dev Topology"
pushd moviebuster-mysql
./run.sh
popd
pushd moviebuster-rabbit
./run.sh
popd
pushd moviebuster-apigw
./setup.sh
popd
pushd moviebuster-bonus
./setup.sh
popd
pushd moviebuster-movie
./setup.sh
popd
pushd moviebuster-rent
./setup.sh
popd
