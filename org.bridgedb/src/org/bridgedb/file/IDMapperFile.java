// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
// Copyright 2006-2009 BridgeDb developers
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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bridgedb.AbstractIDMapperCapabilities;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.impl.InternalUtils;

/**
 * Interface for ID mapping from files.
 * 
 */ 
public abstract class IDMapperFile implements IDMapper {
    protected final IDMapperFileCapabilities cap;
    protected final IDMappingReader reader;

    /**
     * Constuctor from a {@link IDMappingReader}. transitivity is unsupported
     * by default.
     * @param reader ID mapping reader
     * @throws IDMapperException when failed to read
     */
    public IDMapperFile(final IDMappingReader reader) throws IDMapperException
    {
        this(reader, false);
    }

    /**
     * Constuctor from a {@link IDMappingReader} and user-defined free search
     * capacity.
     * @param reader ID mapping reader
     * @param freeSearch if this IDMapper supports free search
     * @throws IDMapperException when failed to read
     * @throws IllegalArgumentException if reader is null
     */
    public IDMapperFile(final IDMappingReader reader,
            final boolean freeSearch) throws IDMapperException
    {
        if (reader==null) {
            throw new IllegalArgumentException("reader cannot be null.");
        }

        this.reader = reader;
        cap = new IDMapperFileCapabilities(freeSearch);
    }

    /**
     * {@inheritDoc}
     */
    public Map<Xref, Set<Xref>> mapID(final Collection<Xref> srcXrefs,
                final DataSource... tgtDataSources) throws IDMapperException {
        if (srcXrefs==null) {
            throw new NullPointerException("argument srcXrefs cannot be null");
        }

        Map<Xref, Set<Xref>> result = new HashMap();

        // remove unsupported target datasources
        Set<DataSource> supportedTgtDatasources = cap.getSupportedTgtDataSources();
        Set<DataSource> tgtDss;
        if (tgtDataSources.length == 0) {
            tgtDss = new HashSet<DataSource>(cap.getSupportedTgtDataSources());
        } else {
            tgtDss = new HashSet<DataSource>(Arrays.asList(tgtDataSources));
            tgtDss.retainAll(supportedTgtDatasources);
        }

        if (tgtDss.isEmpty()) {
            return result;
        }

        Map<Xref,Set<Xref>> mapXrefs = reader.getIDMappings();
        if (mapXrefs==null) {
            return result;
        }

        Set<DataSource> supportedSrcDatasources = cap.getSupportedSrcDataSources();
        for (Xref srcXref : srcXrefs) {
            if (!supportedSrcDatasources.contains(srcXref.getDataSource())) {
                continue;
            }

            Set<Xref> refs = mapXrefs.get(srcXref);
            if (refs==null) continue;
            
            for (Xref tgtXref : refs) 
            {
            	if (tgtDataSources.length == 0 || tgtDss.contains(tgtXref.getDataSource()))
    			{
            		InternalUtils.multiMapPut(result, srcXref, tgtXref);
                }
            }
        }

        return result;
    }

    /** {@inheritDoc} */
    public Set<Xref> mapID(Xref srcXref, DataSource... tgtDataSources) throws IDMapperException {        Map<Xref,Set<Xref>> mapXrefs = reader.getIDMappings();
        Set<Xref> result = new HashSet<Xref>();

        if (mapXrefs==null) {
            return result;
        }

        Set<DataSource> tgtDss = new HashSet<DataSource>(Arrays.asList(tgtDataSources));
        Set<Xref> destRefs = mapXrefs.get(srcXref);
        if (destRefs != null) for (Xref destRef : mapXrefs.get(srcXref))
        {
            if (tgtDataSources.length == 0 || tgtDss.contains(destRef.getDataSource()))
            {
                result.add (destRef);
            }
        }
        
        return result;
    }
	
    /**
     * {@inheritDoc}
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
    // In the case of DataCollection, there is no need to discard associated resources.
    
    /** {@inheritDoc} */
    public void close() throws IDMapperException { isConnected = false; }
    /** {@inheritDoc} */
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

    private class IDMapperFileCapabilities extends AbstractIDMapperCapabilities {

        public IDMapperFileCapabilities(final boolean freeSearch) throws IDMapperException
        {
            super (IDMapperFile.this.reader.getDataSources(), false, null);
        }

    }

}

