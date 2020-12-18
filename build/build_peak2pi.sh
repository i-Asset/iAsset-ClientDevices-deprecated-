#!/bin/sh

##
CWD=$(pwd)
echo "CWD: $CWD"

# build Peak2Pi project of iAsset
cd ../devices/devices.Peak2Pi
mvn clean install
mvn verify
cd "$CWD"

