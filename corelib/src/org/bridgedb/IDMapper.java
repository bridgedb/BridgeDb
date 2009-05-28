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
 * Base interface for all id mapping methods.
 * Has methods for basic functionality such as looking up cross-references and backpage text.
 */
public interface IDMapper {
    /**
     * Supports one-to-one mapping and one-to-many mapping.
     * @param srcXrefs source Xref, containing ID and ID type/data source
     * @param tgtDataSources target ID types/data sources
     * @return a map from source Xref to target Xref's
     * @throws IDMapperException if failed
     */
    public Map<Xref, Set<Xref>> mapID(Set<Xref> srcXrefs, Set<DataSource> tgtDataSources) throws IDMapperException;

    /**
     * Check whether an Xref exists.
     * @param xref reference to check
     * @return if the reference exists, false if not
     * @throws IDMapperException if failed
     */
    public boolean xrefExists(Xref xref) throws IDMapperException;

    /**
     * free text search for matching symbols or identifiers
     * @param text text to search
     * @param limit up limit of number of hits
     * @return a set of hit references
     * @throws IDMapperException if failed
     * @throws UnsupportedOperationException if free search is not supported.
     */
    public Set<Xref> freeSearch (String text, int limit) throws IDMapperException;

    /**
     *
     * @return capacities of the ID mapper
     */
    public IDMapperCapabilities getCapabilities();
}
