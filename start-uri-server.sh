#!/bin/sh
# Starts the BridgeDb URI web service with the Jetty 9 from the Debian package
# jetty9, using StartUriServer.java. See URI-WEBSERVICE.md.
#
# Usage:
#   ./start-uri-server.sh [port] [host]
#       starts the web service, by default at http://localhost:8183/
#   ./start-uri-server.sh --load <file.bridge> <Wikidata ID>
#       loads the mappings of one Wikidata item from a BridgeDb Derby file,
#       after dropping and recreating all tables in the database
#
# Set REBUILD=yes to build the modules and their classpaths again.
set -eu
cd "$(dirname "$0")"

SERVER=org.bridgedb.uri.ws.server
DERBY=org.bridgedb.rdb.derby
JETTY_LIB=/usr/share/jetty9/lib
SERVLET_API=/usr/share/java/servlet-api-4.0.1.jar

for f in "$JETTY_LIB" "$SERVLET_API"; do
    if [ ! -e "$f" ]; then
        echo "Missing $f: install the Debian packages jetty9 and libservlet-api-java" >&2
        exit 1
    fi
done

# BridgeDb needs Java 21 or later: use JAVA_HOME if set
JAVA="${JAVA_HOME:+$JAVA_HOME/bin/}java"
JAVA_VERSION=$("$JAVA" -XshowSettings:properties -version 2>&1 \
    | sed -n 's/^ *java\.specification\.version = //p')
if [ "${JAVA_VERSION%%.*}" -lt 21 ]; then
    echo "$JAVA is Java $JAVA_VERSION, but Java 21 or later is needed: set JAVA_HOME" >&2
    exit 1
fi

if [ ! -f "$SERVER/target/classpath.txt" ] || [ ! -f "$DERBY/target/classpath.txt" ] \
        || [ "${REBUILD:-no}" = yes ]; then
    mvn -q -B -pl "$SERVER,$DERBY" -am -DskipTests -Dmaven.javadoc.skip=true \
        package dependency:build-classpath \
        -Dmdep.outputFile=target/classpath.txt -Dmdep.includeScope=runtime
fi

# Leave out jars that clash with Jetty 9 and CXF:
# - servlet-api 2.5, as Jetty 9 needs servlet API 3.1 or later
# - Jersey 1, as jersey-core has its own javax.ws.rs classes (JAX-RS 1.1)
filter() {
    tr ':' '\n' < "$1" | grep -vE '/(servlet-api-2\.5|jersey-[a-z]+-1\.[0-9.]+)\.jar$' | paste -sd: -
}
CP="$SERVER/target/classes:$(filter "$SERVER/target/classpath.txt")"

if [ "${1:-}" = --load ]; then
    if [ $# -ne 3 ]; then
        echo "Usage: $0 --load <file.bridge> <Wikidata ID>" >&2
        exit 1
    fi
    CP="$CP:$DERBY/target/classes:$(filter "$DERBY/target/classpath.txt")"
    exec "$JAVA" -cp "$CP" LoadUriExample.java "$2" "$3"
fi

for jar in jetty-server jetty-servlet jetty-http jetty-io jetty-util jetty-security; do
    CP="$CP:$(ls "$JETTY_LIB"/$jar-9.*.jar)"
done
CP="$CP:$SERVLET_API"

exec "$JAVA" -cp "$CP" StartUriServer.java "$@"
