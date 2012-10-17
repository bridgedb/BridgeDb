/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.bridgedb.metadata.constants.RdfConstants;
import org.bridgedb.metadata.constants.VoidConstants;
import org.bridgedb.rdf.StatementReader;
import org.bridgedb.utils.Reporter;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 *
 * @author Christian
 */
public class MetaDataCollection extends AppendBase implements MetaData {
    
    Map<Resource,ResourceMetaData> resourcesMap = new HashMap<Resource,ResourceMetaData>();
    Set<String> errors = new HashSet<String>();
    Set<Statement> unusedStatements = new HashSet<Statement>();
    MetaDataSpecification metaDataRegistry;
    
    public MetaDataCollection(Set<Statement> incomingStatements, MetaDataSpecification specification) throws MetaDataException {
        Set<Statement> statements = new HashSet(incomingStatements);
        this.metaDataRegistry = specification;
        Set<Statement> subsetStatements = extractStatementsByPredicate(VoidConstants.SUBSET, statements);
        Set<Resource> ids = findIds(statements);
        for (Resource id:ids){
            if (!resourcesMap.containsKey(id)){
               ResourceMetaData resourceMetaData =  getResourceMetaData(id, statements);
               if (resourceMetaData == null){
                   Reporter.report(id + " has no known rdf:type ");
                   errors.add(id + " has no known rdf:type ");                   
               } else {
                   resourcesMap.put(id, resourceMetaData);
               }
            }
        }
        addSubsets(subsetStatements);
        checkAllRemainingAreLinks(statements);
    }
    
    public MetaDataCollection (String dataFileName, MetaDataSpecification metaDataRegistry) throws MetaDataException{
        this(StatementReader.extractStatements(dataFileName), metaDataRegistry);
    }
        
    private ResourceMetaData getResourceMetaData (Resource id, Set<Statement> statements) throws MetaDataException{
        Set<Value> types = findBySubjectPredicate(id, RdfConstants.TYPE_URI, statements);
        ResourceMetaData resourceMetaData = null;
        for (Value type:types){
            ResourceMetaData rmd =  metaDataRegistry.getResourceByType(type);
            if (rmd != null){
                if (resourceMetaData == null){
                   resourceMetaData = rmd; 
                   resourceMetaData.loadValues(id, statements, this);
                } else {
                   errors.add(id + " has a second known rdf:type " + type);
                }
            }
        } 
        return resourceMetaData;
    }
    
    private Set<Resource> findIds(Set<Statement> statements){
        HashSet<Resource> results = new HashSet<Resource>();
        for (Statement statement: statements) {
            if (statement.getPredicate().equals(RdfConstants.TYPE_URI)){
                 results.add(statement.getSubject());
            }
            if (statement.getPredicate().equals(VoidConstants.SUBSET)){
                 results.add(statement.getSubject());
                 Value object = statement.getObject();
                 if (object instanceof Resource){
                     results.add((Resource)object);
                     errors.add(VoidConstants.SUBSET + " has unexpected non resource object " + object);
                 }
            }
        }  
        return results;
    }
    
    private Set<Statement> extractStatementsByPredicate(URI predicate, Set<Statement> statements){
        HashSet<Statement> results = new HashSet<Statement>();
        for (Iterator<Statement> iterator = statements.iterator(); iterator.hasNext();) {
            Statement statement = iterator.next();
            if (statement.getPredicate().equals(predicate)){
                iterator.remove();
                results.add(statement);
            }
        }  
        return results;
    }

    private Set<Value> findBySubjectPredicate(Resource subject, URI predicate, Set<Statement> statements){
        HashSet<Value> values = new HashSet<Value>();         
        for (Statement statement: statements){
            if (statement.getSubject().equals(subject)){
                if (statement.getPredicate().equals(predicate)){
                    values.add(statement.getObject());
                }
            }
        }  
        return values;
    }

    private void addSubsets(Set<Statement> subsetStatements) {
        for (Statement statement: subsetStatements){
            ResourceMetaData parent = resourcesMap.get(statement.getSubject());
            if (parent == null){
                errors.add("No resource found for " + statement.getSubject() + " unable to find parent");                
            } 
            Value object = statement.getObject();
            if (object instanceof Resource){
                ResourceMetaData child = resourcesMap.get((Resource)object);
                if (child == null){
                    errors.add("No resource found for " + object + " unable to find child");
                } else {
                    if (parent != null){
                        child.addParent(parent);                           
                    }
                }
            } else {
                errors.add("Unexpected Object in subset statement " + object);                
            }
        }
    }

    private void checkAllRemainingAreLinks(Set<Statement> statements) {
        Set<Value> linkPredicates = getValuesByPredicate(VoidConstants.LINK_PREDICATE);
        for (Statement statement:statements){
            if (linkPredicates == null || !linkPredicates.contains(statement.getPredicate())){
                unusedStatements.add(statement);     
            }
        }
    }


    // ** Retreival Methods
    public Set<Resource> getIds(){
        return resourcesMap.keySet();
    }
    
    public Set<Resource> getIdsOfType(URI type){
        Set<Resource> result = new HashSet<Resource>();
        Set<Resource>  keys =  resourcesMap.keySet();
        for (Resource key: keys){
            ResourceMetaData resourceMetaData = resourcesMap.get(key);
            if (resourceMetaData.getType().equals(type)){
                result.add(key);
            }
        }
        return result;
    }

    public String summary() {
        StringBuilder builder = new StringBuilder();
        for (ResourceMetaData resource:resourcesMap.values()){
            resource.appendSummary(builder, 0);
        }
        return builder.toString();
    }

    // ** MetaData Methods 
    @Override
    public boolean hasRequiredValues() {
        for (ResourceMetaData resouce:resourcesMap.values()){
            if (!resouce.hasRequiredValues()){
                return false;
            }
        }
        return true;
    }

    public boolean hasRequiredValuesOrIsSuperset() {
        for (ResourceMetaData resource:resourcesMap.values()){
            if (!resource.isSuperset()){
                if (!resource.hasRequiredValues()){
                    return false;
                } else {
                }
            }
        }
        return true;
    }

    @Override
    public boolean hasCorrectTypes() {
        for (ResourceMetaData resouce:resourcesMap.values()){
            if (!resouce.hasCorrectTypes()){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean allStatementsUsed() {
        if (!unusedStatements.isEmpty()) {
            return false;
        }
        for (ResourceMetaData resouce:resourcesMap.values()){
            if (!resouce.allStatementsUsed()){
                return false;
            }
        }
        return true;
    }

    @Override
    public Set<Statement> getRDF(){
        HashSet results = new HashSet<Statement>();
        for (ResourceMetaData resouce:resourcesMap.values()){
            results.addAll(resouce.getRDF());
        }
        return results;
     }

   //** AppendBase Methods 
    
    @Override
    void appendShowAll(StringBuilder builder, int tabLevel) {
         Collection<ResourceMetaData> theResources = resourcesMap.values();
         for (ResourceMetaData resouce:theResources){
             resouce.appendShowAll(builder, 0);
         }
         //appendUnusedStatements(builder);
    }
    
    @Override
    void appendUnusedStatements(StringBuilder builder) {
         Collection<ResourceMetaData> theResources = resourcesMap.values();
         for (ResourceMetaData resouce:theResources){
             resouce.appendUnusedStatements(builder);
         }
         for (Statement statement:unusedStatements){
             builder.append(statement);
             newLine(builder);
         }
    }
   
    @Override
    void appendSchema(StringBuilder builder, int tabLevel) {
         Collection<ResourceMetaData> theResources = resourcesMap.values();
         for (ResourceMetaData resouce:theResources){
             resouce.appendSchema(builder, 0);
         }
    }

    @Override
    void appendValidityReport(StringBuilder builder, boolean checkAllpresent, boolean includeWarnings, int tabLevel) {
         Collection<ResourceMetaData> theResources = resourcesMap.values();
         for (ResourceMetaData resource:theResources){
             if (!resource.isSuperset()){
                resource.appendValidityReport(builder, checkAllpresent, includeWarnings, 0);
             } else {
                resource.appendParentValidityReport(builder, checkAllpresent, includeWarnings, 0);
             }
         }
         for (String error:errors){
             tab(builder, tabLevel);
             builder.append(error);
             newLine(builder);
         }
    }

    @Override
    public Set<Value> getValuesByPredicate(URI predicate) {
        HashSet<Value> result = null;
        for (Resource id: resourcesMap.keySet()){
            ResourceMetaData rmd = resourcesMap.get(id);
            Set<Value> moreResults = rmd.getValuesByPredicate(predicate);
            if (moreResults != null){
                if (result == null){
                    result = new HashSet<Value>();
                }
                result.addAll(moreResults);
            }
        }
        return result;
    }

     
    public void validate() throws MetaDataException {
        String report = this.validityReport(false);
        if (report.equals(CLEAR_REPORT)){
            //OK 
        } else {
            throw new MetaDataException(report);
        }
    }

    ResourceMetaData getResourceByID(Resource id) {
        return resourcesMap.get(id);
    }

    public Set<ResourceMetaData> getResourceMetaDataByType(URI type) {
        HashSet<ResourceMetaData> results = new HashSet<ResourceMetaData>();
        for (ResourceMetaData resultMetaData:resourcesMap.values()){
            if(type.equals(resultMetaData.getType())){
                results.add(resultMetaData);
            }
        }
        return results;
    }

   @Override
    public Set<ResourceMetaData> getResoucresByPredicate(URI predicate) {
        HashSet<ResourceMetaData> result = null;
        for (Resource id: resourcesMap.keySet()){
            ResourceMetaData rmd = resourcesMap.get(id);
            Set<ResourceMetaData> moreResults = rmd.getResoucresByPredicate(predicate);
            if (moreResults != null){
                if (result == null){
                    result = new HashSet<ResourceMetaData>();
                }
                result.addAll(moreResults);
            }
        }
        return result;
    }

}
