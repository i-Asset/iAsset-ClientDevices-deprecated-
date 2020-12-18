#!/bin/sh

##
CWD=$(pwd)
echo "CWD: $CWD"

# build panda project of iAsset
cd ../devices/devices.panda
mvn clean package
mvn verify
cd "$CWD"

