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
public class MetaDataCollection implements MetaData {
    
    Map<Resource,ResourceMetaData> resources = new HashMap<Resource,ResourceMetaData>();
    Set<String> errors = new HashSet<String>();
    Set<Statement> unusedStatements;
    
    public MetaDataCollection(Set<Statement> statements) throws MetaDataException {
        unusedStatements = new HashSet<Statement>(0);
        for (Statement statement:statements){
             unusedStatements.add(statement);
        }
        Set<Resource> ids = findResourceByPredicate(RdfConstants.TYPE_URI);
       for (Resource id:ids){
            if (!resources.containsKey(id)){
               ResourceMetaData resourceMetaData =  getResourceMetaData(id);
               if (resourceMetaData == null){
                   Reporter.report(id + " has no known rdf:type ");
                   errors.add(id + " has no known rdf:type ");                   
               } else {
                   resources.put(id, resourceMetaData);
               }
            }
        }
    }
    
    public String toString(){
         StringBuilder builder = new StringBuilder();
         Collection<ResourceMetaData> theResources = resources.values();
         for (ResourceMetaData resouce:theResources){
             resouce.appendToString(builder, 0);
         }
         for (Statement statement: unusedStatements){
             builder.append("\n");
             builder.append(statement);
         }
         return builder.toString();
    }
    
    public String Schema(){
         StringBuilder builder = new StringBuilder();
         Collection<ResourceMetaData> theResources = resources.values();
         for (ResourceMetaData resouce:theResources){
             resouce.appendSchema(builder, 0);
         }
         return builder.toString();
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
    
    public final Set<Value> findBySubjectPredicate(Resource subject, URI predicate){
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
