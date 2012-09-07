/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.util.ArrayList;
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
public class ResourceMetaData extends MetaDataBase implements MetaDataClass{

    private final String name;
    private final URI type;
    private final List<MetaDataClass> childMetaData;
    private Resource id;
    
    ResourceMetaData(Element element) throws MetaDataException {
        name = element.getAttribute(Schema.NAME);
        String typeSt = element.getAttribute(Schema.TYPE);
        type = new URIImpl(typeSt);
        childMetaData = MetaDataClassFactory.getChildMetaData(element);
    }

    private ResourceMetaData(String theName, URI theType, List<MetaDataClass> children) {
        this.name = theName;
        this.type = theType;
        childMetaData = children;
    }
    
 
    @Override
    public void appendToSchema(StringBuilder builder, int tabLevel) {
        tab(builder, tabLevel);
        builder.append("Resource ");
        builder.append(name);
        newLine(builder, tabLevel + 1);
        builder.append("rdf:type ");
        builder.append(type);
        newLine(builder);
        for (MetaDataClass child:childMetaData){
            child.appendToSchema(builder, tabLevel + 1);
        }
    }

    URI getType() {
        return type;
    }

    @Override
    public void loadValues(Resource id, Set<Statement> data) {
        this.id = id;
        for (MetaDataClass child:childMetaData){
            child.loadValues(id, data);
        }
    }

    public ResourceMetaData getSchemaClone() {
        List<MetaDataClass> children = new ArrayList<MetaDataClass>();
        for (MetaDataClass child:childMetaData){
            children.add(child.getSchemaClone());
        }
        return new ResourceMetaData(name, type, children);
    }

}
