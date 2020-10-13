#!/bin/sh

##
CWD=$(pwd)
echo "CWD: $CWD"

# build base device project of iAsset
cd ../devices/BASE/basys.basedevice
mvn clean install
mvn verify
cd "$CWD"

