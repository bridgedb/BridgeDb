/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import org.bridgedb.metadata.constants.SchemaConstants;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bridgedb.metadata.constants.RdfConstants;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.w3c.dom.Element;

/**
 *
 * @author Christian
 */
public class ResourceMetaData extends HasChildrenMetaData implements MetaData{

    private final URI type;
    private final Set<ResourceMetaData> parents = new HashSet<ResourceMetaData>();
    
    ResourceMetaData(Element element) throws MetaDataException {
        super(element);
        String typeSt = element.getAttribute(SchemaConstants.TYPE);
        type = new URIImpl(typeSt);
    }

    private ResourceMetaData(String theName, URI theType, List<MetaDataBase> children) {
        super(theName, children);
        this.type = theType;
    }
    
    @Override
    public void loadValues(Resource id, Set<Statement> data, MetaData parent) {
        super.loadValues(id, data, parent);
        for (Iterator<Statement> iterator = data.iterator(); iterator.hasNext();) {
            Statement statement = iterator.next();
            if (statement.getSubject().equals(id)){
                iterator.remove();
                if (statement.getObject().equals(type) && statement.getPredicate().equals(RdfConstants.TYPE_URI)){
                    //Type statement can be rrecreated so is not needed 
                } else {
                    rawRDF.add(statement);
                }
            }
        }  
        MetaDataRegistry.registerResource(this);
    }

    void appendSpecific(StringBuilder builder, int tabLevel){
        tab(builder, tabLevel);
        builder.append("Resource ");
        builder.append(name);
        builder.append(" id ");
        builder.append(id);
        newLine(builder);        
    }
    @Override
    void appendShowAll(StringBuilder builder, RequirementLevel forceLevel, int tabLevel) {
        super.appendShowAll(builder, forceLevel, tabLevel);
        for (Statement statement: rawRDF){
            tab(builder, tabLevel + 1);
            builder.append(statement);
            newLine(builder);
        }
    }
 
    @Override
    void appendUnusedStatements(StringBuilder builder) {
         for (Statement statement: rawRDF){
            builder.append(statement);
            newLine(builder);
        }
    }

    @Override
    public void appendSchema(StringBuilder builder, int tabLevel) {
        tab(builder, tabLevel);
        builder.append("Resource ");
        builder.append(name);
        newLine(builder, tabLevel + 1);
        builder.append("rdf:type ");
        builder.append(type);
        newLine(builder);
        for (MetaDataBase child:childMetaData){
            child.appendSchema(builder, tabLevel + 1);
        }
    }

    URI getType() {
        return type;
    }

    public ResourceMetaData getSchemaClone() {
        List<MetaDataBase> children = new ArrayList<MetaDataBase>();
        for (MetaDataBase child:childMetaData){
            children.add(child.getSchemaClone());
        }
        return new ResourceMetaData(name, type, children);
    }

    @Override
    boolean hasValues() {
        //A Resource has values if any of the children have values.
        for (MetaDataBase child:childMetaData){
            if (child.hasValues()) { 
                return true; 
            }
        }
        return false;
    }

   @Override
    public void appendValidityReport(StringBuilder builder, RequirementLevel forceLevel, boolean includeWarnings, int tabLevel) {
        for (MetaDataBase child:childMetaData){
            child.appendValidityReport(builder, forceLevel, includeWarnings, tabLevel);
        }
    }
    
    @Override
    public boolean allStatementsUsed() {
        if (rawRDF.isEmpty()) {
            for (MetaDataBase child:childMetaData){
                if (!child.allStatementsUsed()){
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    void addParent(ResourceMetaData parent) {
        parents.add(parent);
    }

}
