BridgeDb
========

[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.593034.svg)](https://doi.org/10.5281/zenodo.593034)
[![Actions Status](https://github.com/bridgedb/BridgeDb/workflows/build/badge.svg)](https://github.com/bridgedb/BridgeDb/actions)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.bridgedb/bridgedb-bundle/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.bridgedb/bridgedb-bundle)
[![codecov](https://codecov.io/gh/bridgedb/BridgeDb/branch/master/graph/badge.svg?token=F0kJdoEt0x)](https://codecov.io/gh/bridgedb/BridgeDb)
[![NWO](https://img.shields.io/badge/NWO%20Open%20Science-203.001.121-orange)](https://www.nwo.nl/en/projects/203001121)

BridgeDb is currently tested with Java 17 and higher (4.0.x releases).

Using BridgeDb dependencies
---------------------------

The [BridgeDb releases](https://github.com/bridgedb/BridgeDb/releases) are published to 
[Maven Central](https://search.maven.org/artifact/org.bridgedb/bridgedb-bundle), which means you can use the BridgeDb JARs without needing to compile BridgeDb.

Usage depends on which module you require. The examples below assumes artifact `org.bridgedb.bio` and version `4.0.0`:

For [Maven](https://maven.apache.org/):

```xml
<dependencies>
    <dependency>
        <groupId>org.bridgedb</groupId>
        <artifactId>org.bridgedb.bio</artifactId>
        <version>4.0.0</version>
    </dependency>
</dependencies>
```

For [Gradle](https://gradle.org/):

```gradle
compile group: 'org.bridgedb', name: 'org.bridgedb.bio', version: '4.0.0'
```

For [Ivy](http://ant.apache.org/ivy/):

```xml
<dependency org="org.bridgedb" name="org.bridgedb.bio" rev="4.0.0"/>
```

For [Buildr](https://buildr.apache.org/):

```buildr
'org.bridgedb:org.bridgedb.bio:jar:4.0.0'
```


Compilation
-----------

If you've obtained the source code of BridgeDb, you should be
able to compile with a simple:

```shell
mvn clean install -Dgpg.skip
```

You can find the libraries in the folder called "target", in each sublibrary folder (used to be called "dist" in ant).

If you want to ignore failing tests, e.g. because you are not online,
add this option: -Dmaven.test.failure.ignore=true. Furthermore,
note that 'mvn clean compile' fails.

Command line tools
------------------

The BridgeDb distribution comes with a few command line tools. You
can try:

```shell
bash info.sh <database.bridge>
bash qc.sh <database.bridge> <database2.bridge>
bash voidtool.sh <database.bridge> <fileName.void>
```

Checking for regressions
------------------------

You may want to run the following commands to detect regressions,
which is particularly important before starting to make a release:

```shell
mvn clean test
```

Note that the first call may fail a number of unit tests, like for those
of `IDMapperCapabilitiesTest`. For that one, this is because it is an
abstract class that should not actually be run by JUnit as test suite.

Note also that the second one may fail because BridgeDb module dependencies
may not be resolved yet.

Several tests also depend on a running MySQL backend or running BridgeDb
webservice. You can exclude these tests in Maven with:

```shell
mvn clean test -Djunit5.excludeGroups=webservice,mysql
```

The JUnit5 tagging also allows to specifically test the MySQL-backend tests,
for example with:

```shell
mvn clean test junit5.groups=mysql
```

Updating the datasources
------------------------

The `datasources.tsv` and `organisms.tsv` files were separated from this
repository, and this BridgeDb library is regularly updated for releases
from [https://github.com/bridgedb/datasources](https://github.com/bridgedb/datasources).

```shell
wget -O ./org.bridgedb.bio/src/main/resources/org/bridgedb/bio/datasources.tsv https://bridgedb.github.io/datasources/datasources.tsv
wget -O ./org.bridgedb.bio/src/main/resources/org/bridgedb/bio/datasources_headers.tsv https://bridgedb.github.io/datasources/datasources_headers.tsv
wget -O ./org.bridgedb.bio/src/main/resources/org/bridgedb/bio/organisms.tsv https://bridgedb.github.io/datasources/organisms.tsv
cp ./org.bridgedb.bio/src/main/resources/org/bridgedb/bio/*.tsv ./org.bridgedb.bio/resources/org/bridgedb/bio/.
```

The last line makes a copy for backwards compatibility.

Making releases
---------------

If it is time for a release, first, update `org.bridgedb/src/main/resources/version.props` (for BridgeDb),
this `README.md` and `CITATION.cff` (for Zenodo).

To make the release, run the following commands. Mind you, this requires you
to have an approved Sonatype (http://oss.sonatype.org/) account with push rights:

```shell
mvn versions:set -DnewVersion=4.0.0
mvn clean deploy
```

The second command will make the actual push. These commands will update the version
and everything.

To make a development (SNAPSHOT) release, use:

```shell
mvn versions:set -DnewVersion=3.1.1-SNAPSHOT
mvn clean deploy
```

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

Additional packages added for version 2 (mainly for URI support needed for OpenPHACTS)
org.bridgedb.utils - adds logging and some utils - Needed for all of modules below
org.bridgedb.sql - Alternative SQL database optimized for speed not size

org.bridgedb.ws.*  Another version of the Webservice. Runs with and IDMapper

org.bridgedb.rdf   Loads DataSources from rdf and miriam (with URI suppport)

org.bridgedb.uri.sql   Adds support for URIs (requires org.bridgedb.sql)
org.bridgedb.uri.loader  Loads RDF linksets into org.bridgedb.uri.sql. Also creates transitive linksets

org.bridgedb.uri.ws.*  Extends org.bridgedb.ws.* with URI supports from org.bridgedb.uri.sql 

Database structure
------------------

For further information about the database structure check [the documentation here](docs/database).

Contact
-------

* Website, wiki and bug tracker: http://www.bridgedb.org
* Mailing list: http://groups.google.com/group/bridgedb-discuss/
* Source code can be obtained from http://github.com/bridgedb/BridgeDb

Authors
-------

BridgeDb and related tools are developed by (alphabetic order):

 * Manas Awasthi
 * Christian Brenninkmeijer
 * Jianjiong Gao
 * Alasdair Gray
 * Isaac Ho
 * Martijn van Iersel
 * Alexander Pico
 * Stian Soiland-Reyes
 * Egon Willighagen
 * Martina Kutmon
 * Jonathan Mélius
 * Anders Riutta
 * Randy Kerber

The lead teams at this moment are (alphabetic order):

 * Gladstone Institutes
 * Maastricht University
 * The University of Manchester

License
-------

BridgeDb is free and open source. It is available under
the conditions of the [Apache License, version 2.0](https://www.apache.org/licenses/LICENSE-2.0). 
See LICENSE-2.0.txt for details.

Configuration For the URI/OpenPHACTS packages ONLY!
=============

Where are configuration files loaded from?
------------------------------------------
BridgeDb looks for the configuration files from the following locations with priority given to those at the top of the list (i.e. location 1 is a
higher priority than 2 etc). Once it finds a configuration file the other locations are ignored.  

1. Directly in the run directory  (Mainly for java *.jar runs)  
2. Environment Variable `BRIDGEDB_CONFIG` : can be used to point to any location  
3. Tomcat configuration folder : `$CATALINA_HOME/conf/BridgeDb`  
4. conf/BridgeDb : Allows tomcat 7 to pick up `$CATALINA_HOME/conf/BridgeDb` even if it can not get `$CATALINA_HOME`  
4. ../conf/BridgeDb : Allows tomcat 6 to pick up `$CATALINA_HOME/../conf/BridgeDb` even if it can not get `$CATALINA_HOME`  
5. Using classLoader getResource : This will pick up the files included in Jars and Wars.  


Configuration files
-------------------

* local.properties
* BridgeDB.properties
* log4j.properties  
* DataSource.ttl  
* lens.properties  
* graph.properties

### local.properties
(There is no local properties files included)

This is the recommended place to overwrite individual property values of any other *.properties file.

`local.properties` will overwrite values with the same key in any other properties file.
Properties not overwritten in local will keep their original values.

To install local properties you need to.

1. Create a local.properties file  
2. Store it in a location as described above  
3. Copy the keys from the original file  

### BridgeDB.properties
(Default file is included in build and can be found in org.bridgedb.utils/resources)

This file contains the local setup information which **MUST** be configured correctly for the service to run. It is essential that the
database user, password and database name are correct.
		
You **MUST** either supply local values matching your local setup or setup your data stores to use the defaults. 
The recommended way to overwrite properties is to add a property with the exact (case sensitive) key to local.properties

Database Dependency   
-------------------
(for the org.bridgedb.sql package and its dependencies ONLY)

* MySQL **MUST** be installed and running, otherwise it fails to start. Tested with MySQL up to version 5.5
* MySQL databases and users **MUST** be created with CREATE, DROP, INDEX, INSERT, ALTER,
UPDATE, DELETE, and SELECT permissions.

Consult the BridgeDB.properties file for the defaults, or copy and amend the configuration file
to reflect your own setup.

If you are using the default MySQL accounts and databases then execute the file 
mysqlConfig.sql from the BridgeDB root directory which will configure your local mysql with the BridgeDb defaults

```shell
mysql -u root -p < mysqlConfig.sql
```

Note that the SQL script will fail, without reverting changes made up to the 
point of failure, if any of the user accounts or databases already exist.

RDF Repository and Transitive Directory Dependency
-------------------------
(For org.bridgedb.rdf package and its extensions ONLY)

BridgeDb uses OpenRDF Sesame RDF engine and this is included automatically via Maven.  
**WARNING**: All directories **MUST** exists and the (linux) user running tomcat **MUST** have READ/WRITE permission set!
Some of the OpenRDF error message are unclear if this is not the case.

See BridgeDb.properties and change the appropriate property to point to the correct directory.
A Sesame SailNativeStore(s) will be created automatically as long as the loader can create/find the directory,

We recommend changing the relative directories to absolute directories.
Please ensure the parent directories exist and have the correct permissions. 

The settings for testing (and therefore compilation) can be left as is as long as the testing user would have permission to create and delete files there.

The BaseURI variable is no longer used but may be in the future so is worth setting correctly.

Other Configuration files
-------------------------
### log4j.properties
(Default file is included in build and can be found in org.bridgedb.utils/resources)

Edit this to change the logger setup.
The default can be found in the Utils Resource directory
Please refer to the log4j documentation for more information.

### DataSource.ttl 
(Included in the build and found at org.bridgedb.rdf/resources)

RDF format of all the BridgeDb DataSource(s) and Registered UriPatterns,
Found in $BRIDGEDB_HOME/org.bridgedb.rdf/resources

This file defines all the URI patterns that will match every BridgeDb DataSource.
Warning: As additional UriPatterns are constantly being found and created this file is subject to continuous updates. 
Having a local DataSource.ttl is therefore highly discouraged as it will block future updates being discovered.
Instead please push any changes into the version inside the source code. 
This file is **NOT** effected by local.properties and 
you cannot change existing or add additional datasource URI patterns through local.properties. 
If you require local additions that should not become general usage (such as commercial uriPatterns)
then the suggested approach is for you to change the code to use multiple dataSource files.

### lens.properties
(Included in the build and found at org.bridgedb.uri.sql\resources)

This file defines the lenses to be used in the system.
See [Scientific Lenses over Linked Data](http://ceur-ws.org/Vol-951/paper5.pdf)
for more information on what lenses are.

Can and should be added to using local.properties

**WARNING**: As the Lens work is still evolving it is subject to alterations and the format of this file could be changed at any time.
Having a local lens.properties is highly discouraged as it will block future updates being discovered.
Instead please push any changes into the version inside the source code.

Local additions that should not become general usage (such as commercial lens) can be added to the local.properties file.

Note: the fourth part of the key  
`lens.lenkey.justification.***`  
only serves to keep the keys unique and can have any value.
If extending a key we suggest using `local**` as the fourth part of the justification key to ensure not overwriting general additions.

### graph.properties
(Included in the build and found at org.bridgedb.uri.sql\resources)

This file maps RDF Graphs/Context with the UriPatterns found in that graph.
This allows Map functions to supply a graph name rather than a list of targetUriPatterns

Data in the included file is Open PHACTS specific.

Data Loading
============
All tests should load their required data at the start of the tests.
To load the test data into the live SQL use the method SetupWithTestData in the URI loader package.
The [IMS project](https://github.com/openphacts/IdentityMappingService) also has a data loader which should be used
if the IMS is the deployed project.

Compilation
===========
For URI/Open PHACTS packages

If you've obtained the source code of BridgeDb, you should be
able to compile with a simple: 

```shell
mvn clean install
```
	
Note that for the maven build to run all tests: 
1. The MySQL database **MUST** be running and configured as above.
2. (Optional) http://localhost:8080/OPS-IMS to be running the war created by the URI webserver Server module,
   with test data which can be loaded using the class SetupWithTestData in the URI Loader module.
   Maven will skip the client tests if the localhost server is not found.
	
OPS Webservice Setup.
--------------------

Make sure your local.properties file matches:
* The SQL databases included user names and password
* The RDF parent directories are setup (and accessible) as above.

or you have set up the default databases etc from BridgeDB.properties

Deploy $BridgeDb/org.bridgedb.uri.ws.service/target/org.bridgedb.uri.ws.server-*.war to something like your local
Tomcat webapps directory
To setup databases and add test data run org.bridgedb.uri.loader.SetupLoaderWithTestData. The easiest way is within eclipse since you
can set the OPS_IMS_CONFIG environment variable within the run configuration, Netbeans unfortunately does not allow environment variables to be set
within the IDE.
(Optional) Deploy $BridgeDb/org.bridgedb.ws.service/target/BridgeDb.war
   Both wars share the same SQL data.


Note: If Installing the Open PHACTS IMS and or the Open PHACTS QueryExpander the org.bridgedb.uri.ws.server-*.war should not be deployed but
instead the war appropriate to the other project should be deployed. See the readme within the
other projects for more details.

