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

import java.io.IOException;

/**
 * Interface for ID mapping from files
 * 
 */ 
public abstract class IDMapperFile implements IDMapper {
    protected final IDMapperFileCapabilities cap;
    protected Map<Xref,Set<Xref>> mapXrefs; // map from each Xref to all its matched references
    protected final IDMappingReader reader;

    /**
     * Constuctor from a url. Free search is unsupported by default.
     * @param url
     * @throws java.io.IOException
     */
    public IDMapperFile(final IDMappingReader reader) {
        this(reader, false);
    }

    /**
     *
     * @param url
     * @param freeSearch
     * @throws IDMapperException when failed to read
     */
    public IDMapperFile(final IDMappingReader reader,
            final boolean freeSearch) {
        if (reader==null) {
            throw new NullPointerException();
        }

        this.reader = reader;
        cap = new IDMapperFileCapabilities(freeSearch);        
    }

    /**
     * Supports one-to-one mapping and one-to-many mapping.
     * @param srcXrefs source Xref, containing ID and ID type/data source
     * @param tgtDataSources target ID types/data sources
     * @return a map from source Xref to target Xref's
     * @throws IDMapperException if failed
     */
    public Map<Xref, Set<Xref>> mapID(final Set<Xref> srcXrefs, final Set<DataSource> tgtDataSources) throws IDMapperException {
        if (srcXrefs==null || tgtDataSources==null) {
            throw new NullPointerException();
        }

        read();

        if (!cap.getSupportedTgtDataSources().containsAll(tgtDataSources)) {
            // TODO: throw an expeption or just ignore the unsupported data sources?
            throw new IDMapperException("Unsupported target data sources.");
        }

        Map<Xref, Set<Xref>> return_this = new HashMap();

        for (Xref srcXref : srcXrefs) {
            Set<Xref> refs = mapXrefs.get(srcXref);
            if (refs==null) continue;

            Set<Xref> tgtRefs = return_this.get(srcXref);
            if (tgtRefs==null) {
                tgtRefs = new HashSet();
                return_this.put(srcXref, tgtRefs);
            }

            for (Xref tgtXref : refs) {
                if (tgtDataSources.contains(tgtXref.getDataSource())) {
                    tgtRefs.add(tgtXref);
                }
            }
        }

        return return_this;
    }

    /**
     * Check whether an Xref exists.
     * @param xref reference to check
     * @return if the reference exists, false if not
     * @throws IDMapperException if failed
     */
    public boolean xrefExists(final Xref xref) throws IDMapperException {
        if (xref==null) {
            throw new NullPointerException();
        }

        read();

        return mapXrefs.containsKey(xref);
    }

    private boolean isConnected = true;
    // In the case of IDMapperFile, there is no need to discard associated resources.
    public void close() throws IDMapperException { isConnected = false; }
    public boolean isConnected() { return isConnected; }
    
    /**
     *
     * @return capacities of the ID mapper
     */
    public IDMapperCapabilities getCapabilities() {        
        return cap;
    }

    public void read() throws IDMapperException {
        if (mapXrefs==null) {
            reader.read();
            cap.addSrcDataSources(reader.getDataSources());
            cap.addTgtDataSources(reader.getDataSources());
            mapXrefs = reader.getIDMappings();
        }
    }
}

class IDMapperFileCapabilities implements IDMapperCapabilities {
    private boolean freeSearch;
    private final Set<DataSource> srcDataSources;
    private final Set<DataSource> tgtDataSources;

    public IDMapperFileCapabilities() {
        this(false);
    }

    public IDMapperFileCapabilities(final boolean freeSearch) {
        this.freeSearch = freeSearch;
        srcDataSources = new HashSet();
        tgtDataSources = new HashSet();
    }

    public void setFreeSearchSupported(final boolean freeSearch) {
        this.freeSearch = freeSearch;
    }

    public void addSrcDataSources(final Set<DataSource> dataSources) {
        if (dataSources==null) {
            throw new NullPointerException();
        }
        srcDataSources.addAll(dataSources);
    }

    public void addTgtDataSources(final Set<DataSource> dataSources) {
        if (dataSources==null) {
            throw new NullPointerException();
        }
        tgtDataSources.addAll(dataSources);
    }

    public boolean isFreeSearchSupported() {
        return freeSearch;
    }

    public Set<DataSource> getSupportedSrcDataSources() {
        return srcDataSources;
    }

    public Set<DataSource> getSupportedTgtDataSources() {
        return tgtDataSources;
    }    
}
