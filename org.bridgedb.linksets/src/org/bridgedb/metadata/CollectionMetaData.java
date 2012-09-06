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
public abstract class CollectionMetaData extends MetaData{

    private Set<MetaData> collection; 
    
    public CollectionMetaData(Resource id, RDFData input){
        super(id, input);
    }
    
    public CollectionMetaData(RDFData input){
        super(input);
    }

    final Set<MetaData> getCollection(){
        if (collection == null){
            collection = new HashSet<MetaData>();
        }
        return collection;
    }
    
    @Override
    void addChildren(StringBuilder builder, RequirementLevel forceLevel) {
        for (MetaData metaData:collection){
            metaData.addInfo(builder, forceLevel);
        }
    }

    @Override
    public boolean hasRequiredValues(RequirementLevel forceLevel){
        if (!super.hasRequiredValues(forceLevel)) { return false; }
        for (MetaData metaData:collection){
            if (!metaData.hasRequiredValues(forceLevel)) { return false; }
        }
        return true;
    }

    public boolean hasCorrectTypes(){
        if (!super.hasCorrectTypes()) { return false; }
        for (MetaData metaData:collection){
            if (!metaData.hasCorrectTypes()) { return false; }
        }
        return true;
    }

    void validityReport(StringBuilder builder, RequirementLevel forceLevel, boolean includeWarnings){
        super.validityReport(builder, forceLevel, includeWarnings);
        for (MetaData metaData:collection){
            metaData.validityReport(builder, forceLevel, includeWarnings);
        }
    }
}
