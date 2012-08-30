/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;
import java.util.HashSet;
import java.util.Set;
import org.openrdf.model.Resource;

/**
 *
 * @author Christian
 */
public abstract class DataSetCollectionMetaData extends MetaData{

    Set<DataSetMetaData> dataSets; 
    
    public DataSetCollectionMetaData(Resource id, RDFData input){
        super(id, input);
    }
    
    public DataSetCollectionMetaData(RDFData input){
        super(input);
    }

    @Override
    void addChildren(StringBuilder builder, RequirementLevel forceLevel) {
        for (DataSetMetaData dataset:dataSets){
            dataset.addInfo(builder, forceLevel);
        }
    }

    public boolean hasRequiredValues(RequirementLevel forceLevel, boolean exceptAlternatives){
        if (!super.hasRequiredValues(forceLevel, exceptAlternatives)) { return false; }
        for (DataSetMetaData dataset:dataSets){
            if (!dataset.hasRequiredValues(forceLevel, exceptAlternatives)) { return false; }
        }
        return true;
    }

    public boolean hasCorrectTypes(){
        if (!super.hasCorrectTypes()) { return false; }
        for (DataSetMetaData dataset:dataSets){
            if (!dataset.hasCorrectTypes()) { return false; }
        }
        return true;
    }

    void validityReport(StringBuilder builder, RequirementLevel forceLevel, boolean exceptAlternatives, 
            boolean includeWarnings){
        super.validityReport(builder, forceLevel, exceptAlternatives, includeWarnings);
        for (DataSetMetaData dataset:dataSets){
            dataset.validityReport(builder, forceLevel, exceptAlternatives, includeWarnings);
        }
    }
}
