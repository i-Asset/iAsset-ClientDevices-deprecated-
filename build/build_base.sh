#!/bin/sh

##
CWD=$(pwd)
echo "CWD: $CWD"

# build base pom directory 
#(this step is needed to create the base pom dir)
cd ../devices
mvn clean install
mvn verify
cd "$CWD"

# build base device project of iAsset
cd ../devices/BASE/basys.basedevice
mvn clean install
mvn verify
cd "$CWD"

