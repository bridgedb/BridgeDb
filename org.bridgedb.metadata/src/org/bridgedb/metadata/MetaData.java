/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.util.Set;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

/**
 *
 * @author Christian
 */
interface MetaData {
    
    String Schema();

    boolean hasRequiredValues(RequirementLevel requirementLevel);
    
    boolean hasCorrectTypes();

    String validityReport(StringBuilder builder, RequirementLevel forceLevel, boolean includeWarnings);
    
    void appendValidityReport(StringBuilder builder, RequirementLevel forceLevel, boolean includeWarnings, int tabLevel);

}
