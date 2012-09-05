/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.bridgedb.linkset.constants.DctermsConstants;
import org.bridgedb.linkset.constants.PavConstants;
import org.bridgedb.linkset.constants.VoidConstants;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 *
 * @author Christian
 */
public class LinkSetMetaData extends CollectionMetaData{
    
    public static URI RESOURCE_TYPE = VoidConstants.LINKSET;

    DataSetMetaData sourceDataSet;
    DataSetMetaData objectDataSet;
    //Add them to CollectionMetaData.dataSets as well to pick up code there
    
    public LinkSetMetaData(Resource id, RDFData input){
        super(id, input);        
    }

    public LinkSetMetaData(RDFData input){
        super(input);        
    }

    @Override
    void setupValues() {
        values.add(new SingletonValue("License", DctermsConstants.LICENSE, URI.class, RequirementLevel.MUST));
        values.add(new SingletonValue("Authored By",PavConstants.AUTHORED_BY, URI.class, RequirementLevel.MUST));
        values.add(new SingletonValue("Authored On",PavConstants.AUTHORED_ON, Date.class, RequirementLevel.MUST));
        values.add(new SingletonValue("Created By",PavConstants.CREATED_BY, URI.class, RequirementLevel.MUST));
        values.add(new SingletonValue("Created On",PavConstants.CREATED_ON, Date.class, RequirementLevel.MUST));
        values.add(new SingletonValue("Predicate",VoidConstants.LINK_PREDICATE, URI.class, RequirementLevel.TECHNICAL_MUST));
        values.add(new SingletonValue("Number of Links",VoidConstants.TRIPLES, Integer.class, RequirementLevel.MUST));
        values.add(new SingletonValue("Title", DctermsConstants.TITLE, String.class, RequirementLevel.MUST));
        values.add(new SingletonValue("Description", DctermsConstants.DESCRIPTION, String.class, RequirementLevel.MUST));
    }

    @Override
    void readFromInput(RDFData input) {
        Set<MetaData> collection = getCollection();
        //Read source And Object DataSets first to stop super from reading them as just datasets. 
        Value sourceId = this.getByIdPredicate(input, VoidConstants.SUBJECTSTARGET);   
        if (sourceId != null && sourceId instanceof Resource){
            sourceDataSet = new DataSetMetaData((Resource)sourceId, input);
            collection.add(sourceDataSet);
        } else {
            sourceDataSet = null;
        }
        Value objectId = this.getByIdPredicate(input, VoidConstants.OBJECTSTARGET);   
        if (objectId != null && objectId instanceof Resource){
            objectDataSet = new DataSetMetaData((Resource)objectId, input);
            collection.add(objectDataSet);
        } else {
            objectDataSet = null;
        }
        super.readFromInput(input);
    }

    @Override
    URI getResourceType() {
         return RESOURCE_TYPE;
    }
    
    public boolean hasRequiredValues(RequirementLevel forceLevel, boolean exceptAlternatives){
        if (this.sourceDataSet == null){ return false; }
        if (this.objectDataSet == null){ return false; }
        //Super will check the datasets as long as they are there.
        return super.hasRequiredValues(forceLevel, exceptAlternatives);
    }

    void validityReport(StringBuilder builder, RequirementLevel forceLevel, boolean exceptAlternatives, 
            boolean includeWarnings){
        if (this.sourceDataSet == null){         
            builder.append("ERROR: sourceDataSet not found. \n");
        }
        if (this.objectDataSet == null){         
            builder.append("ERROR: objectDataSet not found. \n");
        }
        super.validityReport(builder, forceLevel, exceptAlternatives, includeWarnings);
    }
    
    void addInfo(StringBuilder builder, RequirementLevel forceLevel){
        super.addInfo(builder, forceLevel);
        builder.append("Source: ");
        newLine(builder);
        sourceDataSet.addInfo(builder, forceLevel);
        builder.append("Object: ");
        newLine(builder);
        sourceDataSet.addInfo(builder, forceLevel);
    }
}
