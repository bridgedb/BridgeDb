#!/bin/sh

# change to directory of this script
cd $(dirname $0)

java -jar dist/org.bridgedb.server.jar "$@"
