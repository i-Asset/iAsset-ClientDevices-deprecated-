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
cd ../devices/devices.basedevice
mvn clean install
mvn verify
cd "$CWD"

# build panda project of iAsset
cd ../devices/devices.panda
mvn clean package
mvn verify
cd "$CWD"

# build belt project of iAsset
cd ../devices/devices.conveyorbelt
mvn clean package
mvn verify
cd "$CWD"

# build Peak2Pi project of iAsset
cd ../devices/devices.Peak2Pi
mvn clean package
mvn verify
cd "$CWD"

# build IPSA project of iAsset
cd ../devices/devices.IPSA
mvn clean package
mvn verify
cd "$CWD"
