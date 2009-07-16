#!/bin/sh

ant clean
ant dist

LOG="$HOME/bridgedb_stats.log";
DATE=`date`;

VALUE=`stat -c"%s" bridgedb*.tar.gz`
echo "$DATE\tsize of bridgedb_xxx.tar.gz\t$VALUE\tbytes" >> $LOG

for i in lib/br*.jar; 
do 
	VALUE=`stat -c"%s" $i`;
	JARNAME=`basename $i`
	echo "$DATE\tsize::$JARNAME\t$VALUE\tbytes" >> $LOG
done

for i in . corelib bio batchmapper
do
	VALUE=`find $i -iname "*.java" -exec cat '{}' \; | wc -l`
	echo "$DATE\tLOC::$i\t$VALUE\tLOC" >> $LOG

	VALUE=`find $i -iname "*.java" | wc -l`
	echo "$DATE\tjava files::$i\t$VALUE\tfiles" >> $LOG

	VALUE=`find $i -iname "*.java" -exec grep TODO '{}' \; | wc -l`
	echo "$DATE\tnumber::TODO in $i\t$VALUE\ttasks" >> $LOG
done

VALUE=`cat corelib/warnings.txt | wc -l`
echo "$DATE\tnumber::checkstyle warnings in corelib\t$VALUE\twarnings" >> $LOG

