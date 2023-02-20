#!/bin/sh
#
# Run after first compiling with "mvn compile install" and updating the version
# number below.

VERSION=`tail -1 org.bridgedb/src/main/resources/version.props | cut -d'=' -f2`
CLASSPATH=org.bridgedb.tools.info/target/org.bridgedb.tools.info-${VERSION}.jar:\
org.bridgedb/target/org.bridgedb-${VERSION}.jar:\
org.bridgedb.bio/target/org.bridgedb.bio-${VERSION}.jar:\
org.bridgedb.rdb/target/org.bridgedb.rdb-${VERSION}.jar:\
${HOME}/.m2/repository/org/apache/derby/derby/10.5.3.0_1/derby-10.5.3.0_1.jar:\
${HOME}/.m2/repository/com/google/collections/google-collections/1.0/google-collections-1.0.jar

java -DstrictDataSourceChecking=false -cp ${CLASSPATH} \
  org.bridgedb.tools.info.BridgeInfo "$@"
