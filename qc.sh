#!/bin/sh
#
# Run after first compiling with "mvn compile install" and updating the version
# number below.

VERSION=`tail -1 org.bridgedb/version.props | cut -d'=' -f2`
QCJAR=org.bridgedb.tools.qc/target/org.bridgedb.tools.qc-${VERSION}-jar-with-dependencies.jar

java -DstrictDataSourceChecking=false -jar ${QCJAR} "$@"
