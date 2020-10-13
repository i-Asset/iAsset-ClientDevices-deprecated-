#!/bin/sh

##
CWD=$(pwd)
echo "CWD: $CWD"

# build belt project of iAsset
cd ../devices/conveyorbelt/basys.conveyorbelt
mvn clean install
mvn verify
cd "$CWD"

