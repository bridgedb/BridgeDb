// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bridgedb.AbstractIDMapperCapabilities;
import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.impl.InternalUtils;
import org.bridgedb.webservice.IDMapperWebservice;
import org.bridgedb.Xref;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

//TODO: implements AttributeMapper
public class IDMapperPicrRest extends IDMapperWebservice
{
    static 
    {
		BridgeDb.register ("idmapper-picr-rest", new Driver());
	}

    private String baseUrl = "https://www.ebi.ac.uk/Tools/picr/rest/";
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
            Map<String, String> args = 
            	InternalUtils.parseLocation(location, "only-active");

			boolean isOnlyActive = true;
			if (args.containsKey("only-active"))
			{
				isOnlyActive = Boolean.parseBoolean(args.get("only-active"));
			}
			return new IDMapperPicrRest(isOnlyActive);
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
            InputStream is = InternalUtils.getInputStream(url);
            // Get the result as XML document.
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document doc = builder.parse(is);
            NodeList nodes = doc.getElementsByTagName("ns2:mappedDatabases");
            int n = nodes.getLength();
            List<String> dbs = new ArrayList<String>(n);
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
	public Map<Xref, Set<Xref>> mapID(Collection<Xref> srcXrefs,
		DataSource... tgtDataSources) throws IDMapperException 
	{
		return InternalUtils.mapMultiFromSingle(this, srcXrefs, tgtDataSources);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override public Set<Xref> mapID(Xref xref,
			DataSource... tgtDataSources) throws IDMapperException 
	{
            if (xref==null) {
                throw new NullPointerException(
                            "srcXrefs or tgtDataSources cannot be null.");
            }

            Set<Xref> result = new HashSet<Xref>();

            // remove unsupported data sources
            Set<String> tgtDss = new HashSet<String>();
            if (tgtDataSources.length > 0) {
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

            DataSource ds = xref.getDataSource();
            if (!supportedDatabases.contains(ds))
                return result;

            Set<String> databases = new HashSet<String>(tgtDss.size()+1);
            databases.addAll(tgtDss);
            databases.add(ds.getFullName()); // add the source ds

            String accession = xref.getId();

            try {
                result = mappingService(accession, databases);
            } catch (Exception e) {
            	throw new IDMapperException (e);
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

            Set<String> dss = new HashSet<String>();
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
        throws IOException, SAXException, ParserConfigurationException
 {
            Set<Xref> result = new HashSet<Xref>();

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
            InputStream is = InternalUtils.getInputStream(url.toString());
            if (is.available()<=0) return result;

            // Get the result as XML document.
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(is);

            String[] tags = new String[] {"identicalCrossReferences"
                        , "logicalCrossReferences"};
            List<Node> nodes = new ArrayList<Node>();
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
                    if ("accession".equals(child.getNodeName())) 
                    {
                        tgtAcc = child.getTextContent();
                    } 
                    else if ("databaseName".equals(child.getNodeName())) 
                    {
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
}
