/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.bridgedb.metadata.constants.SchemaConstants;
import org.bridgedb.metadata.utils.Reporter;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.w3c.dom.Element;

/**
 *
 * @author Christian
 */
public class LinkedResource extends MetaDataBase implements MetaData, LeafMetaData{

    private final URI predicate;
    private final URI resourceType;
    private final Set<Resource> ids = new HashSet<Resource>();
    private final Set<Statement> rawRDF = new HashSet<Statement>();

    private final RequirementLevel requirementLevel;

    LinkedResource(Element element) throws MetaDataException {
        super(element);
        String typeSt = element.getAttribute(SchemaConstants.TYPE);
        resourceType = new URIImpl(typeSt);
        String predicateSt = element.getAttribute(SchemaConstants.PREDICATE);
        predicate = new URIImpl(predicateSt);
        String requirementLevelSt = element.getAttribute(SchemaConstants.REQUIREMENT_LEVEL);
        requirementLevel = RequirementLevel.parse(requirementLevelSt);
    }
 
    LinkedResource(LinkedResource other){
        super(other.name);
        predicate = other.predicate;
        resourceType = other.resourceType;
        requirementLevel = other.requirementLevel;
    }

    @Override
    void loadValues(Resource id, Set<Statement> data, MetaData parent) {
        setupValues(id, parent);
        for (Iterator<Statement> iterator = data.iterator(); iterator.hasNext();) {
            Statement statement = iterator.next();
            if (statement.getPredicate().equals(predicate)){
                Value value = statement.getObject();
                if (value instanceof Resource){
                    iterator.remove();
                    rawRDF.add(statement);
                    ids.add((Resource)value);
                }
            }
        }  
    }

    @Override
    LinkedResource getSchemaClone() {
        return new LinkedResource(this);
    }

    @Override
    void appendSchema(StringBuilder builder, int tabLevel) {
        try {
            ResourceMetaData resource = MetaDataRegistry.getResourceByType(resourceType);
            tab(builder, tabLevel);
            builder.append("Resource Link ");
            builder.append(name);
            newLine(builder, tabLevel + 1);
            builder.append("predicate ");
            builder.append(predicate);        
            newLine(builder, tabLevel + 1);
            builder.append("Requirement Level ");
            builder.append(requirementLevel);        
            newLine(builder);
            resource.appendSchema(builder, tabLevel + 1);
        } catch (MetaDataException ex) {
            appendException(builder, tabLevel, ex);
        }
    }

    private void appendException(StringBuilder builder, int tabLevel, MetaDataException ex){
            tab(builder, tabLevel);
            builder.append("ERROR!! Linked Resource ");
            builder.append(name);
            builder.append(" Not in MetaData! ");
            newLine(builder, tabLevel + 1);
            builder.append(ex);
            newLine(builder, tabLevel + 1);
            builder.append("Please check there is a reource declared with the type ");
            builder.append(resourceType);        
            newLine(builder);        
    }
    
    @Override
    void appendShowAll(StringBuilder builder, RequirementLevel requirementLevel, int tabLevel) {
        tab(builder, tabLevel);
        builder.append("Resource Link ");
        builder.append(name);
        newLine(builder, tabLevel + 1);
        builder.append("predicate ");
        builder.append(predicate);        
        newLine(builder);
        if (ids.isEmpty()){
            builder.append(" not set ");
        } else {
            for (Resource id: ids){
                ResourceMetaData rmd = MetaDataRegistry.getResourceByID(id);
                if (rmd == null){
                    resourceNotFound(builder, id, tabLevel + 1);
                } else {
                    rmd.appendShowAll(builder, requirementLevel, tabLevel + 1);
                }
            }
        }
        tab(builder, tabLevel);
        builder.append("Resource Link Done");        
        newLine(builder);
    }

    private void resourceNotFound(StringBuilder builder, Resource id, int tabLevel) {
        tab(builder, tabLevel);
        builder.append("No Resource found with id ");
        builder.append(id);
        newLine(builder);
    }
    
    @Override
    boolean hasValues() {
        if (ids.isEmpty()){
            return false;
        } else {
            for (Resource id: ids){
                ResourceMetaData rmd = MetaDataRegistry.getResourceByID(id);
                if (rmd == null){
                    return false;
                } else {
                    if (!rmd.hasValues()){
                        return false;
                    }
                }
            }
            return true;
        }
    }

    @Override
    public boolean hasRequiredValues(RequirementLevel forceLevel) {
        if (requirementLevel.compareTo(forceLevel) > 0) {
            return true; //Not required
        }
        if (ids.isEmpty()){
            return false;
        } else {
            for (Resource id: ids){
                ResourceMetaData rmd = MetaDataRegistry.getResourceByID(id);
                if (rmd == null){
                    return false;
                } else {
                    if (!rmd.hasRequiredValues(requirementLevel)){
                        return false;
                    }
                }
            }
            return true;
        }
    }

    @Override
    public boolean hasCorrectTypes() {
        //If there are no links it is true;
        for (Resource id: ids){
            ResourceMetaData rmd = MetaDataRegistry.getResourceByID(id);
            if (rmd == null){
                //However if there is an id not found it is false;
                return false;
            } else {
                if (!rmd.hasRequiredValues(requirementLevel)){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    void appendValidityReport(StringBuilder builder, RequirementLevel forceLevel, boolean includeWarnings, int tabLevel) {
        if (ids.isEmpty()){
            if (requirementLevel.compareTo(forceLevel) <= 0) {
                tab(builder, tabLevel);
                builder.append("ERROR: ");
                builder.append(id );
                builder.append(":");
                builder.append(name);
                builder.append(" is missing. ");
                newLine(builder, tabLevel + 1);
                builder.append("Please add a statment with the predicate ");
                builder.append(predicate);
                newLine(builder);
            }
        } else {
            for (Resource id: ids){
                ResourceMetaData rmd = MetaDataRegistry.getResourceByID(id);
                if (rmd == null){
                    //If it is declared but missing that is an error
                    tab(builder, tabLevel);
                    builder.append("ERROR: Resource ");
                    builder.append(id);
                    builder.append(" is missing. ");
                } else {
                    if (requirementLevel.compareTo(forceLevel) <= 0) {
                        rmd.appendValidityReport(builder, forceLevel, includeWarnings, tabLevel + 1);
                    } else {
                        //Not actually required so set level high to only append correct type info
                        rmd.appendValidityReport(builder, RequirementLevel.TECHNICAL_MUST, includeWarnings, tabLevel + 1);                        
                    }
                }
            }
        }
    }
    
    @Override
    public boolean allStatementsUsed() {
        for (Resource id: ids){
            ResourceMetaData rmd = MetaDataRegistry.getResourceByID(id);
            if (rmd == null){
                //No Statements here so ok
                return true;
            } else {
                if (!rmd.allStatementsUsed()){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void appendUnusedStatements(StringBuilder builder) {
        for (Resource id: ids){
            ResourceMetaData rmd = MetaDataRegistry.getResourceByID(id);
            if (rmd == null){
                //No Statements here
            } else {
                rmd.appendUnusedStatements(builder);
            }
        }
    }

    @Override
    public Set<Value> getValuesByPredicate(URI predicate) {
        //We don't want Values from linked resources.
        return null;
    }

    @Override
    LinkedResource getLeafByPredicate(URI predicate) {
        if (this.predicate.equals( predicate)){
            return this;
        }
        return null;
    }

    @Override
    Set<? extends LeafMetaData> getLeaves() {
        HashSet<LinkedResource> results = new HashSet<LinkedResource>();
        results.add(this);
        return results;
    }

    @Override
    public URI getPredicate() {
        return predicate;
    }

    @Override
    public void addParent(LeafMetaData parentLeaf) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}