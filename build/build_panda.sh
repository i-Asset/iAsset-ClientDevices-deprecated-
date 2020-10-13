#!/bin/sh

##
CWD=$(pwd)
echo "CWD: $CWD"

# build panda project of iAsset
cd ../devices/panda/basys.panda
mvn clean install
mvn verify
cd "$CWD"

