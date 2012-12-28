/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import org.bridgedb.rdf.constants.BridgeDBConstants;
import org.bridgedb.utils.BridgeDBException;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 *
 * @author Christian
 */
public enum UriMappingRelationship {
    
    //The URL pattern already recorded in the DataSource in Version 1
    DATA_SOURCE_URL_PATTERN(BridgeDBConstants.DATA_SOURCE_URL_PATTERN_SHORT, 
            BridgeDBConstants.DATA_SOURCE_URL_PATTERN_URI, false), 
    //The IDENTIFERS_ORG Uri which can be created from the (mirian) URN in DataSource
    IDENTIFERS_ORG(BridgeDBConstants.IDENTIFERS_ORG_PATTERN_SHORT, BridgeDBConstants.IDENTIFERS_ORG_PATTERN_URI, 
            false), 
    //The one used in WikiPathways data
    WIKIPATHWAYS(BridgeDBConstants.WIKIPATHWAYS_PATTERN_SHORT, BridgeDBConstants.WIKIPATHWAYS_PATTERN_URI, false), 
    //The one used by the data source provider
    SOURCE_RDF(BridgeDBConstants.SOURCE_RDF_PATTERN_SHORT, BridgeDBConstants.SOURCE_RDF_PATTERN_URI, false), 
    //The one used by the http://bio2rdf.org/
    BIO2RDF_URI(BridgeDBConstants.BIO2RDF_PATTERN_SHORT, BridgeDBConstants.BIO2RDF_PATTERN_URI, false);

    static UriMappingRelationship byRdfResource(Value relationshipId) throws BridgeDBException {
        for (UriMappingRelationship relationship: values()){
            if (relationship.uri.equals(relationshipId)){
                return relationship;
            }
        }
        throw new BridgeDBException("No UriMappingRelationship knoown with id " + relationshipId);
    }
    
    private final String rdfId;
    private final URI uri;
    private final boolean multiplesUriPatternsAllowed;
    
    private UriMappingRelationship(String sp, URI p, boolean manyUriPatterns){
        rdfId = sp;
        uri = p;
        multiplesUriPatternsAllowed = manyUriPatterns;
    }
    
    public String getRdfId(){
        return rdfId;
    }
    
    public boolean multiplesUriPatternsAllowed(){
        return multiplesUriPatternsAllowed; 
    }

}
