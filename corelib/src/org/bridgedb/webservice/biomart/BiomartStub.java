/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package org.bridgedb.webservice.biomart;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

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
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/**
 * BioMart service class, adapted from BioMart client in Cytoscape
 */
public class BiomartStub {
	public static final String defaultBaseURL = "http://www.biomart.org/biomart/martservice";
    
    private final String baseURL;
	private static final String RESOURCE = "/org/bridgedb/webservice/biomart/filterconversion.txt";

    //private Map<String, Map<String, String>> databases = null;
    private Map<String, Database> databases = null;

    private Map<String, Dataset> datasets = new HashMap();
    private Map<String, Vector<Dataset>> mapDbDss = new HashMap();
    private Map<String, Map<String, Filter>> mapDsFilters = new HashMap();
    private Map<String, Map<String, Attribute>> mapDsAttrs = new HashMap();

	// Key is datasource, value is database name.
	//private Map<String, String> datasourceMap = new HashMap<String, String>();
	private Map<String, Map<String, String>> filterConversionMap;
		
	private static final int BUFFER_SIZE = 81920;

    // one instance per base url
	private static Map<String, BiomartStub> instances = new HashMap();

    public static BiomartStub getInstance() throws IOException {
        return getInstance(defaultBaseURL);
    }

    public static BiomartStub getInstance(String baseURL) throws IOException {
        if (baseURL==null) {
            throw new IllegalArgumentException("base url cannot be null");
        }

        BiomartStub instance = instances.get(baseURL);
        if (instance==null) {
            instance = new BiomartStub(baseURL);
            instances.put(baseURL, instance);
        }

        return instance;
   }


	/**
	 * Creates a new BiomartStub object from given URL.
	 *
	 * @param baseURL  DOCUMENT ME!
	 * @throws IOException
	 */
	private BiomartStub(String baseURL) throws IOException {
        this.baseURL = baseURL + "?";

		loadConversionFile();
	}

	private void loadConversionFile() throws IOException {
		filterConversionMap = new HashMap<String, Map<String, String>>();

		InputStreamReader inFile;

		inFile = new InputStreamReader(this.getClass().getResource(RESOURCE).openStream());

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
	 *  DOCUMENT ME!
	 *
	 * @param dbName DOCUMENT ME!
	 * @param filterID DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Attribute filterToAttributeName(String dsName, String dbName, String filterID) {
		if (filterConversionMap.get(dbName) == null) {
			return null;
		} else {
            String attrName = filterConversionMap.get(dbName).get(filterID);
			return this.getAttribute(dsName, attrName);
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param baseURL DOCUMENT ME!
	 */
//	public void setBaseURL(String baseURL) {
//        if (baseURL==null) {
//            this.baseURL = defaultBaseURL + "?";
//        } else {
//            this.baseURL = baseURL + "?";
//        }
//
//        databases = null;
//
//        datasets = new HashMap();
//        mapDbDss = new HashMap();
//	}

	/**
	 *  Get the registry information from the base URL.
	 *
	 * @return  Map of registry information.  Key value is "name" field.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public Map<String, Database> getRegistry()
	    throws IOException, ParserConfigurationException, SAXException {
		// If already loaded, just return it.
		if (databases != null)
			return databases;

		// Initialize database map.
		databases = new HashMap<String, Database>();

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

			databases.put(dbID, new Database(dbID,entry));
		}

		is.close();
		is = null;

		return databases;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param martName DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public Vector<Dataset> getAvailableDatasets(final String martName)
	    throws IOException {
        Vector<Dataset> return_this = mapDbDss.get(martName);
        if (return_this!=null) {
            return return_this;
        }

		try {
			getRegistry();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//final Map<String, String> datasources = new HashMap<String, String>();
        return_this = new Vector();

        Database database = databases.get(martName);

		Map<String, String> detail = database.getParam();

		String urlStr = "http://" + detail.get("host") + ":" + detail.get("port")
		                + detail.get("path") + "?type=datasets&mart=" + detail.get("name");
		System.out.println("DB name = " + martName + ", Target URL = " + urlStr + "\n");

		URL url = new URL(urlStr);
		InputStream is = getInputStream(url);

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String s;

		String[] parts;

		while ((s = reader.readLine()) != null) {
			parts = s.split("\\t");

			if ((parts.length > 4) && parts[3].equals("1")) {
                Dataset dataset = new Dataset(parts[1], parts[2], database);
				return_this.add(dataset);
				//datasourceMap.put(parts[1], martName);
                datasets.put(parts[1], dataset);
			}
		}

		is.close();
		reader.close();
		reader = null;
		is = null;

        mapDbDss.put(martName, return_this);

		return return_this;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param datasetName DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws IOException DOCUMENT ME!
	 */
	public Map<String, Filter> getFilters(String datasetName)
            throws IOException {
        if (datasetName==null) {
            throw new java.lang.IllegalArgumentException("Dataset name cannot be null");
        }

        if (mapDsFilters.get(datasetName)!=null) {
            return mapDsFilters.get(datasetName);
        }

		Map<String, Filter> filters = new HashMap();

		Dataset dataset = getDataset(datasetName);
        if (dataset==null) {
            return filters;
        }

        Database database = dataset.getDatabase();

		Map<String, String> detail = database.getParam();

		String urlStr = "http://" + detail.get("host") + ":" + detail.get("port")
		                + detail.get("path") + "?virtualschema="
		                + detail.get("serverVirtualSchema") + "&type=filters&dataset="
		                + datasetName;

		//System.out.println("Dataset name = " + datasetName + ", Target URL = " + urlStr + "\n");
		URL url = new URL(urlStr);
		InputStream is = getInputStream(url);

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String s;

		String[] parts;

		while ((s = reader.readLine()) != null) {
			parts = s.split("\\t");

			if ((parts.length > 1)) {
				if ((parts[1].contains("ID(s)") || parts[1].contains("Accession(s)")
				           || parts[1].contains("IDs")) && (parts[0].startsWith("with_") == false)
				           && (parts[0].endsWith("-2") == false) || parts.length>6 && parts[5].equals("id_list")) {
					//filters.put(parts[1], parts[0]);
                    filters.put(parts[0], new Filter(parts[0], parts[1]));
//					System.out.println("### Filter Entry = " + parts[1] + " = " + parts[0]);
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
	 *  DOCUMENT ME!
	 *
	 * @param datasetName DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 * @throws IOException
	 *
	 * @throws IOException DOCUMENT ME!
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
	 *  Send the XML query to Biomart, and get the result as table.
	 *
	 * @param xmlQuery DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
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
//		String line;
//		line = reader.readLine();
//		
//		String[] parts = line.split("\\t");
//		final List<String[]> result = new ArrayList<String[]>();
//		result.add(parts);

//		while ((line = reader.readLine()) != null) {
//			
//			System.out.println("Result ==> " + line);
//			
//			parts = line.split("\\t");
//			result.add(parts);
//		}
//
//		is.close();
//		reader.close();
//		reader = null;

//		return result;
	}

    public Database getDatabase(final String dbname) {
        return databases.get(dbname);
    }

    public Dataset getDataset(final String dsname) {
        return datasets.get(dsname);
    }
    
    private static final int msConnectionTimeout = 2000;
    //TODO: test when IOException is throwed
    protected static InputStream getInputStream(URL source) throws IOException {
        InputStream stream = null;
        int expCount = 0;
        int timeOut = msConnectionTimeout;
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
