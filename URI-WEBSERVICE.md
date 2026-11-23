The BridgeDb URI web service
============================

BridgeDb has two kinds of mapping services. The ID mapping service takes an
identifier with its data source, such as `Q47512` with system code `Wd`, and
returns identifiers in other data sources. The URI web service, in the module
`org.bridgedb.uri.ws.server`, takes a URI (or IRI) and returns a list of URIs
for the same thing. For example, it maps the Wikidata URI for acetic acid,
`http://www.wikidata.org/entity/Q47512`, to URIs in ChEBI, PubChem, HMDB, KEGG
and other databases.

This page explains two scripts in the root folder of this repository:

* `start-uri-server.sh` runs the URI web service. It uses two small Java
  programs next to it: `StartUriServer.java` starts the service, and
  `LoadUriExample.java` loads example data.
* `mapUri.groovy` uses the web service to map a URI to other URIs, by default
  the Wikidata URI for acetic acid.

Requirements
------------

* Java 21 or later, with `JAVA_HOME` pointing to it if `java` is an older
  version
* Maven, to build the modules
* Groovy, for `mapUri.groovy`
* The Debian packages `mariadb-server` (the database) and `jetty9` (the web
  server, which also installs the servlet API package `libservlet-api-java`):

  ```shell
  sudo apt install mariadb-server jetty9
  ```

* For the example data: a BridgeDb metabolite mapping database in Derby
  format (a `.bridge` file), such as `metabolites_20260208.bridge`

Step 1: set up the database (once)
----------------------------------

The web service reads its mappings from a MySQL or MariaDB database. Create the
default users and databases with the script in this repository:

```shell
sudo mariadb < mysqlConfig.sql
```

This creates the database `ims` for the web service and `imstest` for the
tests, with users of the same name and password. These defaults are in
`org.bridgedb.utils/src/main/resources/BridgeDb.properties`. To use other
values, put a `local.properties` file in the root folder of the repository,
where the scripts run. For example, for a database on another port:

```
SqlPort jdbc:mysql://localhost:3307
```

The README explains the other places where BridgeDb looks for these files.

Step 2: load the example data
-----------------------------

The web service only knows the mappings in its database. Load the mappings of
acetic acid from a BridgeDb metabolite database:

```shell
./start-uri-server.sh --load metabolites_20260208.bridge Q47512
```

**Warning:** this drops and recreates all tables in the `ims` database.

The first run builds the modules with Maven, which can take a minute. The
loader then reads the mappings of the Wikidata item `Q47512` from the `.bridge`
file. For each mapped identifier, it picks one URI, preferring
`http://identifiers.org/`, and stores a `skos:exactMatch` mapping in both
directions. For acetic acid, this gives 15 mappings, to data sources such as
CAS, ChEBI, ChEMBL and PubChem.

The mappings get the justification
`http://semanticscience.org/resource/SIO_001171` (database cross-reference),
which is part of the `Default` lens. The web service only returns mappings
whose justification is in the lens of the request, and without a lens it uses
`Default`.

Step 3: start the web service
-----------------------------

```shell
./start-uri-server.sh
```

The web service then runs at http://localhost:8183/ until you stop it with
Ctrl-C. The port and host are optional arguments:
`./start-uri-server.sh 8184 0.0.0.0` makes it reachable from other computers
on port 8184. Port 8080 is often taken, for example by the `jetty9` system
service that the Debian package starts.

To check that it works, ask for the mappings with `curl`:

```shell
curl -H 'Accept: application/xml' \
  'http://localhost:8183/map?Uri=http%3A%2F%2Fwww.wikidata.org%2Fentity%2FQ47512'
```

The answer is XML with one `<mapping>` element per mapping, 15 for acetic acid.
Each lists all known URIs for the mapped identifier in `<targetUri>` elements.

Step 4: map a URI with Groovy
-----------------------------

```shell
groovy mapUri.groovy
```

This asks the web service at http://localhost:8183 for the mappings of
`http://www.wikidata.org/entity/Q47512`, and prints the other URIs for acetic
acid, sorted:

```
http://www.wikidata.org/entity/Q47512 maps to 67 URIs:
  http://bio2rdf.org/chebi:15366
  http://bio2rdf.org/cpd:C00033
  ...
  http://identifiers.org/chebi/CHEBI:15366
  http://identifiers.org/hmdb/HMDB00042
  http://identifiers.org/pubchem.compound/176
  ...
  https://www.guidetopharmacology.org/GRAC/LigandDisplayForward?ligandId=1058
```

The 15 mappings give 67 URIs, as the web service returns every URI pattern it
knows for a data source. Mappings work in both directions, so
`groovy mapUri.groovy http://identifiers.org/chebi/CHEBI:15366` gives the
Wikidata URI back. Both arguments are optional: the URI to map, and the address
of the web service.

How the start script works
--------------------------

The module `org.bridgedb.uri.ws.server` was written to run as a war file in
Tomcat, with Spring. That set-up no longer builds: the war plugin is commented
out in its POM, and Spring is not among its dependencies. Debian's Tomcat 10
and 11 also need the `jakarta` APIs, while this code uses `javax`.

`start-uri-server.sh` therefore runs the web service without a war file:

1. It builds `org.bridgedb.uri.ws.server` and `org.bridgedb.rdb.derby` with
   Maven, and saves their runtime classpaths in `target/classpath.txt` in each
   module. It does this only once: run it with `REBUILD=yes` after changing
   the code.
2. It leaves out two kinds of jars that clash with Jetty 9 and CXF: the servlet
   API 2.5, and the Jersey 1 jars, which have their own copy of the JAX-RS
   1.1 classes.
3. It adds the Jetty 9 jars from `/usr/share/jetty9/lib` and the servlet API
   from `/usr/share/java/servlet-api-4.0.1.jar`.
4. It runs `StartUriServer.java` directly from source, without a separate
   compile step. This program starts Jetty with CXF's
   `CXFNonSpringJaxrsServlet`, which serves the class
   `org.bridgedb.ws.uri.WSUriServer`.

With `--load`, it runs `LoadUriExample.java` instead, with the Derby classes
added to the classpath.

Known limitations
-----------------

* Only XML works. JSON requests fail with HTTP 500, as there is no JSON
  provider for CXF among the dependencies.
* The HTML pages, such as http://localhost:8183/ and the HTML version of
  `/map`, fail with HTTP 500. `WebTemplates` configures Velocity's logging in
  the Velocity 1 way, which Velocity 2.3 rejects.
* The results for acetic acid include three URIs that are wrong, such as
  `http://identifiers.org/cl/CHEMBL539`. The system codes of ChEMBL compound
  (`Cl`) and the Cell Ontology (`cl`) differ only in case, and MariaDB compares
  text without regard to case by default. ChEMBL identifiers therefore also get
  Cell Ontology URIs.
* The Jetty 9 project no longer supports Jetty 9, but Debian 13 still provides
  security updates for its `jetty9` package. By default, the web service only
  listens on `localhost`.

These scripts were tested on Debian 13 with Java 21, MariaDB 11.8, Jetty
9.4.57 and Groovy 5.1.0, with the example data from
`metabolites_20260208.bridge`.
