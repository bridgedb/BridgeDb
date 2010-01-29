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

package org.bridgedb.webservice.biomart;

import java.io.IOException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bridgedb.AbstractIDMapperCapabilities;
import org.bridgedb.AttributeMapper;
import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.impl.InternalUtils;
import org.bridgedb.webservice.IDMapperWebservice;
import org.bridgedb.webservice.biomart.util.BiomartClient;

/**
 *
 * @author gjj
 */
public class IDMapperBiomart extends IDMapperWebservice implements AttributeMapper {

    static {
		BridgeDb.register ("idmapper-biomart", new Driver());
	}

	private final static class Driver implements org.bridgedb.Driver {
        /** private constructor to prevent outside instantiation. */
		private Driver() { } 

        /** {@inheritDoc} */
        public IDMapper connect(String location) throws IDMapperException  {
            // e.g.: dataset=oanatinus_gene_ensembl
            // e.g.: http://www.biomart.org/biomart/martservice?mart=ensembl&dataset=hsapiens_gene_ensembl
            String baseURL = BiomartClient.DEFAULT_BASE_URL;

            Map<String, String> args = 
            	InternalUtils.parseLocation(location, "mart", "dataset");
            
            if (args.containsKey("BASE"))
            {
            	baseURL = args.get("BASE");
            }
            
            // may be null if unspecified.
            String mart = args.get("mart");
            
            // may be null if unspecified.
            String dataset = args.get("dataset");

            return new IDMapperBiomart(mart, dataset, baseURL);
        }
    }

    private String mart;
    private String dataset;
    private BiomartStub stub;

    private String baseURL;

    private Set<DataSource> supportedSrcDs;
    private Set<DataSource> supportedTgtDs;

    /**
     * Use default url of BiMart.
     * @param mart name of mart
     * @param dataset name of dataset
     * @throws IDMapperException if failed to link to the dataset
     */
    public IDMapperBiomart(String mart, String dataset)
            throws IDMapperException {
        this(mart, dataset, null);
    }

    /**
     * Construct from a dataset, a database, id-only option and transitivity
     * option.
     * @param mart name of mart
     * @param dataset name of dataset
     * @param baseURL base url of BioMart
     * @throws IDMapperException if failed to link to the dataset
     */
    public IDMapperBiomart(String mart, String dataset, String baseURL)
            throws IDMapperException {
        this.mart = mart;
        this.dataset = dataset;
        if (baseURL!=null) {
            this.baseURL = baseURL;
        } else {
            this.baseURL = BiomartClient.DEFAULT_BASE_URL;
        }

        try {
            stub = BiomartStub.getInstance(this.baseURL);
        } catch (IOException e) {
            throw new IDMapperException(e);
        }

        if (!stub.availableMarts().contains(mart)) {
            throw new IDMapperException("Mart " + mart + " doesn't exist.");
        }

        if (!stub.availableDatasets(mart).contains(dataset)) {
            throw new IDMapperException("dataset not exist.");
        }

        supportedSrcDs = this.getSupportedSrcDataSources();
        supportedTgtDs = this.getSupportedTgtDataSources();
        
        cap = new BiomartCapabilities();
    }

    /**
     *
     * @return base URL of BioMart.
     */
    public String getBaseURL() {
        return baseURL;
    }

    /**
     *
     * @return mart name
     */
    public String getMart() {
        return mart;
    }

    /**
     * Get dataset.
     * @return dataset from BioMart
     */
    public String getDataset() {
        return dataset;
    }

    /**
     *
     * @return URL of the dataset
     */
    public String toString() {
        return getBaseURL()+"?dataset="+getDataset();
    }

    /**
     * {@inheritDoc}
     */
    public Map<Xref, Set<Xref>> mapID(Collection<Xref> srcXrefs,
                DataSource... tgtDataSources) throws IDMapperException 
    {
        if (srcXrefs==null)
            throw new NullPointerException("srcXrefs cannot be null.");

        Map<Xref, Set<Xref>> result = new HashMap<Xref, Set<Xref>>();

        // source datasources
        // first key: full name of src Datasource
        // second key: id of xref
        // third key: xref itself
        Map<String, Map<String, Xref>> mapSrcTypeIDXrefs = 
        	new HashMap<String, Map<String, Xref>>();
        
        
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
        Set<String> tgtTypes = new HashSet<String>();
        if (tgtDataSources.length > 0)
        	for (DataSource ds : tgtDataSources) 
        	{
        		if (supportedTgtDs.contains(ds)) {
        			tgtTypes.add(ds.getFullName());
        		}
        	}
        else
        	throw new UnsupportedOperationException("For idmapper-biomart, you have to specify at least one target DataSource");
        String[] tgts = tgtTypes.toArray(new String[0]);

        for (Map.Entry<String, Map<String, Xref>> entry :
                mapSrcTypeIDXrefs.entrySet()) {
            String src = entry.getKey();
            Set<String> ids = entry.getValue().keySet();
            Map<String,Set<String>[]> res =
                        stub.translate(mart, dataset , src, tgts, ids);


            for (Map.Entry<String,Set<String>[]> entryRes : res.entrySet()) {
                String srcId = entryRes.getKey();
                Set<String>[] tgtIds = entryRes.getValue();
                if (tgtIds==null) { // source xref not exist
                    continue;
                }

                Xref srcXref = mapSrcTypeIDXrefs.get(src).get(srcId);
                
                Set<Xref> tgtXrefs = new HashSet<Xref>();
                for (int itgt=0; itgt<tgts.length; itgt++) {
                    for (String tgtId : tgtIds[itgt]) {
                        Xref tgtXref = new Xref(tgtId,
                                DataSource.getByFullName(tgts[itgt]));
                        tgtXrefs.add(tgtXref);
                    }
                }
                
                result.put(srcXref, tgtXrefs);
            }
        }        

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public boolean xrefExists(Xref xref) throws IDMapperException 
    {
    	// We query specifically for ensembl_gene_id etc.
    	// Biomart doesn't like querying for everything
        Set<Xref> set = mapID(xref, 
        		DataSource.getByFullName("ensembl_gene_id"),
        		DataSource.getByFullName("ensembl_transcript_id"));
        return !set.isEmpty();
    }

    /**
     * free text search is not supported for BioMart-based IDMapper.
     * {@inheritDoc}
     */
    public Set<Xref> freeSearch (String text, int limit)
            throws IDMapperException {
        throw new UnsupportedOperationException();
    }

    /**
     * Get supported source data sources.
     * @return supported source data sources
     * @throws IDMapperException if failed to read the filters
     */
    protected Set<DataSource> getSupportedSrcDataSources()
            throws IDMapperException {
        Set<DataSource> dss = new HashSet<DataSource>();
        Set<String> filters = stub.availableSrcIDTypes(mart, dataset);
        for (String filter : filters) {
            DataSource ds = DataSource.getByFullName(filter);
            if (ds!=null) {
                dss.add(ds);
            }
        }
        return dss;
    }

    /**
     * Get supported target data sources.
     * @return supported target data sources
     * @throws IDMapperException if failed to read the filters
     */
    protected Set<DataSource> getSupportedTgtDataSources() 
            throws IDMapperException {
        Set<DataSource> dss = new HashSet<DataSource>();
        Set<String> types = stub.availableTgtIDTypes(mart, dataset);

        for (String type : types) {
            DataSource ds = DataSource.getByFullName(type);
            if (ds!=null) {
                dss.add(ds);
            }
        }
        return dss;
    }

    private final IDMapperCapabilities cap;

    private class BiomartCapabilities extends AbstractIDMapperCapabilities
    {
            /** default constructor.
             * @throws IDMapperException when database is not available */
            public BiomartCapabilities() throws IDMapperException
            {
                    super (null, false, null);
            }

        /** {@inheritDoc} */
        public Set<DataSource> getSupportedSrcDataSources() throws IDMapperException {
            return IDMapperBiomart.this.supportedSrcDs;
        }

        /** {@inheritDoc} */
        public Set<DataSource> getSupportedTgtDataSources() throws IDMapperException {
            return IDMapperBiomart.this.supportedTgtDs;
        }

    }

    /**
     * {@inheritDoc}
     */
    public IDMapperCapabilities getCapabilities() {
        return cap;
    }

    private boolean isConnected = true;
 
    // In the case of IDMapperBioMart, there is no need to discard associated resources.
    /**
     * {@inheritDoc}
     */
    public void close() throws IDMapperException { isConnected = false; }
    /**
     * {@inheritDoc}
     */
    public boolean isConnected() { return isConnected; }

    /**
     * {@inheritDoc}
     */
    public Set<String> getAttributes(Xref ref, String attrType) throws IDMapperException {
        if (ref==null || attrType==null) {
            return Collections.emptySet();
        }

        String srcType = ref.getDataSource().getFullName();
        if (srcType == null) return Collections.emptySet();
        
        String[] tgtTypes = new String[]{attrType};
        Set<String> srcIds = new HashSet<String>(1);
        srcIds.add(ref.getId());

        Map<String,Set<String>[]> map = stub.translate(mart, dataset, srcType, tgtTypes, srcIds);
        Set<String>[] sets = map.get(ref.getId());
        if (sets==null || sets.length==0)
            return null;

        return sets[0];
    }

    /**
     * {@inheritDoc}
     */
    public Map<Xref, String> freeAttributeSearch (String query, String attrType, int limit) throws IDMapperException {
        throw new UnsupportedOperationException("Free attribute search not supported.");
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getAttributeSet() throws IDMapperException {
        return stub.availableTgtAttributes(mart, dataset);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Set<String>> getAttributes(Xref ref) throws IDMapperException {
        Map<String, Set<String>> map = new HashMap<String, Set<String>>();
        for (String attr : getAttributeSet()) {
            Set<String> attrs = getAttributes(ref, attr);
            if (attrs!=null) {
                map.put(attr, attrs);
            }
        }
        return map;
    }
}
