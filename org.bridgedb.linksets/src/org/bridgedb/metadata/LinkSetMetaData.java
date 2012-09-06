/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.bridgedb.linkset.constants.DctermsConstants;
import org.bridgedb.linkset.constants.DulConstants;
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
        values.add(new SingletonValue("Title", DctermsConstants.TITLE, String.class, RequirementLevel.MUST));
        values.add(new SingletonValue("Description", DctermsConstants.DESCRIPTION, String.class, RequirementLevel.MUST));
        values.add(new SingletonValue("License", DctermsConstants.LICENSE, URI.class, RequirementLevel.MUST));
        //Target Datasets added in readFromInput
        values.add(new SingletonValue("Link Relationship",VoidConstants.LINK_PREDICATE, URI.class, RequirementLevel.TECHNICAL_MUST));
        values.add(new SingletonValue("Link Justification",DulConstants.EXPRESSES, URI.class, RequirementLevel.MUST));
        //Link Source
            SingletonValue authoredBy = new SingletonValue("Authored By",PavConstants.AUTHORED_BY, URI.class, RequirementLevel.MUST);
            values.add(authoredBy);        
            SingletonValue authoredOn = new SingletonValue("Authored On",PavConstants.AUTHORED_ON, Date.class, RequirementLevel.MUST);
            values.add(authoredOn); 
            SingletonValue createdBy = new SingletonValue("Created By",PavConstants.CREATED_BY, URI.class, RequirementLevel.MUST);
            values.add(createdBy); 
            SingletonValue createdOn = new SingletonValue("Created On",PavConstants.CREATED_ON, Date.class, RequirementLevel.MUST);
            values.add(createdOn); 
        values.add(new SingletonValue("Number of Links",VoidConstants.TRIPLES, Integer.class, RequirementLevel.SHOULD));
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
        Value targetID = this.getByIdPredicate(input, VoidConstants.TARGET);  
        while (targetID != null){
            DataSetMetaData target = new DataSetMetaData((Resource)targetID, input);
            collection.add(target);
            targetID = this.getByIdPredicate(input, VoidConstants.TARGET);  
        }
        super.readFromInput(input);
    }

    @Override
    URI getResourceType() {
         return RESOURCE_TYPE;
    }
    
    public boolean hasRequiredValues(RequirementLevel forceLevel, boolean exceptAlternatives){
        if (getCollection().size() != 2){ return false; }
        //Super will check the datasets as long as they are there.
        return super.hasRequiredValues(forceLevel, exceptAlternatives);
    }

    void validityReport(StringBuilder builder, RequirementLevel forceLevel, boolean exceptAlternatives, 
            boolean includeWarnings){
        if (getCollection().size() != 2){
            builder.append("ERROR: Found ");
            builder.append(getCollection().size());
            builder.append(" targets. Expected 2!\n");
        }
        if (this.sourceDataSet == null){         
            builder.append("WARNING: sourceDataSet not found. \n");
        }
        if (this.objectDataSet == null){         
            builder.append("WARNING: objectDataSet not found. \n");
        }
        super.validityReport(builder, forceLevel, exceptAlternatives, includeWarnings);
    }
    
    void addInfo(StringBuilder builder, RequirementLevel forceLevel){
        super.addInfo(builder, forceLevel);
        builder.append("Source: ");
        newLine(builder);
        if (sourceDataSet != null){
            sourceDataSet.addInfo(builder, forceLevel);
        } else {
            tab(builder);
            builder.append("No Source Dataset define.");
            newLine(builder);
        }
        builder.append("Object: ");
        newLine(builder);
        if (objectDataSet != null){
            objectDataSet.addInfo(builder, forceLevel);
        } else {
            tab(builder);
            builder.append("No Object Dataset define.");
            newLine(builder);
        }
    }
}
