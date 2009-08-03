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

package org.bridgedb.webservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.bridgedb.AbstractIDMapperCapabilities;
import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.Xref;
import org.bridgedb.file.IDMappingReaderFromDelimitedReader;
import org.bridgedb.webservice.biomart.Attribute;
import org.bridgedb.webservice.biomart.BiomartStub;
import org.bridgedb.webservice.biomart.Filter;
import org.bridgedb.webservice.biomart.XMLQueryBuilder;

/**
 *
 * @author gjj
 */
public class IDMapperBiomart extends IDMapperWebservice {

    static {
		BridgeDb.register ("idmapper-biomart", new Driver());
	}

	private static class Driver implements org.bridgedb.Driver {
        private Driver() { } // prevent outside instantiation

        private static final String datasetTag = "dataset=";

		public IDMapper connect(String location) throws IDMapperException  {
            // e.g.: dataset=oanatinus_gene_ensembl
			// e.g.: http://www.biomart.org/biomart/martservice?dataset=oanatinus_gene_ensembl
            String baseURL = BiomartStub.defaultBaseURL;
            String param = location;
            int idx = location.indexOf('?');
            if (idx>-1) {
                baseURL = location.substring(0,idx);
                param = location.substring(idx+1);
            }

            idx = param.indexOf(datasetTag);
            String datasetName = param.substring(idx+datasetTag.length());

            idx = datasetName.indexOf("&");
            if (idx>-1) {
                datasetName = datasetName.substring(0,idx);
            }

            return new IDMapperBiomart(datasetName, baseURL);
		}
	}

    protected String datasetName;
    protected BiomartStub stub;
    protected boolean transitivity;
    protected boolean idOnlyForTgtDataSource;

    protected String baseURL;

    private Map<DataSource, Filter> mapSrcDSFilter;
    private Map<DataSource, Attribute> mapSrcDSAttr;

    /**
     * Transitivity is unsupported.ID only. ID only for target data sources.
     * Use default url of BiMart.
     * @param datasetName name of dataset
     * @throws IDMapperException if failed to link to the dataset
     */
    public IDMapperBiomart(String datasetName) throws IDMapperException {
        this(datasetName, null);
    }

    /**
     * Use default url of BiMart.
     * @param datasetName name of dataset
     * @param idOnlyForTgtDataSource id-only option, filter data source ends
     *        with 'ID' or 'Accession'.
     * @param transitivity transitivity option
     * @throws IDMapperException if failed to link to the dataset
     */
    public IDMapperBiomart(String datasetName, boolean idOnlyForTgtDataSource,
                boolean transitivity) throws IDMapperException {
        this(datasetName, null, idOnlyForTgtDataSource, transitivity);
    }

    /**
     * Transitivity is unsupported.ID only. ID only for target data sources.
     * @param datasetName name of dataset
     * @param baseURL base url of BioMart
     * @throws IDMapperException if failed to link to the dataset
     */
    public IDMapperBiomart(String datasetName, String baseURL)
                throws IDMapperException {
        this(datasetName, baseURL, true);
    }

    /**
     * Transitivity is unsupported.
     * @param datasetName name of dataset
     * @param baseURL base url of BioMart
     * @param idOnlyForTgtDataSource id-only option, filter data source ends
     *        with 'ID' or 'Accession'.
     * @throws IDMapperException if failed to link to the dataset
     */
    public IDMapperBiomart(String datasetName, String baseURL,
            boolean idOnlyForTgtDataSource) throws IDMapperException {
        this(datasetName, baseURL, idOnlyForTgtDataSource, false);
    }

    /**
     * Construct from a dataset, a database, id-only option and transitivity
     * option.
     * @param datasetName name of dataset
     * @param baseURL base url of BioMart
     * @param idOnlyForTgtDataSource id-only option, filter data source ends
     *        with 'ID' or 'Accession'.
     * @param transitivity transitivity option
     * @throws IDMapperException if failed to link to the dataset
     */
    public IDMapperBiomart(String datasetName, String baseURL, 
            boolean idOnlyForTgtDataSource, boolean transitivity) throws IDMapperException {
        this.datasetName = datasetName;
        if (baseURL!=null) {
            this.baseURL = baseURL;
        } else {
            this.baseURL = BiomartStub.defaultBaseURL;
        }

        try {
            stub = BiomartStub.getInstance(this.baseURL);
        } catch (IOException e) {
            throw new IDMapperException(e);
        }
        
        setIDOnlyForTgtDataSource(idOnlyForTgtDataSource);
        setTransitivity(transitivity);

        mapSrcDSFilter = new HashMap();
        mapSrcDSAttr = new HashMap();
        
        cap = new BiomartCapabilities();
    }

    /**
     * Filter target datasource ending with "ID" or "Accession".
     * @param idOnlyForTgtDataSource ID-only if true
     */
    public void setIDOnlyForTgtDataSource(boolean idOnlyForTgtDataSource) {
        this.idOnlyForTgtDataSource = idOnlyForTgtDataSource;
    }

    /**
     *
     * @return true if ID-only for target data sources.
     */
    public boolean getIDOnlyForTgtDataSource() {
        return idOnlyForTgtDataSource;
    }

    /**
     * Set transitivity support.
     * @param transitivity support transitivity if true.
     */
    public void setTransitivity(final boolean transitivity) {
        this.transitivity = transitivity;
    }

    /**
     * Get transitivity support.
     * @return true if support transitivity; false otherwise.
     */
    public boolean getTransitivity() {
        return transitivity;
    }

    /**
     * Set base url of BioMart.
     * @param baseURL {@link URL} of BioMart.
     * @throws IDMapperException if failed to read local resources.
     */
    public void setBaseURL(final String baseURL) throws IDMapperException {
        try {
            stub = BiomartStub.getInstance(baseURL);
        } catch (IOException e) {
            throw new IDMapperException(e);
        }
        this.baseURL = baseURL;
    }

    /**
     *
     * @return base {@link URL} of BioMart.
     */
    public String getBaseURL() {
        return baseURL;
    }

    /**
     * Set dataset.
     * @param dataset dataset from BioMart
     */
    public void setDataset(final String dataset) {
        this.datasetName = dataset;
    }

    /**
     * Get dataset.
     * @return dataset from BioMart
     */
    public String getDataset() {
        return datasetName;
    }

    public String toString() {
        return getBaseURL()+"?dataset="+getDataset();
    }

    /**
     * {@inheritDoc}
     */
    public Map<Xref, Set<Xref>> mapID(Set<Xref> srcXrefs, Set<DataSource> tgtDataSources) throws IDMapperException {
        if (srcXrefs==null || tgtDataSources==null) {
            throw new java.lang.IllegalArgumentException("srcXrefs or tgtDataSources cannot be null");
        }

        Map<Xref, Set<Xref>> result = new HashMap();

        // remove unsupported source datasources
        Set<DataSource> supportedSrcDatasources = cap.getSupportedSrcDataSources();
        Map<DataSource, String> queryFilters = getQueryFilters(srcXrefs);
        Iterator<DataSource> it = queryFilters.keySet().iterator();
        while (it.hasNext()) {
            DataSource ds = it.next();
            if (!supportedSrcDatasources.contains(ds)) {
                it.remove();
            }
        }
        if (queryFilters.isEmpty()) {
            return result;
        }

        // remove unsupported target datasources
        Set<DataSource> supportedTgtDatasources = cap.getSupportedTgtDataSources();
        Vector<DataSource> tgtDss = new Vector(tgtDataSources);
        tgtDss.retainAll(supportedTgtDatasources);
        if (tgtDss.isEmpty()) {
            return result;
        }

        for (Map.Entry<DataSource, String> filter : queryFilters.entrySet()) {
            DataSource srcDs = filter.getKey();

            String srcAttr = mapSrcDSFilter.get(srcDs).getName();
            Attribute[] attrs = getAttributes(tgtDss, srcAttr);

            Map<String, String> queryFilter = new HashMap(1);
            queryFilter.put(srcAttr, filter.getValue());

            String query = XMLQueryBuilder.getQueryString(datasetName, attrs, queryFilter);

            BufferedReader bfr = null;
            try {
                bfr = stub.sendQuery(query);
                if (!bfr.ready())
                    throw new IDMapperException("Query failed");
            } catch (IOException e) {
                throw new IDMapperException(e);
            }

            if (bfr==null) {
                return result;
            }
            
            IDMappingReaderFromDelimitedReader reader
                    = new IDMappingReaderFromDelimitedReader(bfr,
                                "\\t", null, transitivity);

            Vector<DataSource> dss = new Vector(tgtDss.size()+1);
            dss.addAll(tgtDss);
            dss.add(srcDs);
            reader.setDataSources(dss);
            
            Map<Xref,Set<Xref>> mapXrefs = reader.getIDMappings();
            if (mapXrefs==null) {
                return result;
            }

            for (Xref srcXref : srcXrefs) {
                Set<Xref> refs = mapXrefs.get(srcXref);
                if (refs==null) continue;

                Set<Xref> tgtRefs = result.get(srcXref);
                if (tgtRefs==null) {
                    tgtRefs = new HashSet();
                    result.put(srcXref, tgtRefs);
                }

                for (Xref tgtXref : refs) {
                    if (tgtDataSources.contains(tgtXref.getDataSource())) {
                        tgtRefs.add(tgtXref);
                    }
                }
            }
        }

        return result;
    }

    /**
     * Create filters from the source xrefs
     * @param srcXrefs
     * @return map from data source to IDs
     */
    protected Map<DataSource,String> getQueryFilters(Set<Xref> srcXrefs) {
        Map<DataSource, Set<String>> mapNameValue = new HashMap();
        for (Xref xref : srcXrefs) {
            DataSource ds = xref.getDataSource();
            Set<String> ids = mapNameValue.get(ds);
            if (ids==null) {
                ids = new HashSet();
                mapNameValue.put(ds, ids);
            }
            ids.add(xref.getId());
        }

        Map<DataSource,String> filters = new HashMap();
        for (Map.Entry<DataSource, Set<String>> entry : mapNameValue.entrySet()) {
            DataSource ds = entry.getKey();
            StringBuilder value = new StringBuilder();
            for (String str : entry.getValue()) {
                value.append(str);
                value.append(",");
            }

            int len = value.length();
            if (len>0) {
                value.deleteCharAt(len-1);
            }

            filters.put(ds, value.toString());
        }

        return filters;
    }

    /**
     * This code is bollowed from IDMapperClient from Cytoscape.
     * @param tgtDataSources
     * @return attributes
     */
    protected Attribute[] getAttributes(Vector<DataSource> tgtDataSources, 
            String filterName) {
        int n = tgtDataSources.size();
        Attribute[] attrs = new Attribute[n+1];

        int iattr = 0;
        for (DataSource ds : tgtDataSources) {
            //attrs[iattr++] = new Attribute(ds.getFullName());
            attrs[iattr++] = this.mapSrcDSAttr.get(ds);
        }

        // Database-specific modification.
        // This is not the best way, but cannot provide universal solution.
        Attribute attr;
        if (datasetName.contains("REACTOME")) {
            attr = stub.filterToAttributeName(datasetName, "REACTOME", filterName);
        } else if (datasetName.contains("UNIPROT")) {
            attr = stub.filterToAttributeName(datasetName, "UNIPROT", filterName);
        } else if (datasetName.contains("VARIATION")) {
            attr = stub.getAttribute(datasetName, filterName + "_stable_id");
        } else {
            attr = stub.getAttribute(datasetName, filterName);
        }

        attrs[n] = attr;

        return attrs;
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
     * free text search is not supported for BioMart-based IDMapper
     */
    public Set<Xref> freeSearch (String text, int limit) throws IDMapperException {
        throw new UnsupportedOperationException();
    }

    protected Set<DataSource> getSupportedSrcDataSources() throws IOException {
        Set<DataSource> dss = new HashSet();
        Map<String, Filter> filters = stub.getFilters(datasetName);
        for (Filter filter : filters.values()) {
            //String fullName = filter.getDisplayName()+" ("+filter.getName()+")";
            String fullName = filter.getDisplayName();
            if (fullName.endsWith("(s)")) {
                fullName = fullName.substring(0, fullName.length()-3);
            }
            //TODO: mapping to bridgedb system code
            DataSource ds = DataSource.getByFullName(fullName);
            dss.add(ds);
            mapSrcDSFilter.put(ds, filter);
        }
        return dss;
    }

    protected Set<DataSource> getSupportedTgtDataSources() throws IOException {
        Map<String, Attribute> attributeVals = stub.getAttributes(datasetName);
        Set<DataSource> dss = new HashSet();
        for (Attribute attr : attributeVals.values()) {
            String displayName = attr.getDisplayName();
            String name = attr.getName();
            if (idOnlyForTgtDataSource) {
                if (!displayName.endsWith("ID")
                        && !displayName.endsWith("Accession")
                        && !name.endsWith("id")
                        && !name.endsWith("accession")) {
                    continue;
                }
            }
            //String fullName = displayName + " ("+name+")";
            String fullName = displayName;
            //TODO: mapping to bridgedb system code
            DataSource ds = DataSource.getByFullName(fullName);
            dss.add(ds);
            mapSrcDSAttr.put(ds, attr);
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
        try {
            return IDMapperBiomart.this.getSupportedSrcDataSources();
        } catch (IOException ex) {
            throw new IDMapperException(ex);
        }
        }

        /** {@inheritDoc} */
        public Set<DataSource> getSupportedTgtDataSources() throws IDMapperException {
            try {
            return IDMapperBiomart.this.getSupportedTgtDataSources();
        } catch (IOException ex) {
            throw new IDMapperException(ex);
        }
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
