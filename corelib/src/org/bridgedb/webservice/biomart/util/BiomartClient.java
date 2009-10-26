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

package org.bridgedb.webservice.biomart.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

import java.net.URL;
import java.net.URLConnection;

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bridgedb.webservice.biomart.IDMapperBiomart;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

/**
 * BioMart service class, adapted from BioMart client in Cytoscape.
 */
public final class BiomartClient {
    public static final String DEFAULT_BASE_URL = "http://www.biomart.org/biomart/martservice";
    
    private final String baseURL;
    private static final String RESOURCE = "filterconversion.txt";

    //private Map<String, Map<String, String>> databases = null;
    private Map<String, Database> marts = null;

    private Map<String, Dataset> datasets = new HashMap<String, Dataset>();
    private Map<String, Map<String,Dataset>> mapDbDss = new HashMap<String, Map<String, Dataset>>();
    private Map<String, Map<String, Filter>> mapDsFilters = new HashMap<String, Map<String, Filter>>();
    private Map<String, Map<String, Attribute>> mapDsAttrs = new HashMap<String, Map<String, Attribute>>();

    private Map<String, Map<String, String>> filterConversionMap;

    private static final int BUFFER_SIZE = 81920;

    /**
     * Creates a new BiomartStub object from given URL.
     *
     * @param baseURL  DOCUMENT ME!
     * @throws IOException if failed to read local resource
     */
    public BiomartClient(String baseURL) throws IOException {
        this.baseURL = baseURL + "?";
        loadConversionFile();
    }

    /**
     * Conversion map from filter to attribute.
     * @throws IOException if failed to read local resource
     */
    private void loadConversionFile() throws IOException {
        filterConversionMap = new HashMap<String, Map<String, String>>();

        InputStreamReader inFile = new InputStreamReader(IDMapperBiomart.class.getResource(RESOURCE).openStream());

        BufferedReader inBuffer = new BufferedReader(inFile);

        String line;
        String trimed;
        String oldName = null;
        Map<String, String> oneEntry = new HashMap<String, String>();

        String[] dbparts;

        while ((line = inBuffer.readLine()) != null) {
            trimed = line.trim();
            dbparts = trimed.split("\\t");

            if (dbparts[0].equals(oldName) == false) {
                oneEntry = new HashMap<String, String>();
                oldName = dbparts[0];
                filterConversionMap.put(oldName, oneEntry);
            }

            oneEntry.put(dbparts[1], dbparts[2]);
        }

        inFile.close();
        inBuffer.close();
    }

    /**
     * Convert filter to attribute according to the conversion file.
     * @param dsName dataset name.
     * @param dbName database name
     * @param filterID filter ID
     * @return converted attribute
     */
    private Attribute filterToAttribute(String dsName, String dbName,
                String filterID) {
        if (filterConversionMap.get(dbName) == null) {
            return null;
        } else {
            String attrName = filterConversionMap.get(dbName).get(filterID);
            return this.getAttribute(dsName, attrName);
        }
    }

    /**
     *
     * @param dataset dataset name
     * @param filter filter name
     * @return Attribute converted from filter
     */
    public Attribute filterToAttribute(String dataset, String filter) {
        Attribute attr;
        if (dataset.contains("REACTOME")) {
            attr = filterToAttribute(dataset, "REACTOME", filter);
        } else if (dataset.contains("UNIPROT")) {
            attr = filterToAttribute(dataset, "UNIPROT", filter);
        } else if (dataset.contains("VARIATION")) {
            attr = getAttribute(dataset, filter + "_stable_id");
        } else {
            attr = getAttribute(dataset, filter);
        }

        return attr;
    }

    /**
     *  Get the registry information from the base URL.
     *
     * @return  Map of registry information.  Key value is "name" field.
     * @throws ParserConfigurationException if failed new document builder
     * @throws SAXException if failed to parse registry
     * @throws IOException if failed to read from URL
     */
    public Map<String, Database> getRegistry()
            throws IOException, ParserConfigurationException, SAXException {
        // If already loaded, just return it.
        if (marts != null)
            return marts;

        // Initialize database map.
        marts = new HashMap<String, Database>();

        // Prepare URL for the registry status
        final String reg = "type=registry";
        final URL targetURL = new URL(baseURL + reg);

        // Get the result as XML document.
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();

        InputStream is = getInputStream(targetURL);

        final Document registry = builder.parse(is);

        // Extract each datasource
        NodeList locations = registry.getElementsByTagName("MartURLLocation");
        int locSize = locations.getLength();
        NamedNodeMap attrList;
        int attrLen;
        String dbID;

        for (int i = 0; i < locSize; i++) {
            attrList = locations.item(i).getAttributes();
            attrLen = attrList.getLength();

            // First, get the key value
            dbID = attrList.getNamedItem("name").getNodeValue();

            Map<String, String> entry = new HashMap<String, String>();

            for (int j = 0; j < attrLen; j++) {
                entry.put(attrList.item(j).getNodeName(), attrList.item(j).getNodeValue());
            }

            marts.put(dbID, new Database(dbID,entry));
        }

        is.close();
        is = null;

        return marts;
    }

    /**
     * Get available datasets of a mart/database.
     * @param martName mart name
     * @return {@link Vector} of available datasets
     * @throws IOException if failed to read
     */
    public Map<String, Dataset> getAvailableDatasets(final String martName)
            throws IOException {
        Map<String, Dataset> result = mapDbDss.get(martName);
        if (result!=null) {
            return result;
        }

        try {
            getRegistry();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        //final Map<String, String> datasources = new HashMap<String, String>();
        result = new HashMap<String, Dataset>();

        Database database = marts.get(martName);

        if (database==null) {
            return null;
        }

        Map<String, String> detail = database.getParam();

        String urlStr = "http://" + detail.get("host") + ":" + detail.get("port")
                        + detail.get("path") + "?type=datasets&mart=" + detail.get("name");
        //System.out.println("DB name = " + martName + ", Target URL = " + urlStr + "\n");

        URL url = new URL(urlStr);
        InputStream is = getInputStream(url);

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String s;

        String[] parts;

        while ((s = reader.readLine()) != null) {
            parts = s.split("\\t");

            if ((parts.length > 4) && parts[3].equals("1")) {
                Dataset dataset = new Dataset(parts[1], parts[2], database);
                result.put(dataset.getName(),dataset);
                //datasourceMap.put(parts[1], martName);
                datasets.put(parts[1], dataset);
            }
        }

        is.close();
        reader.close();
        reader = null;
        is = null;

        mapDbDss.put(martName, result);

        return result;
    }

    /**
     * Get filters for a dataset.
     * @param datasetName name of data set
     * @return filters
     * @throws IOException if failed to read
     */
    public Map<String, Filter> getFilters(String datasetName)
        throws IOException {
        if (datasetName==null) {
            throw new IllegalArgumentException("Dataset name cannot be null");
        }

        if (mapDsFilters.get(datasetName)!=null) {
            return mapDsFilters.get(datasetName);
        }

        Map<String, Filter> filters = new HashMap<String, Filter>();

        Dataset dataset = getDataset(datasetName);
        if (dataset==null) {
            return filters;
        }

        Database database = dataset.getDatabase();

        Map<String, String> detail = database.getParam();

        String urlStr = "http://" 
                        + detail.get("host") + ":"
                        + detail.get("port")
                        + detail.get("path")
                        + "?virtualschema="
                        + detail.get("serverVirtualSchema")
                        + "&type=filters&dataset="
                        + datasetName;

        //System.out.println("Dataset name = " + datasetName + ", Target URL = "
        //                      + urlStr + "\n");
        URL url = new URL(urlStr);
        InputStream is = getInputStream(url);

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String s;

        String[] parts;

        while ((s = reader.readLine()) != null) {
            parts = s.split("\\t");

            if ((parts.length > 1)) {
                if ((parts[1].contains("ID(s)")
                            || parts[1].contains("Accession(s)")
                            || parts[1].contains("IDs"))
                         && (parts[0].startsWith("with_") == false)
                         && (parts[0].endsWith("-2") == false)
                         || parts.length>6
                         && parts[5].equals("id_list")) {
                    //filters.put(parts[1], parts[0]);
                    filters.put(parts[0], new Filter(parts[0], parts[1]));
                    // System.out.println("### Filter Entry = " + parts[1] + " = " + parts[0]);
                }
            }
        }

        is.close();
        reader.close();
        reader = null;
        is = null;

        mapDsFilters.put(datasetName, filters);

        return filters;
    }

    /**
     * Get attributes.
     * @param datasetName dataset name
     * @return Map of attribute name to attributes
     * @throws IOException if failed to read
     */
    public Map<String, Attribute> getAttributes(String datasetName) throws IOException {
        if (datasetName==null) {
            throw new java.lang.IllegalArgumentException("Dataset name cannot be null");
        }

        if (mapDsAttrs.get(datasetName)!=null) {
            return mapDsAttrs.get(datasetName);
        }

        Map<String, Attribute> attributes = new HashMap<String, Attribute>();

        Dataset dataset = getDataset(datasetName);
        if (dataset==null) {
            return attributes;
        }

        Database database = dataset.getDatabase();

        Map<String, String> detail = database.getParam();

        String urlStr = "http://" + detail.get("host") + ":" + detail.get("port")
                        + detail.get("path") + "?virtualschema="
                        + detail.get("serverVirtualSchema") + "&type=attributes&dataset="
                        + datasetName;

        //System.out.println("Dataset name = " + datasetName + ", Target URL = " + urlStr + "\n");
        URL url = new URL(urlStr);
        InputStream is = getInputStream(url);

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String s;

        String displayName;

        String[] parts;

        while ((s = reader.readLine()) != null) {
            parts = s.split("\\t");

            if (parts.length == 0)
                continue;

            if (parts.length == 4) {
                displayName = parts[3] + ": " + parts[1];
            } else if (parts.length > 1) {
                displayName = parts[1];
            } else {
                displayName = "";
            }

            attributes.put(parts[0], new Attribute(parts[0],displayName));
        }

        is.close();
        reader.close();
        reader = null;
        is = null;

        this.mapDsAttrs.put(datasetName, attributes);

        return attributes;
    }

    /**
     * Get filter.
     * @param datasetName dataset name
     * @param filterName filter name
     * @return filter
     */
    public Filter getFilter(String datasetName, String filterName) {
        if (datasetName==null || filterName==null) {
            throw new java.lang.IllegalArgumentException("datasetName and filterName cannot be null");
        }

        Map<String, Filter> map = this.mapDsFilters.get(datasetName);
        if (map==null) {
            return null;
        }

        return map.get(filterName);
    }

    /**
     * get Attribute.
     * @param datasetName dataset name
     * @param attrName attribute name
     * @return attribute
     */
    public Attribute getAttribute(String datasetName, String attrName) {
        if (datasetName==null || attrName==null) {
            throw new java.lang.IllegalArgumentException("datasetName and attrName cannot be null");
        }

        Map<String, Attribute> map = this.mapDsAttrs.get(datasetName);
        if (map==null) {
            return null;
        }

        return map.get(attrName);
    }

    /**
     * Send the XML query to Biomart, and get the result as table.
     * @param xmlQuery query xml
     * @return result {@link BufferedReader}
     * @throws IOException if failed to read
     */
    public BufferedReader sendQuery(String xmlQuery) throws IOException {

        //System.out.println("=======Query = " + xmlQuery);

        URL url = new URL(baseURL);
        URLConnection uc = url.openConnection();
        uc.setDoOutput(true);
        uc.setRequestProperty("User-Agent", "Java URLConnection");

        OutputStream os = uc.getOutputStream();

        final String postStr = "query=" + xmlQuery;
        PrintStream ps = new PrintStream(os);

        // Post the data
        ps.print(postStr);
        os.close();
        ps.close();
        ps = null;
        os = null;

        return new BufferedReader(new InputStreamReader(uc.getInputStream()), BUFFER_SIZE);
    }

    /**
     * get Database/mart.
     * @param dbname database name
     * @return database
     */
    public Database getMart(final String dbname) {
        return marts.get(dbname);
    }

    /**
     * get Dataset.
     * @param dsname dataset name
     * @return dataset
     */
    public Dataset getDataset(final String dsname) {
        return datasets.get(dsname);
    }
    
    private static final int MS_CONNECTION_TIMEOUT = 2000;
    //TODO: test when IOException is throwed
    protected static InputStream getInputStream(URL source) throws IOException {
        InputStream stream = null;
        int expCount = 0;
        int timeOut = MS_CONNECTION_TIMEOUT;
        while (true) { // multiple chances
            try {
                URLConnection uc = source.openConnection();
                uc.setUseCaches(false); // don't use a cached page
                uc.setConnectTimeout(timeOut); // set timeout for connection
                stream = uc.getInputStream();
                break;
            } catch (IOException e) {
                if (expCount++==4) {
                    throw(e);
                } else {
                    timeOut *= 2;
                }
            }
        }

        return stream;
    }
}
