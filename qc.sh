#!/bin/sh
java -DstrictDataSourceChecking=false -jar dist/org.bridgedb.tools.qc.jar "$@"
