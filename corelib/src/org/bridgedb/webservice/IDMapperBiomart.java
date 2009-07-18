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

package org.bridgedb.webservice;

import org.bridgedb.BridgeDb;
import org.bridgedb.IDMapper;
import org.bridgedb.Xref;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.webservice.biomart.BiomartStub;
import org.bridgedb.webservice.biomart.XMLQueryBuilder;
import org.bridgedb.webservice.biomart.Attribute;
import org.bridgedb.webservice.biomart.Filter;
import org.bridgedb.file.IDMappingReaderFromDelimitedReader;

import java.util.Vector;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import java.io.BufferedReader;
import java.io.IOException;
/**
 *
 * @author gjj
 */
public class IDMapperBiomart extends IDMapperWebservice {

    static {
		BridgeDb.register ("idmapper-pgdb", new Driver());
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

    public IDMapperBiomart(String datasetName) throws IDMapperException {
        this(datasetName, null);
    }

    public IDMapperBiomart(String datasetName, boolean idOnlyForTgtDataSource, boolean transitivity) throws IDMapperException {
        this(datasetName, null, idOnlyForTgtDataSource, transitivity);
    }

    public IDMapperBiomart(String datasetName, String baseURL) throws IDMapperException {
        this(datasetName, baseURL, true);
    }

    public IDMapperBiomart(String datasetName, String baseURL,
            boolean idOnlyForTgtDataSource) throws IDMapperException {
        this(datasetName, baseURL, idOnlyForTgtDataSource, false);
    }
    
    public IDMapperBiomart(String datasetName, String baseURL, 
            boolean idOnlyForTgtDataSource, boolean transitivity) throws IDMapperException {
        this.datasetName = datasetName;
        try {
            stub = BiomartStub.getInstance(baseURL);
        } catch (IOException e) {
            throw new IDMapperException(e);
        }
        
        if (baseURL!=null) {
            this.baseURL = baseURL;
        } else {
            this.baseURL = BiomartStub.defaultBaseURL;
        }

        setIDOnlyForTgtDataSource(idOnlyForTgtDataSource);
        setTransitivity(transitivity);

        mapSrcDSFilter = new HashMap();
        mapSrcDSAttr = new HashMap();
    }

    /**
     * Filter target datasource ending with "ID" or "Accession"
     * @param idOnlyForTgtDataSource
     */
    public void setIDOnlyForTgtDataSource(boolean idOnlyForTgtDataSource) {
        this.idOnlyForTgtDataSource = idOnlyForTgtDataSource;
    }

    public boolean getIDOnlyForTgtDataSource() {
        return idOnlyForTgtDataSource;
    }

    public void setTransitivity(final boolean transitivity) {
        this.transitivity = transitivity;
    }

    public boolean getTransitivity() {
        return transitivity;
    }

    public void setBaseURL(final String baseURL) throws IDMapperException {
        try {
            stub = BiomartStub.getInstance(baseURL);
        } catch (IOException e) {
            throw new IDMapperException(e);
        }
        this.baseURL = baseURL;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setDataset(final String dataset) {
        this.datasetName = dataset;
    }
    
    public String getDataset() {
        return datasetName;
    }

    public String toString() {
        return getBaseURL()+"?dataset="+getDataset();
    }

    /**
     * Supports one-to-one mapping and one-to-many mapping.
     * @param srcXrefs source Xref, containing ID and ID type/data source
     * @param tgtDataSources target ID types/data sources
     * @return a map from source Xref to target Xref's
     * @throws IDMapperException if failed
     */
    public Map<Xref, Set<Xref>> mapID(Set<Xref> srcXrefs, Set<DataSource> tgtDataSources) throws IDMapperException {
        if (srcXrefs==null || tgtDataSources==null) {
            throw new java.lang.IllegalArgumentException("srcXrefs or tgtDataSources cannot be null");
        }

        Map<Xref, Set<Xref>> return_this = new HashMap();

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
            return return_this;
        }

        // remove unsupported target datasources
        Set<DataSource> supportedTgtDatasources = cap.getSupportedTgtDataSources();
        Vector<DataSource> tgtDss = new Vector(tgtDataSources);
        tgtDss.retainAll(supportedTgtDatasources);
        if (tgtDss.isEmpty()) {
            return return_this;
        }

        for (Map.Entry<DataSource, String> filter : queryFilters.entrySet()) {
            DataSource srcDs = filter.getKey();

            String srcAttr = mapSrcDSFilter.get(srcDs).getName();
            Attribute[] attrs = getAttributes(tgtDss, srcAttr);

            Map<String, String> queryFilter = new HashMap(1);
            queryFilter.put(srcAttr, filter.getValue());

            String query = XMLQueryBuilder.getQueryString(datasetName, attrs, queryFilter);

            BufferedReader result = null;
            try {
                result = stub.sendQuery(query);
                if (!result.ready())
                    throw new IDMapperException("Query failed");
            } catch (IOException e) {
                throw new IDMapperException(e);
            }

            if (result==null) {
                return return_this;
            }
            
            IDMappingReaderFromDelimitedReader reader
                    = new IDMappingReaderFromDelimitedReader(result,
                                "\\t", null, transitivity);

            Vector<DataSource> dss = new Vector(tgtDss.size()+1);
            dss.addAll(tgtDss);
            dss.add(srcDs);
            reader.setDataSources(dss);
            
            Map<Xref,Set<Xref>> mapXrefs = reader.getIDMappings();
            if (mapXrefs==null) {
                return return_this;
            }

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
        }

        return return_this;
    }

    /**
     * Create filters from the source xrefs
     * @param srcXrefs
     * @return
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
     *
     * @param tgtDataSources
     * @return
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
			//attrs = new Attribute(stub.toAttributeName("REACTOME", filterName));
            attr = stub.filterToAttributeName(datasetName, "REACTOME", filterName);
		} else if (datasetName.contains("UNIPROT")) {
			//System.out.println("UNIPROT found");
			//attrs = new Attribute(stub.toAttributeName("UNIPROT", filterName));
            attr = stub.filterToAttributeName(datasetName, "UNIPROT", filterName);
		} else if (datasetName.contains("VARIATION")) {
//			String newName = filterName.replace("_id", "_stable_id");
//			newName = newName.replace("_ensembl", "");
			//attrs = new Attribute(filterName + "_stable_id");
            attr = stub.getAttribute(datasetName, filterName + "_stable_id");
		} else {
			//attrs = new Attribute(filterName);
            attr = stub.getAttribute(datasetName, filterName);
		}

        attrs[n] = attr;

        return attrs;
    }

    /**
     * Check whether an Xref exists.
     * @param xref reference to check
     * @return if the reference exists, false if not
     * @throws IDMapperException if failed
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
            String fullName = filter.getDisplayName()+" ("+filter.getName()+")";
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
            String fullName = displayName + " ("+name+")";
            DataSource ds = DataSource.getByFullName(fullName);
            dss.add(ds);
            mapSrcDSAttr.put(ds, attr);
        }
        return dss;
    }

    private final IDMapperCapabilities cap = new IDMapperCapabilities() {
	    public boolean isFreeSearchSupported() { return false; }

	    public Set<DataSource> getSupportedSrcDataSources() throws IDMapperException {
            try {
                return IDMapperBiomart.this.getSupportedSrcDataSources();
            } catch (IOException ex) {
                throw new IDMapperException(ex);
            }
	    }

	    public Set<DataSource> getSupportedTgtDataSources() throws IDMapperException {
	    	try {
                return IDMapperBiomart.this.getSupportedTgtDataSources();
            } catch (IOException ex) {
                throw new IDMapperException(ex);
            }
	    }
	};

    /**
     *
     * @return capacities of the ID mapper
     */
    public IDMapperCapabilities getCapabilities() {
        return cap;
    }

    private boolean isConnected = true;
    // In the case of IDMapperBioMart, there is no need to discard associated resources.
    public void close() throws IDMapperException { isConnected = false; }
    public boolean isConnected() { return isConnected; }
}
