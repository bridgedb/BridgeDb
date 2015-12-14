#!/bin/sh

cd /home/wikipathways/code/bridgedb-2.1.0-SNAPSHOT/

CLASSPATH=\
org.bridgedb.server/lib/mysql-connector-java-5.1.7-bin.jar:\
dist/org.bridgedb.server.jar:\
dist/commons-cli-1.2.jar

MAINCLASS=org.bridgedb.server.Server

java -Xmx640M -cp $CLASSPATH $MAINCLASS "$@"
