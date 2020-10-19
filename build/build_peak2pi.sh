#!/bin/sh

##
CWD=$(pwd)
echo "CWD: $CWD"

# build Peak2Pi project of iAsset
cd ../devices/peak2pi/basys.Peak2Pi
mvn clean package
mvn verify
cd "$CWD"

