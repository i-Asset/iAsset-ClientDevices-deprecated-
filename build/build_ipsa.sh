#!/bin/sh

##
CWD=$(pwd)
echo "CWD: $CWD"

# build IPSA project of iAsset
cd ../devices/devices.IPSA
mvn clean package
mvn verify
cd "$CWD"

