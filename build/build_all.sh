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

# build panda project of iAsset
cd ../devices/panda/basys.panda
mvn clean install
mvn verify
cd "$CWD"

# build belt project of iAsset
cd ../devices/conveyorbelt/basys.conveyorbelt
mvn clean install
mvn verify
cd "$CWD"

# build Peak2Pi project of iAsset
cd ../devices/peak2pi/basys.Peak2Pi
mvn clean install
mvn verify
cd "$CWD"

# build IPSA project of iAsset
cd ../devices/ipsa/basys.IPSA
mvn clean install
mvn verify
cd "$CWD"
