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
package org.bridgedb.webservice.picr;

import java.net.URL;
import java.net.URLConnection;

import java.io.InputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bridgedb.AbstractIDMapperCapabilities;
import org.bridgedb.AttributeMapper;
import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.webservice.IDMapperWebservice;
import org.bridgedb.Xref;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//TODO: implements AttributeMapper
public class IDMapperPicrRest extends IDMapperWebservice
{
    static 
    {
		BridgeDb.register ("idmapper-picr-rest", new Driver());
	}

    private String baseUrl = "http://www.ebi.ac.uk/Tools/picr/rest/";
    private boolean onlyActive;
    
    private Set<DataSource> supportedDatabases = new HashSet<DataSource>();

    /**
     *
     * @param onlyActive using only active mappings if true
     */
    public IDMapperPicrRest(boolean onlyActive) throws IDMapperException
    {        
        List<String> dbs;
        try {
            dbs = getMappedDataBaseNames();
        } catch (Exception e) {
            throw new IDMapperException(e);
        }
        for (String s : dbs)
        {
        	supportedDatabases.add(DataSource.getByFullName(s));
        }
        this.onlyActive = onlyActive;
    }

    /**
     *
     * @return true if using only active mappings; false otherwise
     */
    public boolean getOnlyActive() {
        return onlyActive;
    }

    /**
     *
     * @param onlyActive using only active mappings if true
     */
    public void setOnlyActive(boolean onlyActive) {
        this.onlyActive = onlyActive;
    }

    private static class Driver implements org.bridgedb.Driver
	{
        private Driver() { } // prevent outside instantiation

		public IDMapper connect(String location) throws IDMapperException  
		{
			// location string is ignored...
			
			if (location.equals("only-active=false"))
			{
				return new IDMapperPicrRest(false);
			}
			else if (location.equals ("only-active=true") || location.equals (""))
			{
				return new IDMapperPicrRest(true);
			}
			else
			{
				throw new IDMapperException ("Could not parse location string '" + location + "'");
			}
		}
	}
	
	private boolean closed = false;
	public void close() throws IDMapperException 
	{
		closed = true;
	}

        /**
         * free search is not supported
         */
	public Set<Xref> freeSearch(String text, int limit)
			throws IDMapperException {
		throw new UnsupportedOperationException();
	}

        private List<String> getMappedDataBaseNames() throws Exception {
            String url = baseUrl + "getMappedDatabaseNames";
            InputStream is = getInputStream(url);
            // Get the result as XML document.
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document doc = builder.parse(is);
            NodeList nodes = doc.getElementsByTagName("mappedDatabases");
            int n = nodes.getLength();
            List<String> dbs = new Vector(n);
            for (int i=0; i<n; i++) {
                Node node = nodes.item(i);
                dbs.add(node.getTextContent());
            }
            return dbs;
        }

	private class PICRCapabilities extends AbstractIDMapperCapabilities
	{
		public PICRCapabilities() 
		{
			super (supportedDatabases, false, null);
		}
	}
	
	private PICRCapabilities picrCapabilities = new PICRCapabilities();
	
	public IDMapperCapabilities getCapabilities() 
	{
		return picrCapabilities;
	}

	public boolean isConnected() 
	{
		return !closed;
	}

        /**
         * {@inheritDoc}
         */
	public Map<Xref, Set<Xref>> mapID(Set<Xref> srcXrefs,
			Set<DataSource> tgtDataSources) throws IDMapperException 
	{
            if (srcXrefs==null) {
                throw new java.lang.IllegalArgumentException(
                            "srcXrefs or tgtDataSources cannot be null.");
            }

            Map<Xref, Set<Xref>> result = new HashMap<Xref, Set<Xref>>();

            // remove unsupported data sources
            Set<String> tgtDss = new HashSet();
            if (tgtDataSources!=null) {
                for (DataSource ds : tgtDataSources) {
                    if (supportedDatabases.contains(ds)) {
                        tgtDss.add(ds.getFullName());
                    }
                }
            } else {
                for (DataSource ds : supportedDatabases) {
                    tgtDss.add(ds.getFullName());
                }
            }

            for (Xref xref : srcXrefs) {
                DataSource ds = xref.getDataSource();
                if (!supportedDatabases.contains(ds))
                    continue;

                Set<String> databases = new HashSet(tgtDss.size()+1);
                databases.addAll(tgtDss);
                databases.add(ds.getFullName()); // add the source ds

                String accession = xref.getId();

                Set<Xref> tgtXrefs;
                try {
                    tgtXrefs = mappingService(accession, databases);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue; // skip this one
                }
                
                if (!tgtXrefs.remove(xref)) // remove itself, if not exist,
                    continue;               // then the src xref not exist

                if (!tgtXrefs.isEmpty())
                    result.put(xref, tgtXrefs);
            }

            return result;
	}

        /**
         * {@inheritDoc}
         */
	public boolean xrefExists(Xref xref) throws IDMapperException 
	{
            DataSource ds = xref.getDataSource();
            if (!supportedDatabases.contains(ds))
                return false;

            Set<String> dss = new HashSet();
            dss.add(ds.getFullName());
            
            Set<Xref> xrefs = null;
            try {
                 xrefs = mappingService(xref.getId(), dss);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            return xrefs!=null && !xrefs.isEmpty();
	}

        private Set<Xref> mappingService(String accession, Set<String> databases)
                    throws Exception {
            Set<Xref> result = new HashSet();

            StringBuilder url = new StringBuilder(baseUrl);
            url.append("getUPIForAccession?");

            if (!onlyActive) {
                url.append("onlyactive=false&");
            }

            url.append("accession=");
            url.append(accession);

            for (String database : databases) {
                url.append("&database=");
                url.append(database);
            }

            final Document doc;
            InputStream is = getInputStream(url.toString());
            if (is.available()<=0) return result;

            // Get the result as XML document.
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(is);

            String[] tags = new String[] {"ns2:identicalCrossReferences"
                        , "ns2:logicalCrossReferences"};
            List<Node> nodes = new Vector();
            for (String tag : tags) {
                NodeList nodeList = doc.getElementsByTagName(tag);
                int n = nodeList.getLength();
                for (int i=0; i<n; i++) {
                    nodes.add(nodeList.item(i));
                }
            }

            for (Node node : nodes) {
                NodeList children = node.getChildNodes();
                int nc = children.getLength();
                String tgtAcc = null;
                String tgtDb = null;
                for (int j=0; j<nc; j++) {
                    Node child = children.item(j);
                    if (child.getNodeName().compareTo("ns2:accession")==0) {
                        tgtAcc = child.getTextContent();
                    }  else if (child.getNodeName().compareTo("ns2:databaseName")==0) {
                        tgtDb = child.getTextContent();
                    }
                    if (tgtAcc!=null && tgtDb!=null) {
                        DataSource tgtDs = DataSource.getByFullName(tgtDb);
                        result.add(new Xref(tgtAcc, tgtDs));
                        break;
                    }
                }
            }

            return result;
        }

    private static final int msConnectionTimeout = 2000;
    //TODO: test when IOException is throwed
    protected static InputStream getInputStream(String source) throws IOException {
        URL url = new URL(source);
        InputStream stream = null;
        int expCount = 0;
        int timeOut = msConnectionTimeout;
        while (true) { // multiple chances
            try {
                URLConnection uc = url.openConnection();
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
