org.bridgedb.rdf
-----------------

Purpose:

1. Provide a tool for saving DataSource information in a RDF format.
2. Tool for creating `DataSource`s by reading the RDF format
3. Extra Support for `DataSource` to `UriPattern` mapping
    To replace hack in `ops.bridgebd.ops.sql` (to be renamed `ops.bridgedb.uri.sql`)
4. To be determined if Uri -> Xref -> Uri should be SQL query based or HashMap based
  * Dettermining factor is speed on a large machine

Similar to `ops.bridgedb.bio` with the plan to convert all data from `ops.bridgedb.bio` into 
`../org.bridgedb.utils/resources/BioDataSource.ttl` (?)

Including future updates to datasources.txt (until BioDataSource.ttl becomes default for all projects (if ever)) (?)

## Updating from identifiers.org


These files reflect http://identifiers.org/ registry:

* [resources/IdentifiersOrgDataSource.ttl](resources/IdentifiersOrgDataSource.ttl)
* [resources/IdentifiersOrgDataSource.txt](resources/IdentifiersOrgDataSource.txt)
* [resources/MiriamRegistry.ttl](resources/MiriamRegistry.ttl)

Run the `IdentifersOrgReader` to update the files:

    mvn clean install
    java -jar target/org.bridgedb.rdf-2.1.0-SNAPSHOT.one-jar.jar


The file `MiriamRegistry.ttl` will be updated with the
content of http://www.ebi.ac.uk/miriam/main/export/registry.ttl
and used to populate `IdentifiersOrgDataSource.*`.

It is currently unclear how the remaining files in [resources/](resources/)
are used or maintained (See [GitHub issue #21](https://github.com/bridgedb/BridgeDb/issues/21))
and the relevance to [datasources.txt](../org.bridgedb.bio/resources/org/bridgedb/bio/datasources.txt)
from `org.bridgedb.bio`.

