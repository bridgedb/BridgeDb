/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import org.bridgedb.metadata.constants.SchemaConstants;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.w3c.dom.Element;

/**
 *
 * @author Christian
 */
public class ResourceMetaData extends MetaDataBase implements MetaData{

    private final String name;
    private final URI type;
    private final List<MetaDataBase> childMetaData;

    ResourceMetaData(Element element) throws MetaDataException {
        name = element.getAttribute(SchemaConstants.NAME);
        String typeSt = element.getAttribute(SchemaConstants.TYPE);
        type = new URIImpl(typeSt);
        childMetaData = MetaDataRegistry.getChildMetaData(element);
    }

    private ResourceMetaData(String theName, URI theType, List<MetaDataBase> children) {
        this.name = theName;
        this.type = theType;
        childMetaData = children;
    }
    
    @Override
    public void loadValues(Resource id, Set<Statement> data, MetaData parent) {
        setupValues(id, parent);
        for (MetaDataBase child:childMetaData){
            child.loadValues(id, data, this);
        }
        for (Iterator<Statement> iterator = data.iterator(); iterator.hasNext();) {
            Statement statement = iterator.next();
            if (statement.getSubject().equals(id)){
                 iterator.remove();
                 rawRDF.add(statement);
            }
        }  
    }
 
    @Override
    public void appendToString(StringBuilder builder, int tabLevel) {
        tab(builder, tabLevel);
        builder.append("Resource ");
        builder.append(name);
        newLine(builder);
        for (MetaDataBase child:childMetaData){
            child.appendToString(builder, tabLevel + 1);
        }
        for (Statement statement: rawRDF){
            newLine(builder, tabLevel + 1);
            builder.append(statement);
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
    public boolean hasRequiredValues(RequirementLevel requirementLevel) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasCorrectTypes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String validityReport(StringBuilder builder, RequirementLevel forceLevel, boolean includeWarnings) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void appendValidityReport(StringBuilder builder, RequirementLevel forceLevel, boolean includeWarnings, int tabLevel) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
