Compilation
-----------

If you've obtained the source code of BridgeDb, you should be
able to compile with a simple:

	ant
	
or (experimental; does not compile all jars) - 

	mvn clean install

(If you want to ignore failing tests, e.g. because you are not online,
add this option: -Dmaven.test.failure.ignore=true)

Library dependencies
--------------------

If you do not use all mappers, you do not need to include all
libraries in the dist directory in your project.

Here is a brief overview that will help you to find out
which ones you need. For questions, you can always contact our mailing list.

 * org.bridgedb.jar - always needed. This includes the tab-delimited file driver.
 * org.bridgedb.bio.jar - includes the BioDataSource enum, often needed
 * org.bridgedb.webservice.cronos.jar - needed for CRONOS webservice
 * org.bridgedb.webservice.synergizer.jar - needed for Synergizer webservice
 * org.bridgedb.webservice.picr.jar - needed for PICR webservice
 * org.bridgedb.server.jar - the BridgeRest SERVER, not needed if you only want to access BridgeRest or BridgeWebservice as client
 * org.bridgedb.tools.batchmapper.jar - Contains the batchmapper command line tool

org.bridgedb.jar and org.bridgedb.bio.jar do not need any other jar files to work.
Most of the other jar files in dist/ are part of the SOAP libraries needed only for
some of the webservices. Look in the lib directory and build.xml of the 
respective mappers to find clues which libraries are needed by which service.

Contact
-------

Website, wiki and bug tracker: http://www.bridgedb.org
Mailing list: http://groups.google.com/group/bridgedb-discuss/
Source code can be obtained from http://svn.bigcat.unimaas.nl/bridgedb

Authors
-------

BridgeDb and related tools are developed by (alphabetic order):

 * Christian Brenninkmeijer
 * Jianjiong Gao
 * Alasdair Gray
 * Isaac Ho
 * Martijn van Iersel
 * Alex Pico
 * Stian Soiland-Reyes
 * Egon Willighagen

The lead teams at this moment are (alphabetic order):

 * Gladstone Institutes
 * Maastricht University
 * Manchester University

License
-------

BridgeDb is free and open source. It is available under
the conditions of the Apache 2.0 License. 
See License-2.0.txt for details.
