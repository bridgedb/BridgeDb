/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import org.bridgedb.metadata.constants.RdfConstants;
import org.bridgedb.metadata.constants.VoidConstants;
import org.bridgedb.metadata.utils.Reporter;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.xml.sax.SAXException;

/**
 *
 * @author Christian
 */
public class MetaDataCollection extends AppendBase implements MetaData {
    
    Map<Resource,ResourceMetaData> resourcesMap = new HashMap<Resource,ResourceMetaData>();
    Set<String> errors = new HashSet<String>();
    Set<Statement> unusedStatements;
    
    public MetaDataCollection(Set<Statement> statements) throws MetaDataException {
        unusedStatements = new HashSet<Statement>(0);
        for (Statement statement:statements){
             unusedStatements.add(statement);
        }
        Set<Statement> subsetStatements = extractStatementsByPredicate(VoidConstants.SUBSET);
        Set<Resource> ids = findResourceByPredicate(RdfConstants.TYPE_URI);
        for (Resource id:ids){
            if (!resourcesMap.containsKey(id)){
               ResourceMetaData resourceMetaData =  getResourceMetaData(id);
               if (resourceMetaData == null){
                   Reporter.report(id + " has no known rdf:type ");
                   errors.add(id + " has no known rdf:type ");                   
               } else {
                   resourcesMap.put(id, resourceMetaData);
               }
            }
        }
        addSubsets(subsetStatements);
    }
    
    private ResourceMetaData getResourceMetaData (Resource id) throws MetaDataException{
        Set<Value> types = findBySubjectPredicate(id, RdfConstants.TYPE_URI);
        ResourceMetaData resourceMetaData = null;
        for (Value type:types){
            ResourceMetaData rmd =  MetaDataRegistry.getResourceByType(type);
            if (rmd != null){
                if (resourceMetaData == null){
                   resourceMetaData = rmd; 
                   resourceMetaData.loadValues(id, unusedStatements, this);
                } else {
                   errors.add(id + " has a second known rdf:type " + type);
                }
            }
        } 
        return resourceMetaData;
    }
    
    private Set<Resource> findResourceByPredicate(URI predicate){
        HashSet<Resource> results = new HashSet<Resource>();
        for (Statement statement: unusedStatements) {
            if (statement.getPredicate().equals(predicate)){
                 results.add(statement.getSubject());
            }
        }  
        return results;
    }
    
    private Set<Statement> extractStatementsByPredicate(URI predicate){
        HashSet<Statement> results = new HashSet<Statement>();
        for (Iterator<Statement> iterator = unusedStatements.iterator(); iterator.hasNext();) {
            Statement statement = iterator.next();
            if (statement.getPredicate().equals(predicate)){
                iterator.remove();
                results.add(statement);
            }
        }  
        return results;
    }

    private Set<Value> findBySubjectPredicate(Resource subject, URI predicate){
        HashSet<Value> values = new HashSet<Value>();         
        for (Statement statement: unusedStatements){
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
            ResourceMetaData parent = MetaDataRegistry.getResourceByID(statement.getSubject());
            ResourceMetaData child = MetaDataRegistry.getResourceByID(statement.getSubject());
            child.addParent(parent);
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

    // ** MetaData Methods 
    @Override
    public boolean hasRequiredValues(RequirementLevel requirementLevel) {
        for (ResourceMetaData resouce:resourcesMap.values()){
            if (!resouce.hasRequiredValues(requirementLevel)){
                return false;
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
        for (ResourceMetaData resouce:resourcesMap.values()){
            if (!resouce.allStatementsUsed()){
                return false;
            }
        }
        return true;
    }

   //** AppendBase Methods 
    
    @Override
    void appendShowAll(StringBuilder builder, RequirementLevel forceLevel, int tabLevel) {
         Collection<ResourceMetaData> theResources = resourcesMap.values();
         for (ResourceMetaData resouce:theResources){
             resouce.appendShowAll(builder, forceLevel, 0);
         }
         appendUnusedStatements(builder);
    }
    
    @Override
    void appendUnusedStatements(StringBuilder builder) {
         for (Statement statement: unusedStatements){
             builder.append(statement);
             newLine(builder);
         }
         Collection<ResourceMetaData> theResources = resourcesMap.values();
         for (ResourceMetaData resouce:theResources){
             resouce.appendUnusedStatements(builder);
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
    void appendValidityReport(StringBuilder builder, RequirementLevel forceLevel, boolean includeWarnings, int tabLevel) {
         Collection<ResourceMetaData> theResources = resourcesMap.values();
         for (ResourceMetaData resouce:theResources){
             resouce.appendValidityReport(builder, forceLevel, includeWarnings, 0);
         }
    }

    @Override
    public Set<Value> getValuesByPredicate(URI predicate) {
        HashSet<Value> result = null;
        for (Resource id: resourcesMap.keySet()){
            ResourceMetaData rmd = MetaDataRegistry.getResourceByID(id);
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

}
