/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.bridgedb.metadata.constants.SchemaConstants;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.w3c.dom.Element;

/**
 *
 * @author Christian
 */
public abstract class MetaDataBase extends AppendBase implements MetaData{
    
    Set<Statement> rawRDF;
    Resource id;
    final String name;

   // MetaData parent;
    
    MetaDataBase(Element element){
        name = element.getAttribute(SchemaConstants.NAME);
    }
    
    MetaDataBase(String name){
        rawRDF = new HashSet<Statement>();
        this.name = name;
    }
    
    abstract void loadValues(Resource id, Set<Statement> data, MetaData parent);

    void setupValues(Resource id, MetaData parent){
        this.id = id;
 //       this.parent = parent;
    }
    
    abstract MetaDataBase getSchemaClone();
    
    abstract boolean hasValues();

    abstract Set<? extends LeafMetaData> getLeaves();

    abstract LeafMetaData getLeafByPredicate(URI predicate);

}
