#!/usr/bin/env bash

mvn exec:java -Dexec.args="--package-path=./src/main/resources/franka_msgs/ ./src/main/java/ franka_msgs" -Dexec.mainClass="org.ros.internal.message.GenerateInterfaces"