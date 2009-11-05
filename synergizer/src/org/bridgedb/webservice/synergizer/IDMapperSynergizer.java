// BridgeDb,
// An abstraction layer for identifer mapping services, both local and online.
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
package org.bridgedb.webservice.synergizer;

import java.io.IOException;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bridgedb.AbstractIDMapperCapabilities;
import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.impl.InternalUtils;
import org.bridgedb.webservice.IDMapperWebservice;
import org.bridgedb.Xref;

/**
 * 
 * @author gjj
 */
public class IDMapperSynergizer extends IDMapperWebservice
{
    static 
    {
        BridgeDb.register ("idmapper-synergizer", new Driver());
    }

    private SynergizerStub stub;
    private String baseUrl;
    private String authority;
    private String species;

    private Set<DataSource> supportedSrcDs;
    private Set<DataSource> supportedTgtDs;
    private Map<DataSource, Set<DataSource>> mapSrcDsTgtDs;

    /**
     * Constructor with the default server url.
     * @param authority authority name
     * @param species species name
     * @throws IDMapperException if failed to connect.
     */
    public IDMapperSynergizer(String authority, String species)
            throws IDMapperException {
         this(authority, species, SynergizerStub.defaultBaseURL);
    }

    /**
     *
     * @param authority authority name
     * @param species species name
     * @param baseUrl server url
     * @throws IDMapperException if failed to connect.
     */
    public IDMapperSynergizer(String authority, String species, String baseUrl)
            throws IDMapperException {
        this.baseUrl = baseUrl;
        this.authority = authority;
        this.species = species;

        init();
    }

    public void init() throws IDMapperException {
        try {
            stub = SynergizerStub.getInstance(baseUrl);
        } catch(IOException e) {
            throw new IDMapperException(e);
        }

        supportedSrcDs = this.getSupportedSrcDataSources();
        mapSrcDsTgtDs = this.getMapSrcTgt();
        supportedTgtDs = new HashSet<DataSource>();
        for (Set<DataSource> dss : mapSrcDsTgtDs.values()) {
            supportedTgtDs.addAll(dss);
        }
    }

    /**
     *
     * @return server url
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     *
     * @return authority
     */
    public String getAuthority() {
        return authority;
    }

    /**
     *
     * @return species
     */
    public String getSpecies() {
        return species;
    }

    private static class Driver implements org.bridgedb.Driver {
        private Driver() { } // prevent outside instantiation

        public IDMapper connect(String location) throws IDMapperException {
            // e.g.: ?authority=ensembl&species=Homo sapiens
            String baseURL = SynergizerStub.defaultBaseURL;

            Map<String, String> info = 
            	InternalUtils.parseLocation(location, "authority", "species");
            
            if (info.containsKey("BASE"))
            {
            	baseURL = info.get("BASE");
        	}
            // could be null
            String authority = info.get ("authority");
            // could be null
            String species = info.get ("species");
            return new IDMapperSynergizer(authority, species, baseURL);

        }
    }
	
    private boolean closed = false;
    public void close() throws IDMapperException {
        closed = true;
    }

    private Set<DataSource> getSupportedSrcDataSources()
            throws IDMapperException {
        Set<DataSource> dss = new HashSet<DataSource>();
        Set<String> domains = stub.availableDomains(authority, species);
        for (String domain : domains) {
            dss.add(DataSource.getByFullName(domain));
        }
        return dss;
    }

    private Map<DataSource, Set<DataSource>> getMapSrcTgt()
            throws IDMapperException {
        Map<DataSource, Set<DataSource>> map = new HashMap<DataSource, Set<DataSource>>();
        Set<String> domains = stub.availableDomains(authority, species);
        for (String domain : domains) {
            DataSource src = DataSource.getByFullName(domain);
            Set<String> ranges = stub.availableRanges(authority, species, domain);
            Set<DataSource> tgts = new HashSet<DataSource>();
            for (String range : ranges) {
                tgts.add(DataSource.getByFullName(range));
            }
            map.put(src, tgts);
        }

        return map;
    }

    /**
     * free search is not supported
     */
    public Set<Xref> freeSearch(String text, int limit)
            throws IDMapperException {
        throw new UnsupportedOperationException();
    }

    private class SynergizerCapabilities extends AbstractIDMapperCapabilities
    {
        /** default constructor.
         * @throws IDMapperException when database is not available */
        public SynergizerCapabilities() throws IDMapperException
        {
                super (null, false, null);
        }

        /** {@inheritDoc} */
        public Set<DataSource> getSupportedSrcDataSources() throws IDMapperException {
            return IDMapperSynergizer.this.supportedSrcDs;
        }

        /** {@inheritDoc} */
        public Set<DataSource> getSupportedTgtDataSources() throws IDMapperException {
            return IDMapperSynergizer.this.supportedTgtDs;
        }

        /** {@inheritDoc} */
        public boolean isMappingSupported(DataSource src, DataSource tgt)
			throws IDMapperException  {
            if (src==null || tgt==null) {
                return false;
            }
            
            Map<DataSource,Set<DataSource>> map
                    = IDMapperSynergizer.this.mapSrcDsTgtDs;
            return getSupportedSrcDataSources().contains(src)
                    && map.get(src).contains(tgt);
        }

    }
    
    private SynergizerCapabilities caps = new SynergizerCapabilities();
	
    public IDMapperCapabilities getCapabilities() {
        return caps;
    }

    public boolean isConnected() {
        return !closed;
    }

    /**
     * {@inheritDoc}
     */
    public Map<Xref, Set<Xref>> mapID(Collection<Xref> srcXrefs,
                    DataSource... resultDs) throws IDMapperException
    {
        if (srcXrefs==null) {
            throw new NullPointerException(
                        "srcXrefs or tgtDataSources cannot be null.");
        }

        Map<Xref, Set<Xref>> result = new HashMap<Xref, Set<Xref>>();
		Set<DataSource> dsFilter = new HashSet<DataSource>(Arrays.asList(resultDs));

        // source datasources
        Map<String, Map<String, Xref>> mapSrcTypeIDXrefs = new HashMap<String, Map<String, Xref>>();
        for (Xref xref : srcXrefs) {
            DataSource ds = xref.getDataSource();
            if (!supportedSrcDs.contains(ds)) continue;

            String src = ds.getFullName();
            Map<String, Xref> ids = mapSrcTypeIDXrefs.get(src);
            if (ids==null) {
                ids = new HashMap<String, Xref>();
                mapSrcTypeIDXrefs.put(src, ids);
            }
            ids.put(xref.getId(), xref);
        }

        // supported tgt datasources
        Map<String, Set<String>> mapSrcTgt = new HashMap<String, Set<String>>();
        for (Map.Entry<DataSource, Set<DataSource>> entry
                : mapSrcDsTgtDs.entrySet()) {
            String src = entry.getKey().getFullName();
            if (!mapSrcTypeIDXrefs.containsKey(src))
                continue;

            Set<String> set = new HashSet<String>();
            mapSrcTgt.put(src, set);
            for (DataSource ds : entry.getValue()) {
				if (resultDs.length == 0 || dsFilter.contains(ds))
                    set.add(ds.getFullName());
            }
        }

        for (Map.Entry<String, Map<String, Xref>> entry :
                mapSrcTypeIDXrefs.entrySet()) {
            String src = entry.getKey();
            Set<String> ids = entry.getValue().keySet();
            Set<String> tgts = mapSrcTgt.get(src);
            for (String tgt : tgts) {
                Map<String,Set<String>> res =
                        stub.translate(authority, species, src, tgt, ids);
                for (Map.Entry<String,Set<String>> entryRes : res.entrySet()) {
                    String srcId = entryRes.getKey();
                    Set<String> tgtIds = entryRes.getValue();
                    if (tgtIds==null) { // source xref not exist
                        continue;
                    }

                    Xref srcXref = mapSrcTypeIDXrefs.get(src).get(srcId);
                    Set<Xref> tgtXrefs = result.get(srcXref);
                    if (tgtXrefs==null) {
                        tgtXrefs = new HashSet<Xref>();
                        result.put(srcXref, tgtXrefs);
                    }

                    for (String tgtId : tgtIds) {
                        Xref tgtXref = new Xref(tgtId,
                                DataSource.getByFullName(tgt));
                        tgtXrefs.add(tgtXref);
                    }
                }
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public boolean xrefExists(Xref xref) throws IDMapperException {
        if (xref==null)
            return false;

        DataSource ds = xref.getDataSource();
        if (!supportedSrcDs.contains(ds))
            return false;

        String src = ds.getFullName();
        String id = xref.getId();
        return stub.idExist(authority, species, src, id);
    }
}
