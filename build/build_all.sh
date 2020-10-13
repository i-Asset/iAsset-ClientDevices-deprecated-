#!/bin/sh

##
CWD=$(pwd)
echo "CWD: $CWD"

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
