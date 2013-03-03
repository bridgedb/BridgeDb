// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009  BridgeDb developers
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  OpenPhacts
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.bridgedb.tools.metadata;

import java.util.List;
import org.bridgedb.utils.BridgeDBException;
import org.openrdf.model.Resource;

/**
 *
 * @author Christian
 */
public class MetaDataAlternatives extends HasChildrenMetaData implements MetaData{

    //public MetaDataAlternatives(Element element) throws BridgeDBException {
    //    super(element);
    //    String requirementLevelSt = element.getAttribute(SchemaConstants.REQUIREMENT_LEVEL);
    //}
    
    private final boolean PRESENCE_OPTIONAL = false;
    
    public MetaDataAlternatives(String name, String type, RequirementLevel requirementLevel, List<MetaDataBase> childMetaData){
        super(name, type, requirementLevel, childMetaData);
    }
    
    public MetaDataAlternatives(Resource id, MetaDataAlternatives other, MetaDataCollection collection){
        super(id, other, collection);
    }
    

    @Override
    MetaDataAlternatives getSchemaClone(Resource id, MetaDataCollection collection) {
        return new MetaDataAlternatives(id, this, collection);
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
    void appendValidityReport(StringBuilder builder, boolean checkAllpresent, boolean includeWarnings, int tabLevel) 
            throws BridgeDBException {
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
