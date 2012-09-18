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
    
    Resource id;
    final String name;
    final String documentation;

   // MetaData parent;
    
    MetaDataBase(Element element){
        name = element.getAttribute(SchemaConstants.NAME);
        documentation = MetaDataRegistry.getDocumentationRoot() + element.getAttribute(SchemaConstants.DOCUMENTATION);
    }
    
    MetaDataBase(String name){
        this.name = name;
        this.documentation = MetaDataRegistry.getDocumentationRoot();
    }
    
    MetaDataBase(MetaDataBase other){
        this.name = other.name;
        this.documentation = other.documentation;
    }
    
    abstract void loadValues(Resource id, Set<Statement> data, MetaData parent);

    void setupValues(Resource id, MetaData parent){
        this.id = id;
 //       this.parent = parent;
    }
    
    final void addDocumentationLink(StringBuilder builder, int tabLevel) {
        tab(builder, tabLevel + 1);
        builder.append("See: ");
        builder.append(documentation);
        newLine(builder);
    }

    abstract MetaDataBase getSchemaClone();
    
    abstract boolean hasValues();

    abstract Set<? extends LeafMetaData> getLeaves();

    abstract LeafMetaData getLeafByPredicate(URI predicate);

}
