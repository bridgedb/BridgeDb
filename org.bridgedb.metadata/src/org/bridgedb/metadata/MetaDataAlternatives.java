/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bridgedb.metadata.constants.SchemaConstants;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.w3c.dom.Element;

/**
 *
 * @author Christian
 */
public class MetaDataAlternatives extends HasChildrenMetaData implements MetaData{

    private final RequirementLevel requirementLevel;

    public MetaDataAlternatives(Element element) throws MetaDataException {
        super(element);
        String requirementLevelSt = element.getAttribute(SchemaConstants.REQUIREMENT_LEVEL);
        requirementLevel = RequirementLevel.parse(requirementLevelSt);
    }
    
    public MetaDataAlternatives(String name, RequirementLevel requirementLevel, List<MetaDataBase> children){
        super(name, children);
        this.requirementLevel = requirementLevel;
    }
    

    @Override
    MetaDataAlternatives getSchemaClone() {
        List<MetaDataBase> children = new ArrayList<MetaDataBase>();
        for (MetaDataBase child:childMetaData){
            children.add(child.getSchemaClone());
        }
        return new MetaDataAlternatives(name, requirementLevel, children);
    }

    @Override
    void appendSchema(StringBuilder builder, int tabLevel) {
        tab(builder, tabLevel);
        builder.append("Alternatives ");
        builder.append(name);
        newLine(builder, tabLevel + 1);
        builder.append("Requirement Level ");
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
    }

    @Override
    void appendValidityReport(StringBuilder builder, RequirementLevel forceLevel, boolean includeWarnings, int tabLevel) {
        if (noChildernWithValue()){
            tab(builder, tabLevel);
            builder.append("ERROR: ");
            builder.append(id);
            builder.append(" Alternatives ");
            builder.append(name);
            builder.append(" None of the alternatives have a value.");
            for (MetaDataBase child:childMetaData){
                 child.appendShowAll(builder, RequirementLevel.MAY, tabLevel + 1);
            }
        }
        for (MetaDataBase child:childMetaData){
            child.appendValidityReport(builder, forceLevel, includeWarnings, tabLevel);
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
    public boolean hasRequiredValues(RequirementLevel forceLevel) {
        if (requirementLevel.compareTo(forceLevel) <= 0){
            if (noChildernWithValue()){
            //At least one child must have values so false;
                return false;
            }
        }
        for (MetaDataBase child:childMetaData){
            if (!child.hasRequiredValues(requirementLevel)){
                return false;
            }
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
