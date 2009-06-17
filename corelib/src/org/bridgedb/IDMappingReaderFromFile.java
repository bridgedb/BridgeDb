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

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import java.net.URL;


/**
 * Interface for reading ID mapping data
 * 
 */
public abstract class IDMappingReaderFromFile implements IDMappingReader {
    protected final Set<DataSource> dataSources;
    protected final Map<Xref,Set<Xref>> mapXrefs;
    protected final URL url;

    public IDMappingReaderFromFile(final URL url) {
        if (url==null) {
            throw new NullPointerException();
        }
        this.dataSources = new HashSet();
        this.mapXrefs = new HashMap();
        this.url = url;
    }

    /**
     *
     * @return data sources
     */
    public Set<DataSource> getDataSources() {
        return this.dataSources;
    }

    /**
     * 
     * @return
     */
    public Map<Xref,Set<Xref>> getIDMappings() {
        return this.mapXrefs;
    }

    /**
     *
     * @param xrefs matched references
     */
    protected void addIDMapping(final Set<Xref> xrefs) {
        if (xrefs==null) {
            throw new NullPointerException();
        }

        Set<Xref> newXrefs = new HashSet(xrefs);
        for (Xref xref : xrefs) {
            Set<Xref> oldXrefs = mapXrefs.get(xref);
            if (oldXrefs!=null) {
                newXrefs.addAll(oldXrefs); // merge
            }
        }

        for (Xref xref : newXrefs) {
            mapXrefs.put(xref, newXrefs);
        }

    }

}
