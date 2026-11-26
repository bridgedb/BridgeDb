#!/bin/sh
#
# Run after first compiling with "mvn compile install".

VERSION=`tail -1 org.bridgedb/src/main/resources/version.props | cut -d'=' -f2`
VOIDJAR=org.bridgedb.tools.voidtool/target/org.bridgedb.tools.voidtool-${VERSION}-jar-with-dependencies.jar

java -DstrictDataSourceChecking=false -jar ${VOIDJAR} "$@"
