#!/bin/sh

##
CWD=$(pwd)
echo "CWD: $CWD"

# build IPSA project of iAsset
cd ../devices/ipsa/basys.IPSA
mvn clean package
mvn verify
cd "$CWD"

