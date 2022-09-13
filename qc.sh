#!/bin/sh
#
# Run after first compiling with "mvn compile install" and updating the version
# number below.

VERSION=`tail -1 org.bridgedb/version.props | cut -d'=' -f2`
DERBY=10.15.2.0
CLASSPATH=org.bridgedb.tools.qc/target/org.bridgedb.tools.qc-${VERSION}.jar:\
org.bridgedb/target/org.bridgedb-${VERSION}.jar:\
org.bridgedb.bio/target/org.bridgedb.bio-${VERSION}.jar:\
org.bridgedb.rdb/target/org.bridgedb.rdb-${VERSION}.jar:\
org.bridgedb.rdb.construct/target/org.bridgedb.rdb.construct-${VERSION}.jar:\
${HOME}/.m2/repository/org/apache/derby/derby/${DERBY}/derby-${DERBY}.jar:\
${HOME}/.m2/repository/org/apache/derby/derbytools/${DERBY}/derbytools-${DERBY}.jar:\
${HOME}/.m2/repository/org/apache/derby/derbyshared/${DERBY}/derbyshared-${DERBY}.jar:\
${HOME}/.m2/repository/org/apache/derby/derbyclient/${DERBY}/derbyclient-${DERBY}.jar:\
${HOME}/.m2/repository/com/google/guava/guava/30.1.1-jre/guava-30.1.1-jre.jar

java -DstrictDataSourceChecking=false -cp ${CLASSPATH} \
  org.bridgedb.tools.qc.BridgeQC "$@"
