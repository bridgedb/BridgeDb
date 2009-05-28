// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2009 BiGCaT Bioinformatics
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

package org.bridgedb;

import java.util.Map;
import java.util.Set;

/**
 *
 * 
 */
public interface IDMapper {
        /**
     * Supports one-to-one mapping and one-to-many mapping.
     * @param srcXrefs source Xref, containing ID and ID type/data source
     * @param tgtDataSources target ID types/data sources
     * @return a map from source Xref to target Xref's
     */
    public Map<Xref, Set<Xref>> mapID(Set<Xref> srcXrefs, Set<DataSource> tgtDataSources);

    // Check whether an Xref exists.
    public boolean xrefExists(Xref xref);

    // Free text search
    public Set<Xref> freeSearch (String text, int limit);

    // returns capacities of the ID mapper
    public IDMapperCapabilities getCapabilities();
}
