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
public class MetaDataGroup extends MetaDataBase implements MetaData{

    private final List<MetaDataBase> childMetaData;
    private final String name;

    public MetaDataGroup(Element element) throws MetaDataException {
        name = element.getAttribute(SchemaConstants.NAME);
        childMetaData = MetaDataRegistry.getChildMetaData(element);
    }
    
    public MetaDataGroup(String name, List<MetaDataBase> children){
        this.name = name;
        this.childMetaData = children;
    }
    
    @Override
    void loadValues(Resource id, Set<Statement> data, MetaData parent) {
        setupValues(id, parent);
        for (MetaDataBase child:childMetaData){
            child.loadValues(id, data, this);
        }
    }

    @Override
    MetaDataGroup getSchemaClone() {
        List<MetaDataBase> children = new ArrayList<MetaDataBase>();
        for (MetaDataBase child:childMetaData){
            children.add(child.getSchemaClone());
        }
        return new MetaDataGroup(name, children);
    }

    @Override
    void appendSchema(StringBuilder builder, int tabLevel) {
        tab(builder, tabLevel);
        builder.append("Group ");
        builder.append(name);
        newLine(builder);
        for (MetaDataBase child:childMetaData){
            child.appendSchema(builder, tabLevel + 1);
        }
    }

    @Override
    void appendShowAll(StringBuilder builder, RequirementLevel forceLevel, int tabLevel) {
        tab(builder, tabLevel);
        builder.append("Group ");
        builder.append(name);
        newLine(builder);
        for (MetaDataBase child:childMetaData){
            child.appendShowAll(builder, forceLevel, tabLevel + 1);
        }
    }

    @Override
    void appendValidityReport(StringBuilder builder, RequirementLevel forceLevel, boolean includeWarnings, int tabLevel) {
        for (MetaDataBase child:childMetaData){
            child.appendValidityReport(builder, forceLevel, includeWarnings, tabLevel);
        }
        if (!includeWarnings){
            return;
        }
        //check if any item in the group has a value
        boolean valueFound = false;
        boolean valueMissing = false;
        for (MetaDataBase child:childMetaData){
            if (child.hasRequiredValues(RequirementLevel.MAY)){
                valueFound = true;
            } else {
                valueMissing = true;
            }
        }
        if (valueFound && valueMissing){
            tab(builder, tabLevel);
            builder.append("WARNING: ");
            builder.append(id);
            builder.append(" Group ");
            builder.append(name);
            builder.append(" Some but not all items in the group found.");
            //check all items in the group have a value
            for (MetaDataBase child:childMetaData){
                 child.appendShowAll(builder, RequirementLevel.MAY, tabLevel + 1);
            }
        }
    }

    @Override
    public boolean hasRequiredValues(RequirementLevel requirementLevel) {
        for (MetaDataBase child:childMetaData){
            if (!child.hasRequiredValues(requirementLevel)){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean hasCorrectTypes() {
        for (MetaDataBase child:childMetaData){
            if (!child.hasCorrectTypes()){
                return false;
            }
        }
        return true;
    }
    
}
