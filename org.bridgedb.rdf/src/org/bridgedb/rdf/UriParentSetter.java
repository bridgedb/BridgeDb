/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.bio.BioDataSource;

/**
 *
 * @author Christian
 */
public class UriParentSetter {
    
    public static void setParents() throws IDMapperException{
        addEbiOntologyLookup();
        addScienceSignaling();
    }

    private static void addEbiOntologyLookup() throws IDMapperException {
        DataSource Ds1 = DataSource.getExistingByFullName("Cell Cycle Ontology");
        DataSource Ds2 = DataSource.getExistingByFullName("Unit Ontology");
        DataSource parent = DataSource.register(null,"Ebi Ontology Lookup").
                urlPattern(Ds1.getUrl("$id")).
                asDataSource();
        DataSourceUris.setUriParent(parent, Ds1);
        DataSourceUris.setUriParent(parent, Ds2);
    }

    private static void addScienceSignaling() throws IDMapperException {
        DataSource Ds1 = DataSource.getExistingByFullName("Science Signaling Pathway-Independent Component");
        DataSource parent = DataSource.getByFullName("Science Signaling Pathway");
        DataSourceUris.setUriParent(parent, Ds1);
    }
    
    public static void main(String[] args) throws IDMapperException {
        BioDataSource.init();
        setParents();       
        for (DataSource dataSource:DataSource.getDataSources()){
            DataSourceUris dataSourceUris = DataSourceUris.byDataSource(dataSource);
        }
    }
}
