Configuration
-------------
BridgeDB looks for the configuration file in the following locations. Once it 
finds a configuration file the other locations are ignored. 
* Environment Variable OPS-IMS-CONFIG: can be used to point to any location
* Tomcat configuration folder: $CATALINA_HOME/conf/OPS-IMS
* Class loader directory: The directory containing the jar files
* /conf/OPS-IMS subdirectory of Class loader directory 
     (For Tomcat this is TomcatXXX/conf/OPS-IMS)
* ../conf/OPS-IMS sister directory of Class loader directory (Mainly for junit tests)

The default configuration files can be found at
	$BRIDGEDB_HOME/conf/OPS-IMS
		
The default configuration files show the defaults that will be used if no 
configuration files are found. 

You must either edit the configuration files to match your local setup or setup
your data stores to use the defaults. If you edit the files to your configuration
we recommend copying the file to the tomcat configuration folder.

Database Dependency
-------------------
MySQL version 5 or above must be installed and running
MySQL databases and users must be created with CREATE, DROP, INDEX, INSERT, 
UPDATE, DELETE, and SELECT permissions.

Consult the sqlConfig file for the defaults, or copy and amend the configuration file
to your own setup.

If you are using the default accounts and databases then execute the file 
mysqlConfig.sql to create the accounts with appropriate permissions and the 
databases
	mysql -u root -p < mysqlConfig.sql
Note that the sql script will fail, without reverting changes made up to the 
point of failure, if any of the user accounts or databases already exist.


RDF Repository Dependency
-------------------------
SailNativeStore(s) will be created automatically as long as loader can 
create/find the directory, 

We recommend changing the relative directories to absolute directories.
Please ensure the parent directories exist and have the correct permissions. 

The settings for testing (and therefor compilation) can be left as is.

The BaseURI variable in the RDF configuration file should be the base of the 
Webserver you will drop the web service into.

-------------------------------------------------------------------------------

Compilation
-----------

If you've obtained the source code of BridgeDb, you should be
able to compile with a simple: (Ant build is broken in OPS branches)

	ant
	
or (experimental, makes not all jars) - 

	mvn clean install
	
Note that for the maven build to run all tests 
1) The MySQL database must be running and configured as above.
2) http://localhost:8080/OPS-IMS to be running and include the test data
3) http://localhost:8080/BridgeDb to be running and include the test data
  3 is optional as all Core client tests are repeated in OPS Client

OPS Webservice Setup.
--------------------

Make sure config files, SQL database and rdf parent directory are setup (and accessible) as above.

Deploy $BridgeDb/org.bridgedb.ops.ws.service/target/OPS-IMS.war to something like tomcat/webapps
To setup databases and add test data run org.bridgedb.linkset.SetupLoaderWithTestData
(Optional) Depoly $BridgeDb/org.bridgedb.ws.service/target/BridgeDb.war
   Both wars share the same SQL data.

OPS Load Linksets
-----------------
Make sure config files, SQL database and rdf parent directory are setup (and accessible) as above.

To load a linkset:
Run $BridgeDb\org.bridgedb.linksets\target\org.bridgedb.linksets-2.0.0-SNAPSHOT.one-jar.jar
   (Run without parameters for usage information)

(OPTIONAL) To create a transative linkset:
Run $BridgeDB\BridgeDb\org.bridgedb.transitive\target\org.bridgedb.transitive-2.0.0-SNAPSHOT.one-jar.jar

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
