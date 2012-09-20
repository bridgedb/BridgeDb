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
    private boolean isParent = false;
    
    ResourceMetaData(Element element) throws MetaDataException {
        super(element);
        childMetaData.add(PropertyMetaData.getTypeProperty());
        String typeSt = element.getAttribute(SchemaConstants.TYPE);
        type = new URIImpl(typeSt);
    }
    
    /*public static ResourceMetaData getUntypedResource(Resource id){
        String name;
        if (id instanceof URI){
            name = ((URI)id).getLocalName();
        } else {
            name = id.stringValue();
        }
        return new ResourceMetaData(name);
    }*/
    
    private ResourceMetaData(ResourceMetaData other) {
        super(other);
        this.type = other.type;
    }
    
    /*private ResourceMetaData(String name){
        super(name, new ArrayList<MetaDataBase>());
        type = null;
    }*/
    
    @Override
    public void loadValues(Resource id, Set<Statement> data, MetaData parent) {
        super.loadValues(id, data, parent);
        Set<URI> predicates = getUsedPredicates(data);
        for (URI predicate:predicates){
            PropertyMetaData metaData = PropertyMetaData.getUnspecifiedProperty(predicate);
            metaData.loadValues(id, data, parent);
            childMetaData.add(metaData);
        }
        MetaDataRegistry.registerResource(this);
    }

    private Set<URI> getUsedPredicates(Set<Statement> data){
        Set<URI> results = new HashSet<URI>();
        for (Statement statement: data){
            if (statement.getSubject().equals(id)){
                results.add(statement.getPredicate());
            }
        }        
        return results;
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
        return new ResourceMetaData(this);
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
        if (isParent){
            if (this.hasCorrectTypes()){
                if (this.hasRequiredValues(forceLevel)){
                    //Do nothing
                } else {
                    if (includeWarnings) {
                        tab(builder, tabLevel);
                        builder.append("WARNING: ");
                        builder.append(id);
                        builder.append(" is incomplete so can only be used as a superset ");
                        newLine(builder);
                    }
                }
            } else {
                //Incorrect types show the whole vlaidity report anyway
                for (MetaDataBase child:childMetaData){
                    child.appendValidityReport(builder, forceLevel, includeWarnings, tabLevel);
                }    
            }
        } else {
            for (MetaDataBase child:childMetaData){
                child.appendValidityReport(builder, forceLevel, includeWarnings, tabLevel);
            }
        }
    }
    
    void addParent(ResourceMetaData parent) {
        parents.add(parent);
        Set<LeafMetaData> leaves = getLeaves();
        for (LeafMetaData leaf: leaves){
            URI predicate = leaf.getPredicate();
            LeafMetaData parentLeaf = parent.getLeafByPredicate(predicate);
            leaf.addParent(parentLeaf);
        }
        parent.isParent = true;
    }

    boolean isSuperset() {
        return isParent;
    }

    public void appendSummary(StringBuilder builder, int tabLevel) {
        tab(builder, tabLevel);
        builder.append(name);
        builder.append(" id ");
        builder.append(id);
        if (this.hasCorrectTypes()){
            if (this.hasRequiredValues(RequirementLevel.SHOULD)){
                builder.append(" OK!");
            } else if (this.hasRequiredValues(RequirementLevel.MUST)){
                builder.append(" has MUST values.");
            } else if (isParent){
                builder.append(" can only be used as a superset.");
            } else {
                builder.append(" incomplete!");
            }
        } else {
            builder.append(" has incorrect typed statements.");
        }
        newLine(builder);        
    }
}
