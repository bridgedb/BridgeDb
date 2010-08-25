#!/bin/sh
kill -9 `ps -ef|grep bridgedb|awk '{print $2}'`
sleep 5
java -cp dist/org.bridgedb.server.jar:dist/org.bridgedb.jar org.bridgedb.server.Server "$@"
