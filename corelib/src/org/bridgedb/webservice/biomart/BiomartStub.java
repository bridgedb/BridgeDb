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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.file.IDMappingReaderFromDelimitedReader;
import org.bridgedb.webservice.biomart.util.Attribute;
import org.bridgedb.webservice.biomart.util.BiomartClient;
import org.bridgedb.webservice.biomart.util.Database;
import org.bridgedb.webservice.biomart.util.Dataset;
import org.bridgedb.webservice.biomart.util.Filter;
import org.bridgedb.webservice.biomart.util.XMLQueryBuilder;

import org.xml.sax.SAXException;

/**
 * Cache for SynergizerClient
 * @author gjj
 */
public class BiomartStub {
    public static final String defaultBaseURL
            = BiomartClient.defaultBaseURL;

    // cache data
//    private Map<String,Map<String,Map<String,Set<String>>>>
//            mapAuthSpeciesDomainRange = null;

    // one instance per url
    private static Map<String, BiomartStub> instances = new HashMap();

    /**
     *
     * @return SynergizerStub with the default server url
     * @throws IOException if failed to connect
     */
    public static BiomartStub getInstance() throws IOException {
        return getInstance(defaultBaseURL);
    }

    /**
     *
     * @param baseUrl server url
     * @return SynergizerStub from the server
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

        Set<String> visibleMarts = new HashSet();
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
     * @throws IDMapperException if failed to connect
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
     * @param authority mart name
     * @return available datasets from this mart
     * @throws IDMapperException if failed
     */
    public Set<String> availableDatasets(String mart)
            throws IDMapperException {
        if (mart==null) {
            return new HashSet(0);
        }

        if (!availableMarts().contains(mart)) {
            return new HashSet(0);
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
     * @throws IDMapperException if failed to connect
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
    public Set<String> availableFilters(final String mart,
            final String dataset) throws IDMapperException {
        if (mart==null || dataset==null) {
            return new HashSet(0);
        }

        if (!availableDatasets(mart).contains(dataset)) {
            return new HashSet(0);
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
     * @return attribute names / target id types of the dataset from this
     *         mart
     * @throws IDMapperException if failed.
     */
    public Set<String> availableAttributes(final String mart,
            final String dataset, boolean idOnly) throws IDMapperException {

        if (mart==null || dataset==null) {
            return new HashSet(0);
        }

        if (!availableDatasets(mart).contains(dataset)) {
            return new HashSet(0);
        }

        Map<String,Attribute> attributes;
        try {
            attributes = client.getAttributes(dataset);
        } catch (IOException e) {
            throw new IDMapperException(e);
        }

        Set<String> result;
        if (idOnly) {
            result = new HashSet();
            for (String name : attributes.keySet()) {
                String displayName = client.getAttribute(dataset, name).getDisplayName();
                if (displayName.endsWith("ID")
                        || displayName.endsWith("Accession")
                        || name.endsWith("id")
                        || name.endsWith("accession")) {
                    result.add(name);
                }
            }
        } else {
            result = new HashSet(attributes.keySet());
        }

        return result;
    }

    /**
     * 
     * @param mart mart name
     * @param dataset dataset name
     * @param filter filter name / source id type
     * @param attributes attribute names / target id types
     * @param ids source ids to be translated
     * @return map from source id to target ids
     *         key: source id
     *         value: corresponding target ids
     * @throws IDMapperException
     */
    public Map<String,Set<String>[]> translate(final String mart,
            final String dataset, final String filter,
            final String[] attributes, final Set<String> ids)
            throws IDMapperException {
        int nAttr = attributes.length;
        Attribute[] attrs = new Attribute[nAttr+1];

        // prepare attributes
        int iattr = 0;
        for (String attr : attributes) {
            attrs[iattr++] = client.getAttribute(dataset, attr);
        }
        attrs[nAttr] = client.filterToAttribute(dataset, filter);

        // prepare filters
        StringBuilder sb = new StringBuilder();
        for (String str : ids) {
            sb.append(str);
            sb.append(",");
        }

        int len = sb.length();
        if (len>0) {
            sb.deleteCharAt(len-1);
        }

        Map<String, String> queryFilter = new HashMap(1);
        queryFilter.put(filter, sb.toString());

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
            return new HashMap(0);
        }

        // read id mapping
        Map<String,Set<String>[]> result = new HashMap();
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
                        tgt[i] = new HashSet();
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
