# iAsset-ClientDevices

This repo contains the collected implementations for the following client devices:

1) Franka Panda
2) Conveyor Belt
3) Peak2Pi
4) IPSAAdapter
5) ARTI Chasi

## Steps to make this project work in your Eclipse IDE

1) Import -> Existing Maven Project
2) Select all Projects listed
3) Select a JDK (min V8) in every projects Java Build Path
4) go to the root build directory and run the build files

## Steps to make this project work in your IntelliJ IDE

1) On the welcome screen with no projects open: "Import Project"
2) Select "Maven" and click next
3) Select the root directory, check "Search for projects recursively" and "Import Maven Projects automatically" and click next
4) Check all imported settings and Import
5) go to the root build directory and run the build files

## Build and Execute

In the build-directory there are scripts building and packaging all projects with their dependencies. 
The complete runnable jars can be found in the corresponding target-directory of the concrete device implementation (e.g.: .\devices\conveyorbelt\basys.conveyorbelt\target\).

Hint: The base project dependencies with all the properties can be found in the sub-directory: "dependency-jars".

Examples to build and run:

+ Run to build all: "./build/build_all.sh"
+ Run to build one (e.g. Belt): "./build/build_belt.sh"
+ Run to build base: "./build/build_base.sh"
