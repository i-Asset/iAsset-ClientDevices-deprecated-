#!/bin/sh

##
CWD=$(pwd)
echo "CWD: $CWD"

# build panda project of iAsset
cd ../devices/devices.panda
mvn clean install
mvn verify
cd "$CWD"

