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
    DATA_SOURCE_URL_PATTERN(BridgeDBConstants.DATA_SOURCE_URL_PATTERN_SHORT, BridgeDBConstants.DATA_SOURCE_URL_PATTERN_URI), 
    IDENTIFERS_ORG(BridgeDBConstants.IDENTIFERS_ORG_PATTERN_SHORT, BridgeDBConstants.IDENTIFERS_ORG_PATTERN_URI), 
    WIKIPATHWAYS(BridgeDBConstants.WIKIPATHWAYS_PATTERN_SHORT, BridgeDBConstants.WIKIPATHWAYS_PATTERN_URI), 
    SOURCE_RDF(BridgeDBConstants.SOURCE_RDF_PATTERN_SHORT, BridgeDBConstants.SOURCE_RDF_PATTERN_URI), 
    BIO2RDF_URI(BridgeDBConstants.BIO2RDF_PATTERN_SHORT, BridgeDBConstants.BIO2RDF_PATTERN_URI);

    static UriMappingRelationship byRdfResource(Value relationshipId) throws BridgeDBException {
        for (UriMappingRelationship relationship: values()){
            if (relationship.uri.equals(relationshipId)){
                return relationship;
            }
        }
        throw new BridgeDBException("No UriMappingRelationship knoown with id " + relationshipId);
    }
    
    private String rdfId;
    private URI uri;
    
    private UriMappingRelationship(String sp, URI p){
        rdfId = sp;
        uri = p;
    }
    
    public String getRdfId(){
        return rdfId;
    }
}
