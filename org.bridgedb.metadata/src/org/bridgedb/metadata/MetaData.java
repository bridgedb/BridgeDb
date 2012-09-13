/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.util.Set;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 *
 * @author Christian
 */
interface MetaData {
    
    String Schema();

    boolean hasRequiredValues(RequirementLevel requirementLevel);
    
    boolean hasCorrectTypes();

    String validityReport(RequirementLevel forceLevel, boolean includeWarnings);
    
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
}
