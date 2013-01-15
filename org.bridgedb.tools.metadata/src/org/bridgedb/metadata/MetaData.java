/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

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
public interface MetaData {
    
    String Schema();

    boolean hasRequiredValues();
    
    boolean hasCorrectTypes() throws BridgeDBException;

    String validityReport(boolean includeWarnings) throws BridgeDBException;

    void validate() throws BridgeDBException;
    
    boolean allStatementsUsed();
    
    String unusedStatements();
 
    /**
     * 
     * @param predicate 
     * @return A set of Values for this Predicate. 
     *       Empty if predicate is known but no values are.
     *       Null if predicate is unknown.
     */
    Set<Value> getValuesByPredicate(URI predicate);
    
    Set<ResourceMetaData> getResoucresByPredicate(URI predicate);
            
    Set<Statement> getRDF();
    
}
