#!/bin/sh

##
CWD=$(pwd)
echo "CWD: $CWD"

# build ARTI Chasi project of iAsset
cd ../devices/devices.chasi
mvn clean install
mvn verify
cd "$CWD"

