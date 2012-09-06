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
    /*
           if (valueBase.multipleValuesAllowed()){
             } else {
            }

     */

    public boolean hasRequiredValues(RequirementLevel requirementLevel, boolean forceLevel);
    //if ((valueBase.level.compareTo(forceLevel) <= 0) && !valueBase.hasValue(exceptAlternatives)){

    public boolean hasCorrectTypes();
    //            if (valueBase.hasValue()){
    //            if (!valueBase.correctType()){
    //                return false;
    //            }
    //        }

    public void appendValidityReport(StringBuilder builder, MetaData aThis, RequirementLevel requirementLevel, boolean forceLevel, boolean exceptAlternatives);

    public void addInfo(StringBuilder builder, RequirementLevel requirementLevel);


}
