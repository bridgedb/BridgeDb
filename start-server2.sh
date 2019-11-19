#!/bin/sh

# FIXME: versions of BridgeDb and MySQL-Connector must be updated

cd /home/wikipathways/code/bridgedb-3.0.0-SNAPSHOT/

CLASSPATH=\
org.bridgedb.server/lib/mysql-connector-java-8.0.18-bin.jar:\
dist/org.bridgedb.server.jar:\
dist/commons-cli-1.2.jar

MAINCLASS=org.bridgedb.server.Server

java -Xmx640M -cp $CLASSPATH $MAINCLASS "$@"
