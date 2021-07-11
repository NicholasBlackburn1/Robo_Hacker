#!/bin/bash

echo "builing Blackburn Hacked Client"
./gradlew build
echo "Removing old jar from minecraft launcher local"
rm /home/nicky/.minecraft/versions/BlackburnHacked/BlackburnHacked.jar
echo "Copying jar file"
cp -r /home/nicky/Robo_Hacker/build/libs/mcp-reborn-1.0.0.jar ~/.minecraft/versions/BlackburnHacked/BlackburnHacked.jar
echo "CopyCompleate"
rm /home/nicky/Documents/Mcp1.16.5/MCP-Reborn-1.16/build/libs/mcp-reborn-1.0.0.jar
echo "removed old jar"