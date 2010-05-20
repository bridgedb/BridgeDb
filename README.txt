Compilation
-----------

If you've obtained the source code of BridgeDb, you should be
able to compile with a simple:

	ant
	
or (experimental, makes not all jars) - 

	mvn clean install

Library dependencies
--------------------

If you don't use all mappers, you do not need to include all
libraries in the dist directory in your project.

Here is a brief overview that will help you to find out
which ones you need. For questions, you can always contact our mailing list.

bridgedb.jar - always needed. 
    This includes file, rdb, biomart and BridgeRest drivers.
bridgedb-bio.jar - includes the BioDataSource enum, often needed
bridgedb-cronos.jar - needed for CRONOS webservice
bridgedb-synergizer.jar - needed for Synergizer webservice
bridgedb-picr.jar - needed for PICR webservice
bridgedb-webservice.jar - the BridgeRest SERVER, not needed if you only 
	want to access BridgeRest or BridgeWebservice as client
bridgedb-batchmapper.jar - Contains the batchmapper command line tool

bridgedb.jar and bridgedb-bio.jar do not need any other jar files to work.
All the other jar files in dist/ are SOAP libraries needed only for
some of the webservices. Look in the lib directory and build.xml of the 
respective mappers to find clues which libraries are needed by which service.

Contact
-------

Website, wiki and bug tracker: http://www.bridgedb.org
Mailing list: http://groups.google.com/group/bridgedb-discuss/
Source code can be obtained from http://svn.bigcat.unimaas.nl/bridgedb

Authors
-------

BridgeDb and related tools are developed by

Jianjiong Gao
Isaac Ho
Martijn van Iersel
Alex Pico

License
-------

BridgeDb is free and open source. It is available under
the conditions of the Apache 2.0 License. 
See License-2.0.txt for details.
