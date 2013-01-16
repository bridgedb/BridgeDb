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

import java.util.ArrayList;
import java.util.List;
import org.bridgedb.utils.BridgeDBException;

/**
 *
 * @author Christian
 */
public class MetaDataGroup extends HasChildrenMetaData implements MetaData{

    //public MetaDataGroup(Element element) throws BridgeDBException {
    //    super(element);
    //}
    
    public MetaDataGroup(String name, String type, RequirementLevel requirementLevel, List<MetaDataBase> childMetaData){
        super(name, type, requirementLevel, childMetaData);
    }
    
    public MetaDataGroup(MetaDataGroup other){
        super(other);
    }
    
    @Override
    MetaDataGroup getSchemaClone() {
        List<MetaDataBase> children = new ArrayList<MetaDataBase>();
        for (MetaDataBase child:childMetaData){
            children.add(child.getSchemaClone());
        }
        return new MetaDataGroup(this);
    }

    @Override
    void appendSpecific(StringBuilder builder, int tabLevel) {
        tab(builder, tabLevel);
        builder.append("Group ");
        builder.append(name);
        newLine(builder);
    }
    
     @Override
    void appendValidityReport(StringBuilder builder, boolean checkAllpresent, boolean includeWarnings, int tabLevel) 
             throws BridgeDBException {
        for (MetaDataBase child:childMetaData){
            child.appendValidityReport(builder, checkAllpresent, includeWarnings, tabLevel+1);
        }
        if (!includeWarnings){
            return;
        }
        //check if any item in the group has a value
        boolean valueFound = false;
        boolean valueMissing = false;
        for (MetaDataBase child:childMetaData){
            if (child.hasValues()){
                valueFound = true;
            } else {
                valueMissing = true;
            }
        }
        if (valueFound && valueMissing){
            tab(builder, tabLevel);
            builder.append("WARNING: ");
            builder.append(" Group ");
            builder.append(name);
            builder.append(": Some but not all items in the group found.");
            newLine(builder);
            addDocumentationLink(builder, tabLevel);
            //check all items in the group have a value
            for (MetaDataBase child:childMetaData){
                 child.appendShowAll(builder, tabLevel + 1);
            }
        }
    }

}
