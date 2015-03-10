/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridgedb.uri.tools;

import java.util.Set;

import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.uri.api.Mapping;
import org.bridgedb.uri.api.MappingsBySet;
import org.bridgedb.utils.BridgeDBException;
import org.openrdf.model.Statement;

/**
 *
 * @author christian
 */
@SuppressWarnings("deprecation")
public interface StatementMaker {
 
    /**
     * @deprecated 
     * @param mappingsBySet
     * @param lensBaseUri
     * @return
     * @throws BridgeDBException 
     */
    public Set<Statement> asRDF(MappingsBySet mappingsBySet, String baseUri, String methodName) throws BridgeDBException;

    public Set<Statement> asRDF(MappingSetInfo info, String baseUri, String contextString)  throws BridgeDBException;

    public Set<Statement> asRDF(Set<Mapping> mappings, String baseUri, boolean linksetInfo, String overridePredicateURI)  throws BridgeDBException;
}
