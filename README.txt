Configuration
-------------
BridgeDB looks for the configuration file in the following locations. Once it 
finds a configuration file the other locations are ignored. 
* Environment Variable OPS-IMS-CONFIG: can be used to point to any location
* Tomcat configuration folder: $CATALINA_HOME/conf/OPS-IMS
* Class loader directory: The directory containing the jar files

The default database configuration file can be found at
	org.bridgedb.sql/resources/sqlConfig.txt

The default triplestore (used for linkset metadata) configuration file can be
found at
	org.bridgedb.linksets/resources/rdfConfig.txt
	
The default configuration files show the defaults that will be used if no 
configuration files are found. 

You must either edit the configuration files to match your local setup or setup
your data stores to use the defaults. If you edit the files to your configuration
we recommend copying the file to the tomcat configuration folder.

Database Dependency
-------------------
MySQL version 5 or above must be installed and running
MySQL databases and users must be created with read, create and write permissions

Consult the sqlConfig file for the defaults, or amend the configuration file
to your own setup.


RDF Repository Dependency
-------------------------
SailNativeStore(s) will be created automatically as long as loader can 
create/find the directory

The BaseURI variable in the RDF configuration file should be the base of the 
Webserver you will drop the web service into.

-------------------------------------------------------------------------------

Compilation
-----------

If you've obtained the source code of BridgeDb, you should be
able to compile with a simple:

	ant
	
or (experimental, makes not all jars) - 

	mvn clean install
	
Note that the maven build will fail if either the MySQL database is not running
or the configuration in the sqlConfig file is not present.


Library dependencies
--------------------

If you don't use all mappers, you do not need to include all
libraries in the dist directory in your project.

Here is a brief overview that will help you to find out
which ones you need. For questions, you can always contact our mailing list.

org.bridgedb.jar - always needed. 
    This includes the tab-delimited file driver.
org.bridgedb.bio.jar - includes the BioDataSource enum, often needed
org.bridgedb.webservice.cronos.jar - needed for CRONOS webservice
org.bridgedb.webservice.synergizer.jar - needed for Synergizer webservice
org.bridgedb.webservice.picr.jar - needed for PICR webservice
org.bridgedb.server.jar - the BridgeRest SERVER, not needed if you only 
	want to access BridgeRest or BridgeWebservice as client
org.bridgedb.tools.batchmapper.jar - Contains the batchmapper command line tool

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

BridgeDb and related tools are developed by

Jianjiong Gao
Isaac Ho
Martijn van Iersel
Alex Pico

OpenPhacts BridgeDB Team:
Christian Brenninkmeijer
Alasdair Gray
Egon Willighagen

License
-------

BridgeDb is free and open source. It is available under
the conditions of the Apache 2.0 License. 
See License-2.0.txt for details.
