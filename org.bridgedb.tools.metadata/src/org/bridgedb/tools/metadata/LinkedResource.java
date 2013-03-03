// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009  BridgeDb developers
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  OpenPhacts
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.bridgedb.tools.metadata;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.bridgedb.utils.BridgeDBException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

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
            
    //LinkedResource(Element element) throws BridgeDBException {
    //    super(element);
    //    String typeSt = element.getAttribute(SchemaConstants.TYPE);
    //    resourceType = new URIImpl(typeSt);
    //    String predicateSt = element.getAttribute(SchemaConstants.PREDICATE);
    //    predicate = new URIImpl(predicateSt);
    //    String requirementLevelSt = element.getAttribute(SchemaConstants.REQUIREMENT_LEVEL);
    //}
 
    LinkedResource(Resource id, LinkedResource other, MetaDataCollection collection){
        super(id, other);
        setupValues(id);
        predicate = other.predicate;
        resourceType = other.resourceType;
        this.collection = collection;
        this.registry = other.registry;
        this.cardinality = other.cardinality;
    }

    
    @Override
    void loadValues(Set<Statement> data, Set<String> errors) {
        for (Iterator<Statement> iterator = data.iterator(); iterator.hasNext();) {
            Statement statement = iterator.next();
            if (statement.getSubject().equals(id) && statement.getPredicate().equals(predicate)){
                Value value = statement.getObject();
                if (value instanceof Resource){
                    iterator.remove();
                    rawRDF.add(statement);
                    Resource otherID = (Resource)value;
                    ids.add(otherID);
                    ResourceMetaData rmd = collection.getResourceByID(otherID);
                    if (rmd == null){
                        errors.add("Unable to find a resource for " + otherID );
                    } else {
                        rmd.setAsLinked();
                    }
                }
            }
        } 
     }

    @Override
    LinkedResource getSchemaClone(Resource id, MetaDataCollection collection) {
        return new LinkedResource(id, this, collection);
    }

    @Override
    void appendSchema(StringBuilder builder, int tabLevel) {
        try {
            ResourceMetaData resource = registry.getResourceByType(resourceType, id, collection);
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
        } catch (BridgeDBException ex) {
            appendException(builder, tabLevel, ex);
        }
    }

    private void appendException(StringBuilder builder, int tabLevel, BridgeDBException ex){
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
                    if (!resourceType.equals(rmd.getType())){
                        return false;
                    }
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
    void appendValidityReport(StringBuilder builder, boolean checkAllpresent, boolean includeWarnings, int tabLevel) throws BridgeDBException {
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
                    builder.append(" is missing or not correctly typed. ");
                    newLine(builder);
                } else if (!resourceType.equals(rmd.getType())){
                    tab(builder, tabLevel);
                    builder.append("ERROR: ");
                    builder.append(name);
                    builder.append(" ");
                    builder.append(id);
                    builder.append(" found with type ");
                    builder.append(rmd.getType());            
                    builder.append(" but the requirment is type ");
                    builder.append(resourceType);            
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
                        rmd.appendSpecific(builder, tabLevel+1);
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
    public void addParent(LeafMetaData parentLeaf, Set<String> errors) {
        if (parentLeaf != null){
            if (parentLeaf instanceof LinkedResource){
                LinkedResource parent = (LinkedResource) parentLeaf;
                rawRDF.addAll(parent.rawRDF);
                ids.addAll(parent.ids);
            } else {
                loadValues(parentLeaf.getRDF(), errors);
            }
        }
    }

    @Override
    public Set<Statement> getRDF() {
        return rawRDF;
    }


}