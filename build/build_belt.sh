#!/bin/sh

##
CWD=$(pwd)
echo "CWD: $CWD"

# build belt project of iAsset
cd ../devices/devices.conveyorbelt
mvn clean package
mvn verify
cd "$CWD"

