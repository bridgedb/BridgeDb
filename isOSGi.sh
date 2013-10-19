#!/bin/sh
TMPDIR=tmposgi

mkdir ${TMPDIR}
cd ${TMPDIR}
jar xf ../$1 META-INF/MANIFEST.MF
grep -i bundle-symbolicname META-INF/MANIFEST.MF
cd ..
rm -rf ${TMPDIR}
