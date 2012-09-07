/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import org.bridgedb.metadata.constants.RdfConstants;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.xml.sax.SAXException;

/**
 *
 * @author Christian
 */
public class MetaDataCollection {
    
    Map<Resource,ResourceMetaData> resources = new HashMap<Resource,ResourceMetaData>();
    Set<String> errors = new HashSet<String>();
    
    public MetaDataCollection(Set<Statement> data) throws MetaDataException {
        Set<Resource> ids = getByPredicate(data, RdfConstants.TYPE_URI);
        for (Resource id:ids){
            if (!resources.containsKey(id)){
               ResourceMetaData resourceMetaData =  getResourceMetaData(data, id);
               if (resourceMetaData == null){
                   errors.add(id + " has no known rdf:type ");                   
               } else {
                   resourceMetaData.loadValues(id, data);
                   
               }
            }
        }
    }
    
    private ResourceMetaData getResourceMetaData(Set<Statement> data, Resource id) throws MetaDataException{
        Set<Value> types = getBySubjectPredicate(data, id, RdfConstants.TYPE_URI);
        ResourceMetaData resourceMetaData = null;
        for (Value type:types){
            ResourceMetaData rmd =  MetaDataClassFactory.getResourceByType(type);
            if (rmd != null){
                if (resourceMetaData == null){
                   resourceMetaData = rmd; 
                } else {
                   errors.add(id + " has a second known rdf:type " + type);
                }
            }
        } 
        return resourceMetaData;
    }
    
    private Set<Resource> getByPredicate(Set<Statement> data, URI predicate){
        HashSet<Resource> results = new HashSet<Resource>(0);
        for (Statement statement: data){
            if (statement.getPredicate().equals(predicate)){
                 results.add(statement.getSubject());
            }
        }  
        return results;
    }
    
   public static final Set<Value> getBySubjectPredicate(Set<Statement> data, Resource subject, URI predicate){
        HashSet<Value> values = new HashSet<Value>();         
        for (Statement statement: data){
            if (statement.getSubject().equals(subject)){
                if (statement.getPredicate().equals(predicate)){
                    values.add(statement.getObject());
                }
            }
        }  
        return values;
    }
}
