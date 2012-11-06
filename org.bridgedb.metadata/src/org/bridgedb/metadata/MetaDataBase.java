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
import org.bridgedb.rdf.StatementReader;
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
    final String type;
    final String name;
    final String documentation;
    final RequirementLevel requirementLevel;
    static int NO_CARDINALITY = -1;

   // MetaData parent;
    
    //MetaDataBase(Element element){
    //    name = element.getAttribute(SchemaConstants.NAME);
    //    documentation = MetaDataRegistry.getDocumentationRoot() + element.getAttribute(SchemaConstants.DOCUMENTATION);
    //}
    
    MetaDataBase(String name, String type, RequirementLevel requirementLevel){
        this.name = name;
        this.type = type;
        this.requirementLevel = requirementLevel;
        this.documentation = MetaDataSpecification.getDocumentationRoot();
    }
    
    MetaDataBase(MetaDataBase other){
        this.name = other.name;
        this.type = other.type;
        this.requirementLevel = other.requirementLevel;
        this.documentation = other.documentation;
    }
    
    //abstract void loadValues(Resource id, Set<Statement> data, MetaData parent, MetaDataCollection collection);
    abstract void loadValues(Resource id, Set<Statement> data, MetaDataCollection collection);

    void setupValues(Resource id){
        this.id = id;       
    }
    
    final void addDocumentationLink(StringBuilder builder, int tabLevel) {
        //tab(builder, tabLevel + 1);
        //builder.append("See: ");
        //builder.append(documentation);
        //newLine(builder);
    }

    abstract MetaDataBase getSchemaClone();
    
    abstract boolean hasValues();

    abstract Set<? extends LeafMetaData> getLeaves();

    abstract LeafMetaData getLeafByPredicate(URI predicate);

}
