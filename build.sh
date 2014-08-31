#!/bin/bash
mvn package assembly:single
echo "Deploying dataminingcrm jar to bin folder"
rm -rf bin
mkdir bin
cp target/dataminingcrm-jar-with-dependencies.jar bin/dataminingcrm.jar
cp config.properties bin/
