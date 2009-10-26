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

import java.io.BufferedReader;
import java.io.IOException;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.bridgedb.IDMapperException;
import org.bridgedb.webservice.biomart.util.Attribute;
import org.bridgedb.webservice.biomart.util.BiomartClient;
import org.bridgedb.webservice.biomart.util.Database;
import org.bridgedb.webservice.biomart.util.Dataset;
import org.bridgedb.webservice.biomart.util.Filter;
import org.bridgedb.webservice.biomart.util.XMLQueryBuilder;

import org.xml.sax.SAXException;

/**
 * Wrapp-up for BiomartClient.
 * @author gjj
 */
public final class BiomartStub {
    public static final String defaultBaseURL
            = BiomartClient.DEFAULT_BASE_URL;

    // one instance per url
    private static Map<String, BiomartStub> instances = new HashMap<String, BiomartStub>();

    /**
     *
     * @return BiomartStub with the default server url
     * @throws IOException if failed to connect
     */
    public static BiomartStub getInstance() throws IOException {
        return getInstance(defaultBaseURL);
    }

    /**
     *
     * @param baseUrl server url
     * @return BiomartStub from the server
     * @throws IOException if failed to connect
     */
    public static BiomartStub getInstance(String baseUrl) throws IOException {
        if (baseUrl==null) {
            throw new IllegalArgumentException("base url cannot be null");
        }

        BiomartStub instance = instances.get(baseUrl);
        if (instance==null) {
            instance = new BiomartStub(baseUrl);
            instances.put(baseUrl, instance);
        }

        return instance;
    }

    private BiomartClient client;

    /**
     *
     * @param baseUrl server url.
     * @throws IOException if failed to connect.
     */
    private BiomartStub(String baseUrl) throws IOException {
        client = new BiomartClient(baseUrl);
    }

    /**
     *
     * @return available marts
     * @throws IDMapperException if failed
     */
    public Set<String> availableMarts() throws IDMapperException {
        Map<String, Database> marts;
        try {
            marts = client.getRegistry();
        } catch (IOException e) {
            throw new IDMapperException(e);
        } catch (ParserConfigurationException e) {
            throw new IDMapperException(e);
        } catch (SAXException e) {
            throw new IDMapperException(e);
        }

        Set<String> visibleMarts = new HashSet<String>();
        for (Database db : marts.values()) {
            if (db.visible()) {
                visibleMarts.add(db.getName());
            }
        }

        return visibleMarts;
    }

    /**
     *
     * @param mart mart name
     * @return mart display name or null if not exist
     */
    public String martDisplayName(String mart) {
        if (mart==null) {
            return null;
        }

        Database db = client.getMart(mart);
        return db==null?null:db.displayName();
    }

    /**
     *
     * @param mart mart name
     * @return available datasets from this mart
     */
    public Set<String> availableDatasets(String mart)
            throws IDMapperException {
        if (mart==null) {
            return Collections.emptySet();
        }

        if (!availableMarts().contains(mart)) {
            return Collections.emptySet();
        }

        Map<String,Dataset> datasets;
        try {
            datasets = client.getAvailableDatasets(mart);
        } catch (IOException e) {
            throw new IDMapperException(e);
        }

        return datasets.keySet();
    }

     /**
     *
     * @param dataset dataset name
     * @return dataset display name or null if not exist
     */
    public String datasetDisplayName(String dataset) {
        if (dataset==null) {
            return null;
        }

        Dataset ds = client.getDataset(dataset);
        return ds==null?null:ds.displayName();
    }

    /**
     *
     * @param mart mart name
     * @param dataset dataset name
     * @return available filters / source id types of the dataset from this
     *         mart
     * @throws IDMapperException if failed
     */
    public Set<String> availableSrcIDTypes(final String mart,
            final String dataset) throws IDMapperException {
        if (mart==null || dataset==null) {
            return Collections.emptySet();
        }

        if (!availableDatasets(mart).contains(dataset)) {
            return Collections.emptySet();
        }

        Map<String,Filter> filters;
        try {
            filters = client.getFilters(dataset);
        } catch (IOException e) {
            throw new IDMapperException(e);
        }

        return filters.keySet();
    }

    /**
     *
     * @param mart mart name
     * @param dataset dataset name
     * @return available tgt id types
     * @throws IDMapperException if failed.
     */
    public Set<String> availableTgtIDTypes(final String mart,
            final String dataset) throws IDMapperException {
        return availableAttributes(mart, dataset, true);
    }

    /**
     *
     * @param mart mart name
     * @param dataset dataset name
     * @return availab e tgt attributes (exclude id types)
     * @throws IDMapperException if failed.
     */
    public Set<String> availableTgtAttributes(final String mart,
            final String dataset) throws IDMapperException {
        return availableAttributes(mart, dataset, false);
    }

    /**
     *
     * @param mart mart name.
     * @param dataset dataset name.
     * @param idOnly filter the attributes ending with "ID" or "Accession"
     *        if true; no filter otherwise.
     * @return attribute names / target id types of the dataset from this
     *         mart.
     * @throws IDMapperException if failed.
     */
    private Set<String> availableAttributes(final String mart,
            final String dataset, boolean idOnly) throws IDMapperException {

        if (mart==null || dataset==null) {
            return Collections.emptySet();
        }

        if (!availableDatasets(mart).contains(dataset)) {
            return Collections.emptySet();
        }

        Map<String,Attribute> attributes;
        try {
            attributes = client.getAttributes(dataset);
        } catch (IOException e) {
            throw new IDMapperException(e);
        }

        Set<String> result = new HashSet<String>();
        for (String name : attributes.keySet()) {
            if (name.trim().length()==0)
                continue;
            String displayName = client.getAttribute(dataset, name)
                    .getDisplayName();
            if (idOnly == (displayName.endsWith("ID")
                    || displayName.endsWith("Accession")
                    || name.endsWith("id")
                    || name.endsWith("accession"))) {
                result.add(name);
            }
        }

        return result;
    }

    /**
     * 
     * @param mart mart name
     * @param dataset dataset name
     * @param srcType filter name / source id type
     * @param tgtTypes attribute names / target id types
     * @param srcIds source ids to be translated
     * @return map from source id to target ids
     *         key: source id
     *         value: corresponding target ids
     * @throws IDMapperException if failed to connect
     */
    public Map<String,Set<String>[]> translate(final String mart,
            final String dataset, final String srcType,
            final String[] tgtTypes, final Set<String> srcIds)
            throws IDMapperException {
        if (mart==null||dataset==null||srcType==null||tgtTypes==null||srcIds==null) {
            throw new IllegalArgumentException("Null argument.");
        }

        Attribute tgtAttr = client.filterToAttribute(dataset, srcType);
        if (tgtAttr==null) {
            return new HashMap<String, Set<String>[]>();
        }

        int nAttr = tgtTypes.length;
        Attribute[] attrs = new Attribute[nAttr+1];

        // prepare attributes
        int iattr = 0;
        for (String attr : tgtTypes) {
            attrs[iattr++] = client.getAttribute(dataset, attr);
        }
        attrs[nAttr] = tgtAttr;

        // prepare filters
        StringBuilder sb = new StringBuilder();
        for (String str : srcIds) {
            sb.append(str);
            sb.append(",");
        }

        int len = sb.length();
        if (len>0) {
            sb.deleteCharAt(len-1);
        }

        Map<String, String> queryFilter = new HashMap<String, String>(1);
        queryFilter.put(srcType, sb.toString());

        // build query string
        String query = XMLQueryBuilder.getQueryString(dataset, attrs, queryFilter);

        // query
        BufferedReader bfr = null;
        try {
            bfr = client.sendQuery(query);
            if (!bfr.ready())
                throw new IDMapperException("Query failed");
        } catch (IOException e) {
            throw new IDMapperException(e);
        }

        if (bfr==null) {
            return new HashMap<String, Set<String>[]>(0);
        }

        // read id mapping
        Map<String,Set<String>[]> result = new HashMap<String,Set<String>[]>();
        try {
            bfr.readLine();
            String line;
            while ((line = bfr.readLine())!=null) {
                String[] strs = line.split("\t");
                if (strs.length!=nAttr+1)
                    continue; // because the last one is the src id
                String src = strs[nAttr];
                Set<String>[] tgt = result.get(src);
                if (tgt==null) {
                    tgt = new Set[nAttr];
                    for (int i=0; i<nAttr; i++) {
                        tgt[i] = new HashSet<String>();
                    }
                    result.put(src, tgt);
                }

                for (int i=0; i<nAttr; i++) {
                    String str = strs[i];
                    if (str.length()>0) {
                        tgt[i].add(str);
                    }
                }

            }
        } catch (IOException e) {
            throw new IDMapperException(e);
        }

        return result;
    }
}
