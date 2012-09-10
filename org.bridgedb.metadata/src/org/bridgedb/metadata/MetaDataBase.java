/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 *
 * @author Christian
 */
public abstract class MetaDataBase extends AppendBase implements MetaData{
    
    Set<Statement> rawRDF;
    Resource id;
    MetaData parent;
    
    MetaDataBase(){
        rawRDF = new HashSet<Statement>();
    }
    
    abstract void loadValues(Resource id, Set<Statement> data, MetaData parent);

    void setupValues(Resource id, MetaData parent){
        this.id = id;
        this.parent = parent;
    }
    
    abstract MetaDataBase getSchemaClone();

}
