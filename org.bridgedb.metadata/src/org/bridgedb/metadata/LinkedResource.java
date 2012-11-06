/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.bridgedb.metadata.constants.SchemaConstants;
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
    private MetaDataCollection collection;
    private final MetaDataSpecification registry;
    private final int cardinality;    
    
    LinkedResource(URI predicate, String type, RequirementLevel requirementLevel, URI resourceType, 
            MetaDataSpecification registry){
        this(predicate, type, NO_CARDINALITY, requirementLevel, resourceType, registry);
    }
    
    LinkedResource(URI predicate, String type, int cardinality, RequirementLevel requirementLevel, URI resourceType, 
            MetaDataSpecification registry){
        super(predicate.getLocalName(), type, requirementLevel);
        this.predicate = predicate;
        this.resourceType = resourceType;
        this.registry = registry;
        this.cardinality = cardinality;
    }

    //LinkedResource(Element element) throws MetaDataException {
    //    super(element);
    //    String typeSt = element.getAttribute(SchemaConstants.TYPE);
    //    resourceType = new URIImpl(typeSt);
    //    String predicateSt = element.getAttribute(SchemaConstants.PREDICATE);
    //    predicate = new URIImpl(predicateSt);
    //    String requirementLevelSt = element.getAttribute(SchemaConstants.REQUIREMENT_LEVEL);
    //}
 
    LinkedResource(LinkedResource other){
        super(other);
        predicate = other.predicate;
        resourceType = other.resourceType;
        this.collection = other.collection;
        this.registry = other.registry;
        this.cardinality = other.cardinality;
    }

    @Override
    void loadValues(Resource id, Set<Statement> data, MetaDataCollection collection) {
        setupValues(id);
        this.collection = collection;
        for (Iterator<Statement> iterator = data.iterator(); iterator.hasNext();) {
            Statement statement = iterator.next();
            if (statement.getSubject().equals(id) && statement.getPredicate().equals(predicate)){
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
            ResourceMetaData resource = registry.getResourceByType(resourceType);
            tab(builder, tabLevel);
            builder.append("Resource Link ");
            builder.append(name);
            newLine(builder, tabLevel + 1);
            builder.append("predicate ");
            builder.append(predicate);        
            newLine(builder);
            if (this.cardinality != NO_CARDINALITY){
                newLine(builder, tabLevel + 1);
                builder.append("Cardinality ");
                builder.append(cardinality);        
            }
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
    void appendShowAll(StringBuilder builder, int tabLevel) {
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
                ResourceMetaData rmd = collection.getResourceByID(id);
                if (rmd == null){
                    resourceNotFound(builder, id, tabLevel + 1);
                } else {
                    rmd.appendShowAll(builder, tabLevel + 1);
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
                ResourceMetaData rmd = collection.getResourceByID(id);
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
    public boolean hasRequiredValues() {
        if (requirementLevel != RequirementLevel.MUST){
            return true;
        } else if (this.cardinality != NO_CARDINALITY && this.cardinality != ids.size()) {
            return false;
        } else if (ids.isEmpty() && this.cardinality != 0){
            return false;
        } else {
            for (Resource id: ids){
                ResourceMetaData rmd = collection.getResourceByID(id);
                if (rmd == null){
                    return false;
                } else {
                    if (!rmd.hasRequiredValues()){
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
            ResourceMetaData rmd = collection.getResourceByID(id);
            if (rmd == null){
                //However if there is an id not found it is false;
                return false;
            } else {
                if (!rmd.hasRequiredValues()){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    void appendValidityReport(StringBuilder builder, boolean checkAllpresent, boolean includeWarnings, int tabLevel) {
        if (this.cardinality > 0 && ids.isEmpty()){
            tab(builder, tabLevel);
            builder.append("ERROR: ");
            builder.append(name);
            builder.append(" is missing. ");
            newLine(builder, tabLevel + 1);
            builder.append("Please add a statment with the predicate ");
            builder.append(predicate);
            newLine(builder);
        } else if (this.cardinality != NO_CARDINALITY && this.cardinality != ids.size()){
            tab(builder, tabLevel);
            builder.append("ERROR: ");
            builder.append(name);
            builder.append(" found incorrrect number of times. ");
            newLine(builder, tabLevel + 1);
            builder.append("Found ");
            builder.append(ids.size());
            builder.append(" times but expected  ");
            builder.append(this.cardinality);
            builder.append(" times.");
            newLine(builder, tabLevel + 1);
            if (this.cardinality > ids.size()) {
                builder.append("Please add statment(s) with the predicate ");
            } else {
                builder.append("Please remove statment(s) with the predicate ");                
            } 
            builder.append(predicate);
            newLine(builder);
        } else {
            for (Resource id: ids){
                ResourceMetaData rmd = collection.getResourceByID(id);
                if (rmd == null){
                    //If it is declared but missing that is an error
                    tab(builder, tabLevel);
                    builder.append("ERROR: Resource ");
                    builder.append(id);
                    builder.append(" is missing. ");
                    newLine(builder);
                } else {
                    String linkReport = rmd.validityReport(includeWarnings);
                    if (linkReport.contains("ERROR")) {
                        tab(builder, tabLevel);
                        builder.append("ERROR: ");
                        builder.append(name);
                        if (!rmd.hasCorrectTypes()){ 
                            if (!rmd.hasRequiredValues()) {
                                builder.append(" has missing values and incorrect types. ");
                            } else {
                                builder.append(" has incorrect types. ");
                            }
                        } else {
                            builder.append(" has missing values. ");
                        }
                        newLine(builder, tabLevel + 1);
                        builder.append("Please check id pointed to by predicate ");
                        builder.append(predicate);
                        newLine(builder, tabLevel + 1);
                        rmd.appendSpecific(builder, tabLevel+2);
                        newLine(builder);
                    }
                }
            }
        }
    }
    
    @Override
    public boolean allStatementsUsed() {
        for (Resource id: ids){
            ResourceMetaData rmd = collection.getResourceByID(id);
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
            ResourceMetaData rmd = collection.getResourceByID(id);
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
    public Set<ResourceMetaData> getResoucresByPredicate(URI predicate){
        HashSet<ResourceMetaData> results = new HashSet<ResourceMetaData>();
        for (Resource id: ids){
            if (predicate.equals(this.predicate)){
                results.add(collection.getResourceByID(id));
            }
        }
        return results;
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
        //Do nothing I think;
    }

    @Override
    public Set<Statement> getRDF() {
        return rawRDF;
    }


}