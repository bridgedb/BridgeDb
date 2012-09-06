/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 *
 * @author Christian
 */
public interface MetaPart {

    public void loadFromInput(MetaData metaData, RDFData input);

    public boolean hasRequiredValues(RequirementLevel requirementLevel);

    public boolean hasCorrectTypes();

    public void appendValidityReport(StringBuilder builder, MetaData parent, RequirementLevel forceLevel, 
            boolean includeWarnings);

    public void addInfo(StringBuilder builder, RequirementLevel requirementLevel);


}
