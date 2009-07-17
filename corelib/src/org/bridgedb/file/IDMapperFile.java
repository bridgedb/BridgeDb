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

package org.bridgedb.file;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import java.io.IOException;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

/**
 * Interface for ID mapping from files
 * 
 */ 
public abstract class IDMapperFile implements IDMapper {
    protected final IDMapperFileCapabilities cap;
    protected final IDMappingReader reader;
    protected boolean transitivity;

    /**
     * Constuctor from a url. transitivity is unsupported by default.
     * @param url
     * @throws java.io.IOException
     */
    public IDMapperFile(final IDMappingReader reader) {
        this(reader, false);
    }

    /**
     * free search is unsupported by default.
     * @param url
     * @throws java.io.IOException
     */
    public IDMapperFile(final IDMappingReader reader,
            final boolean transitivity) {
        this(reader, transitivity, false);
    }

    /**
     *
     * @param url
     * @param freeSearch
     * @throws IDMapperException when failed to read
     */
    public IDMapperFile(final IDMappingReader reader,
            final boolean transitivity,
            final boolean freeSearch) {
        if (reader==null) {
            throw new NullPointerException();
        }

        this.reader = reader;
        cap = new IDMapperFileCapabilities(freeSearch);

        setTransitivity(transitivity);
    }

    public void setTransitivity(final boolean transitivity) {
        this.transitivity = transitivity;
    }

    public boolean getTransitivity() {
        return transitivity;
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
            throw new java.lang.IllegalArgumentException("srcXrefs or tgtDataSources cannot be null");
        }

        Map<Xref, Set<Xref>> return_this = new HashMap();

        // remove unsupported target datasources
        Set<DataSource> supportedTgtDatasources = cap.getSupportedTgtDataSources();
        Set<DataSource> tgtDss = new HashSet(tgtDataSources);
        tgtDss.retainAll(supportedTgtDatasources);
        if (tgtDss.isEmpty()) {
            return return_this;
        }

        Map<Xref,Set<Xref>> mapXrefs = reader.getIDMappings();
        if (mapXrefs==null) {
            return return_this;
        }

        Set<DataSource> supportedSrcDatasources = cap.getSupportedSrcDataSources();
        for (Xref srcXref : srcXrefs) {
            if (!supportedSrcDatasources.contains(srcXref.getDataSource())) {
                continue;
            }

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

        Map<Xref,Set<Xref>> mapXrefs = reader.getIDMappings();
        if (mapXrefs==null) {
            return false;
        }

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

    protected IDMappingReader getIDMappingReader() {
        return reader;
    }

    private class IDMapperFileCapabilities implements IDMapperCapabilities {
        private boolean freeSearch;

        public IDMapperFileCapabilities() {
            this(false);
        }

        public IDMapperFileCapabilities(final boolean freeSearch) {
            this.freeSearch = freeSearch;
        }

        public void setFreeSearchSupported(final boolean freeSearch) {
            this.freeSearch = freeSearch;
        }

        public boolean isFreeSearchSupported() {
            return freeSearch;
        }

        public Set<DataSource> getSupportedSrcDataSources() throws IDMapperException {
            return IDMapperFile.this.reader.getDataSources();
        }

        public Set<DataSource> getSupportedTgtDataSources() throws IDMapperException {
            return getSupportedSrcDataSources(); //
        }
    }

}

