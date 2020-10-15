# iAsset-ClientDevices

This repository has iAsset-Basyx (master) as a submodule to build the required projects: basyx.sdk and basyx.components
It contains the collected implementations for the following client devices:

1) Franka Panda
2) Conveyor Belt
3) Peak2Pi
4) IPSAAdapter

Every implementation has its own main function with a corresponding "OtherDevice", which demonstrates how to connect to it


## Steps to make this project work in your Eclipse IDE:

1) Import -> Existing Maven Project
2) Select all Projects listed
3) Select a JDK (min V8) in every projects Java Build Path
3) mvn install
4) clean compile

Instead you may also go to the root build directory and run ./build_java.sh

## Dependencies

+ All client device implementation projects need basyx.sdk and basyx.components The basyx.components needs basyx.sdk..
+ All client device implementation projects require the basedevice-project. This project contains all reusable classes, helpers, etc..
