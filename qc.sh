#!/bin/sh
#
# Run after first compiling with "mvn compile install" and updating the version
# number below.

VERSION=2.2.3-SNAPSHOT
CLASSPATH=org.bridgedb.tools.qc/target/org.bridgedb.tools.qc-${VERSION}.jar:\
org.bridgedb/target/org.bridgedb-${VERSION}.jar:\
org.bridgedb.bio/target/org.bridgedb.bio-${VERSION}.jar:\
org.bridgedb.rdb/target/org.bridgedb.rdb-${VERSION}.jar:\
org.bridgedb.rdb.construct/target/org.bridgedb.rdb.construct-${VERSION}.jar:\
${HOME}/.m2/repository/org/apache/derby/derby/10.5.3.0_1/derby-10.5.3.0_1.jar:\
${HOME}/.m2/repository/com/google/collections/google-collections/1.0/google-collections-1.0.jar

java -DstrictDataSourceChecking=false -cp ${CLASSPATH} \
  org.bridgedb.tools.qc.BridgeQC "$@"
