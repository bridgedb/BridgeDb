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
import org.bridgedb.Xref;
import org.bridgedb.webservice.IDMapperWebservice;
import org.bridgedb.webservice.biomart.util.BiomartClient;

/**
 *
 * @author gjj
 */
public class IDMapperBiomart extends IDMapperWebservice {

    static {
		BridgeDb.register ("idmapper-biomart", new Driver());
	}

	private final static class Driver implements org.bridgedb.Driver {
        /** private constructor to prevent outside instantiation. */
		private Driver() { } 

        /** {@inheritDoc} */
        public IDMapper connect(String location) throws IDMapperException  {
            // e.g.: id-type-filter=true@dataset=oanatinus_gene_ensembl
            // e.g.: http://www.biomart.org/biomart/martservice?dataset=oanatinus_gene_ensembl
            String baseURL = BiomartClient.defaultBaseURL;
            boolean idTypeFilter = true;
            
            String config = null;
            String path = location;
            int idx = location.indexOf("@");
            if (idx==0) {
                path =location.substring(idx+1);
            } else if (idx>0) {
                config = location.substring(0, idx);
                path = location.substring(idx+1);
            }

            if (config!=null) {
                String idTypeFilterTag = "id-type-filter=";
                idx = config.indexOf(idTypeFilterTag);
                if (idx!=-1) {
                    String filter = config.substring(idx+idTypeFilterTag.length());
                    if (filter.toLowerCase().startsWith("true")) {
                        idTypeFilter = true;
                    } else if (filter.toLowerCase().startsWith("false")) {
                        idTypeFilter = false;
                    } else {
                        throw new IDMapperException(
                                "id-type-filter can only be true or false");
                    }
                }
            }

            String param = path;
            idx = path.indexOf('?');
            if (idx>-1) {
                baseURL = path.substring(0,idx);
                param = path.substring(idx+1);
            }

            String martTag = "mart=";
            idx = param.indexOf(martTag);
            String mart = param.substring(idx+martTag.length());

            idx = mart.indexOf("&");
            if (idx>-1) {
                mart = mart.substring(0,idx);
            }

            String datasetTag = "dataset=";
            idx = param.indexOf(datasetTag);
            String dataset = param.substring(idx+datasetTag.length());

            idx = dataset.indexOf("&");
            if (idx>-1) {
                dataset = dataset.substring(0,idx);
            }

            return new IDMapperBiomart(mart, dataset, baseURL,
                    idTypeFilter);
        }
    }

    private String mart;
    private String dataset;
    private BiomartStub stub;
    private boolean idOnlyForTgtDataSource;

    private String baseURL;

    private Set<DataSource> supportedSrcDs;
    private Set<DataSource> supportedTgtDs;

    /**
     * Transitivity is unsupported.ID only. ID only for target data sources.
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
     * Use default url of BiMart.
     * @param mart name of mart
     * @param dataset name of dataset
     * @param idOnlyForTgtDataSource id-only option, filter data source ends
     *        with 'ID' or 'Accession'.
     * @param transitivity transitivity option
     * @throws IDMapperException if failed to link to the dataset
     */
    public IDMapperBiomart(String mart, String dataset,
            boolean idOnlyForTgtDataSource) throws IDMapperException {
        this(mart, dataset, null, idOnlyForTgtDataSource);
    }

    /**
     * Transitivity is unsupported.ID only. ID only for target data sources.
     * @param mart name of mart
     * @param dataset name of dataset
     * @param baseURL base url of BioMart
     * @throws IDMapperException if failed to link to the dataset
     */
    public IDMapperBiomart(String mart, String dataset, String baseURL)
                throws IDMapperException {
        this(mart, dataset, baseURL, true);
    }

    /**
     * Construct from a dataset, a database, id-only option and transitivity
     * option.
     * @param mart name of mart
     * @param dataset name of dataset
     * @param baseURL base url of BioMart
     * @param idOnlyForTgtDataSource id-only option, filter data source ends
     *        with 'ID' or 'Accession'.
     * @param transitivity transitivity option
     * @throws IDMapperException if failed to link to the dataset
     */
    public IDMapperBiomart(String mart, String dataset, String baseURL,
            boolean idOnlyForTgtDataSource) throws IDMapperException {
        this.mart = mart;
        this.dataset = dataset;
        if (baseURL!=null) {
            this.baseURL = baseURL;
        } else {
            this.baseURL = BiomartClient.defaultBaseURL;
        }

        try {
            stub = BiomartStub.getInstance(this.baseURL);
        } catch (IOException e) {
            throw new IDMapperException(e);
        }

        if (!stub.availableMarts().contains(mart)) {
            throw new IDMapperException("Mart not exist.");
        }

        if (!stub.availableDatasets(mart).contains(dataset)) {
            throw new IDMapperException("dataset not exist.");
        }

        this.idOnlyForTgtDataSource = idOnlyForTgtDataSource;

        supportedSrcDs = this.getSupportedSrcDataSources();
        supportedTgtDs = this.getSupportedTgtDataSources();
        
        cap = new BiomartCapabilities();
    }

    /**
     *
     * @return true if ID-only for target data sources.
     */
    public boolean getIDOnlyForTgtDataSource() {
        return idOnlyForTgtDataSource;
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
    public Map<Xref, Set<Xref>> mapID(Set<Xref> srcXrefs,
                Set<DataSource> tgtDataSources) throws IDMapperException {
        if (srcXrefs==null) {
            throw new java.lang.IllegalArgumentException(
                        "srcXrefs or tgtDataSources cannot be null.");
        }

        Map<Xref, Set<Xref>> result = new HashMap<Xref, Set<Xref>>();

        // source datasources
        Map<String, Map<String, Xref>> mapSrcTypeIDXrefs = new HashMap();
        for (Xref xref : srcXrefs) {
            DataSource ds = xref.getDataSource();
            if (!supportedSrcDs.contains(ds)) continue;

            String src = ds.getFullName();
            Map<String, Xref> ids = mapSrcTypeIDXrefs.get(src);
            if (ids==null) {
                ids = new HashMap();
                mapSrcTypeIDXrefs.put(src, ids);
            }
            ids.put(xref.getId(), xref);
        }

        // supported tgt datasources
        Set<String> tgtTypes = new HashSet();
        for (DataSource ds : tgtDataSources) {
            if (supportedTgtDs.contains(ds)) {
                tgtTypes.add(ds.getFullName());
            }
        }
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
                
                Set<Xref> tgtXrefs = new HashSet();
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
    public boolean xrefExists(Xref xref) throws IDMapperException {
        Set<Xref> srcXrefs = new HashSet(1);
        srcXrefs.add(xref);

        Set<DataSource> tgtDataSources = new HashSet();

        Map<Xref, Set<Xref>> map = mapID(srcXrefs, tgtDataSources);
        
        return map.isEmpty();
    }

    /**
     * free text search is not supported for BioMart-based IDMapper.
     */
    public Set<Xref> freeSearch (String text, int limit)
            throws IDMapperException {
        throw new UnsupportedOperationException();
    }

    /**
     * Get supported source data sources
     * @return supported source data sources
     * @throws IOException if failed to read the filters
     */
    protected Set<DataSource> getSupportedSrcDataSources()
            throws IDMapperException {
        Set<DataSource> dss = new HashSet();
        Set<String> filters = stub.availableFilters(mart, dataset);
        for (String filter : filters) {
            DataSource ds = DataSource.getByFullName(filter);
            if (ds!=null) {
                dss.add(ds);
            }
        }
        return dss;
    }

    /**
     * Get supported target data sources
     * @return supported target data sources
     * @throws IOException if failed to read the filters
     */
    protected Set<DataSource> getSupportedTgtDataSources() 
            throws IDMapperException {
        Set<DataSource> dss = new HashSet();
        Set<String> attributes = stub.availableAttributes(mart, dataset,
                idOnlyForTgtDataSource);

        for (String attr : attributes) {
            DataSource ds = DataSource.getByFullName(attr);
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
    public void close() throws IDMapperException { isConnected = false; }
    public boolean isConnected() { return isConnected; }

}
