/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.util.List;
import org.bridgedb.metadata.constants.SchemaConstants;
import org.w3c.dom.Element;

/**
 *
 * @author Christian
 */
public class MetaDataAlternatives extends HasChildrenMetaData implements MetaData{

    //public MetaDataAlternatives(Element element) throws MetaDataException {
    //    super(element);
    //    String requirementLevelSt = element.getAttribute(SchemaConstants.REQUIREMENT_LEVEL);
    //}
    
    private final boolean PRESENCE_OPTIONAL = false;
    
    public MetaDataAlternatives(String name, String type, RequirementLevel requirementLevel, List<MetaDataBase> childMetaData){
        super(name, type, requirementLevel, childMetaData);
    }
    
    public MetaDataAlternatives(MetaDataAlternatives other){
        super(other);
    }
    

    @Override
    MetaDataAlternatives getSchemaClone() {
        return new MetaDataAlternatives(this);
    }

    @Override
    void appendSchema(StringBuilder builder, int tabLevel) {
        tab(builder, tabLevel);
        builder.append("Alternatives ");
        builder.append(name);
        newLine(builder, tabLevel + 1);
        builder.append("RequirementLevel ");
        builder.append(requirementLevel);        
        newLine(builder);
        for (MetaDataBase child:childMetaData){
            child.appendSchema(builder, tabLevel + 1);
        }
    }

    @Override
    void appendSpecific(StringBuilder builder, int tabLevel) {
        tab(builder, tabLevel);
        builder.append("Alternatives ");
        builder.append(name);
        newLine(builder);
        tab(builder, tabLevel);
        if (noChildernWithValue()){
            builder.append("Not enough information found");
        } else {
            builder.append("Enough information found");            
        }
        newLine(builder);
    }

    @Override
    void appendValidityReport(StringBuilder builder, boolean checkAllpresent, boolean includeWarnings, int tabLevel) throws MetaDataException {
        if (checkAllpresent && noChildernWithValue()){
            if (this.requirementLevel == RequirementLevel.MUST){
                reportMissingValues(builder, "ERROR: ", tabLevel);
            } else if (includeWarnings && this.requirementLevel == RequirementLevel.SHOULD){
                reportMissingValues(builder, "WARNING: ", tabLevel);
            }
        }
        for (MetaDataBase child:childMetaData){
            child.appendValidityReport(builder, PRESENCE_OPTIONAL, includeWarnings, tabLevel+1);
        }
    }

    private void reportMissingValues(StringBuilder builder, String message, int tabLevel){
        tab(builder, tabLevel);
        builder.append(message);
        builder.append(" Alternatives ");
        builder.append(name);
        builder.append(": None of the alternatives have a value.");
        newLine(builder);
        addDocumentationLink(builder, tabLevel);
        for (MetaDataBase child:childMetaData){
            child.appendShowAll(builder, tabLevel + 1);
        }
    }
    
    private boolean noChildernWithValue(){
        for (MetaDataBase child:childMetaData){
            //Ok if a single child has valuies
            if (child.hasValues()){
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean hasRequiredValues() {
        if (requirementLevel == RequirementLevel.MUST && noChildernWithValue()){
            //At least one child must have values so false;
                return false;
        }
        return true;        
     }

    @Override
    boolean hasValues() {
        //An Alternatives has values if any of the children have values.
        for (MetaDataBase child:childMetaData){
            if (child.hasValues()) { 
                return true; 
            }
        }
        return false;
    }

}
