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

import java.util.Set;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

/**
 *
 * @author Christian
 */
public abstract class MetaDataBase extends AppendBase implements MetaData{
    
    Resource id;
    final String type;
    final String name;
    final String documentation;
    final RequirementLevel requirementLevel;
    public static int NO_CARDINALITY = -1;

   // MetaData parent;
    
    //MetaDataBase(Element element){
    //    name = element.getAttribute(SchemaConstants.NAME);
    //    documentation = MetaDataRegistry.getDocumentationRoot() + element.getAttribute(SchemaConstants.DOCUMENTATION);
    //}
    
    MetaDataBase(String name, String type, RequirementLevel requirementLevel){
        this.name = name;
        this.type = type;
        this.requirementLevel = requirementLevel;
        this.documentation = MetaDataSpecification.getDocumentationRoot();
    }
    
    MetaDataBase(Resource id, String name, String type, RequirementLevel requirementLevel){
        this.id = id;
        this.name = name;
        this.type = type;
        this.requirementLevel = requirementLevel;
        this.documentation = MetaDataSpecification.getDocumentationRoot();
    }
    
    MetaDataBase(Resource id, MetaDataBase other){
        this.id = id;
        this.name = other.name;
        this.type = other.type;
        this.requirementLevel = other.requirementLevel;
        this.documentation = other.documentation;
    }
    
    abstract void loadValues(Set<Statement> data, Set<String> errors);

    void setupValues(Resource id){
        this.id = id;       
    }
    
    final void addDocumentationLink(StringBuilder builder, int tabLevel) {
        //tab(builder, tabLevel + 1);
        //builder.append("See: ");
        //builder.append(documentation);
        //newLine(builder);
    }

    abstract MetaDataBase getSchemaClone(Resource id, MetaDataCollection collection);
    
    abstract boolean hasValues();

    abstract Set<? extends LeafMetaData> getLeaves();

    abstract LeafMetaData getLeafByPredicate(URI predicate);

}
