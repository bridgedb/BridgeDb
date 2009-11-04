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
package org.bridgedb.webservice.bridgerest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bridgedb.AttributeMapper;
import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.webservice.IDMapperWebservice;

/**
 * IDMapper implementation for BridgeRest, the REST interface of BridgeDb itself.
 */
public class BridgeRest extends IDMapperWebservice implements AttributeMapper
{
	static {
		BridgeDb.register ("idmapper-bridgerest", new Driver());
	}

	private final static class Driver implements org.bridgedb.Driver {
		/** private constructor to prevent outside instantiation. */
		private Driver() { } 

		/** {@inheritDoc} */
		public IDMapper connect(String location) throws IDMapperException  {
			return new BridgeRest(location);
		}
	}

	private final static class RestCapabilities implements IDMapperCapabilities {
		private String baseUrl;
		private Map<String, String> properties = new HashMap<String, String>();
		private boolean freeSearchSupported;

		public RestCapabilities(String baseUrl) throws IDMapperException {
			this.baseUrl = baseUrl;

			try {
				loadProperties();
				loadFreeSearchSupported();
			} catch(IOException e) {
				throw new IDMapperException(e);
			}
		}

		private Set<DataSource> loadDataSources(String cmd) throws IOException {
			Set<DataSource> results = new HashSet<DataSource>();

			URL url = new URL(baseUrl + "/" + cmd);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String line = null;
			while((line = in.readLine()) != null) {
				results.add(DataSource.getByFullName(line));
			}
			in.close();
			return results;
		}

		private void loadFreeSearchSupported() throws IOException {
			URL url = new URL(baseUrl + "/isFreeSearchSupported");
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String line = null;
			while((line = in.readLine()) != null) {
				freeSearchSupported = Boolean.parseBoolean(line);
			}
			in.close();
		}

		private void loadProperties() throws IOException {
			URL url = new URL(baseUrl + "/properties");
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String line = null;
			while((line = in.readLine()) != null) {
				String[] cols = line.split("\t");
				properties.put(cols[0], cols[1]);
			}
			in.close();
		}

		/** {@inheritDoc} */
		public Set<String> getKeys() {
			return properties.keySet();
		}

		/** {@inheritDoc} */
		public String getProperty(String key) {
			return properties.get(key);
		}

		/** {@inheritDoc} */
		public Set<DataSource> getSupportedSrcDataSources()
		throws IDMapperException {
			try {
				return loadDataSources("sourceDataSources");
			} catch(IOException e) {
				throw new IDMapperException(e);
			}
		}

		/** {@inheritDoc} */
		public Set<DataSource> getSupportedTgtDataSources()
		throws IDMapperException {
			try {
				return loadDataSources("targetDataSources");
			} catch(IOException e) {
				throw new IDMapperException(e);
			}
		}

		/** {@inheritDoc} */
		public boolean isFreeSearchSupported() {
			return freeSearchSupported;
		}

		/** {@inheritDoc} */
		public boolean isMappingSupported(DataSource src, DataSource tgt)
		throws IDMapperException {
			try {
				boolean supported = false;

				URL url = new URL(baseUrl + "/isMappingSupported/" + src.getSystemCode() + "/" + tgt.getSystemCode());
				BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
				String line = null;
				while((line = in.readLine()) != null) {
					supported = Boolean.parseBoolean(line);
				}
				in.close();
				return supported;
			} catch(IOException e) {
				throw new IDMapperException(e);
			}
		}
	}

	private final String baseUrl;

	private final IDMapperCapabilities capabilities;

	/**
	 * @param baseUrl base Url, e.g. http://webservice.bridgedb.org/Human or
	 * 	http://localhost:8182
	 * @throws IDMapperException when service is unavailable 
	 */
	BridgeRest (String baseUrl) throws IDMapperException
	{
		this.baseUrl = baseUrl;

		//Get the capabilities
		capabilities = new RestCapabilities(baseUrl);
	}

	private boolean isConnected = true;

	/** {@inheritDoc} */
	public void close() throws IDMapperException { isConnected = false; }

	/** {@inheritDoc} */
	public Set<Xref> freeSearch(String text, int limit)
	throws IDMapperException 
	{
		try
		{
			URL url = new URL (baseUrl + "/search/" + text);
			Set<Xref> result = new HashSet<Xref>();
			result.addAll (parseRefs(url));
			return result;
		}
		catch (IOException ex) {
			throw new IDMapperException (ex);
		} 
	}

	/** {@inheritDoc} */
	public IDMapperCapabilities getCapabilities() {
		return capabilities;
	}

	/** {@inheritDoc} */
	public boolean isConnected() {
		return isConnected;
	}

	/** {@inheritDoc} */
	public Map<Xref, Set<Xref>> mapID(Collection<Xref> srcXrefs,
			DataSource... tgtDataSources) throws IDMapperException {
		Map<Xref, Set<Xref>> result = new HashMap<Xref, Set<Xref>>();
		
		for(Xref x : srcXrefs) {
			result.put(x, mapID(x, tgtDataSources));
		}
		
		return result;
	}

	/** {@inheritDoc} */
	public Set<Xref> mapID(Xref src,
			DataSource... tgtDataSources) throws IDMapperException 
			{
		Set<DataSource> dsFilter = new HashSet<DataSource>();
		if(tgtDataSources != null) dsFilter.addAll(Arrays.asList(tgtDataSources));

		try
		{
			URL url = new URL(baseUrl + "/xrefs/" + src.getDataSource().getSystemCode() + "/" + src.getId());
			Set<Xref> result = new HashSet<Xref>();
			for (Xref dest : parseRefs(url))
			{
				if (dsFilter.size() == 0 || dsFilter.contains(dest.getDataSource()))
				{
					result.add (dest);
				}
			}
			return result;
		}
		catch (IOException ex)
		{
			throw new IDMapperException(ex);
		}
			}

	/** 
	 * helper, opens url and parses Xrefs.
	 * Xrefs are expected as one per row, id &lt;tab> system
	 * @param url URL to open
	 * @return parsed Xrefs
	 * @throws IOException */
	private List<Xref> parseRefs(URL url) throws IOException
	{
		List<Xref> result = new ArrayList<Xref>();
		BufferedReader reader; 
		try
		{
			reader = new BufferedReader(new InputStreamReader(openUrlHelper(url)));
		} catch (IOException ex) { return result; }
		String line;
		while ((line = reader.readLine()) != null)
		{
			String[] fields = line.split("\t");
			Xref dest = new Xref (fields[0], DataSource.getByFullName(fields[1]));
			result.add (dest);
		}
		return result;
	}

	/** {@inheritDoc} */
	public boolean xrefExists(Xref xref) throws IDMapperException {
		try {
			boolean exists = false;
			URL url = new URL(baseUrl + "/xrefExists/" + xref.getDataSource().getSystemCode() + "/" + xref.getId());
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String line = in.readLine();
			exists = Boolean.parseBoolean(line);
			in.close();
			return exists;
		} catch(IOException e) {
			throw new IDMapperException(e);
		}
	}

	/** {@inheritDoc} */
	public Map<Xref, String> freeAttributeSearch(String query, String attrType,
			int limit) throws IDMapperException {
		try {
			Map<Xref, String> result = new HashMap<Xref, String>();
			
			URL url = new URL (baseUrl + "/attributeSearch/" + query + "?limit=" + limit + "&attrName=" + attrType);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			while ((line = in.readLine()) != null) {
				String[] cols = line.split("\t", -1);
				Xref x = new Xref (cols[0], DataSource.getByFullName(cols[1]));
				String value = cols[2];
				result.put(x, value);
			}
			in.close();
			return result;
		} catch (IOException ex) {
			throw new IDMapperException (ex);
		} 
	}

	/** {@inheritDoc} */
	public Set<String> getAttributes(Xref ref, String attrType)
	throws IDMapperException {
		try {
			Set<String> results = new HashSet<String>();

			URL url = new URL (baseUrl + "/attributes/" + ref.getDataSource().getSystemCode() + "/" + 
					ref.getId() + "?attrName=" + attrType);

			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			while ((line = in.readLine()) != null) {
				results.add(line);
			}
			in.close();
			return results;
		} catch (IOException ex) {
			throw new IDMapperException (ex);
		} 
	}

	/** {@inheritDoc} */
	public Set<String> getAttributeSet() throws IDMapperException {
		try {
			Set<String> results = new HashSet<String>();

			URL url = new URL(baseUrl + "/attributeSet");

			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			while ((line = in.readLine()) != null) {
				results.add(line);
			}
			in.close();
			return results;
		} catch (IOException ex) {
			throw new IDMapperException (ex);
		} 
	}

	/** {@inheritDoc} */
	public Map<String, Set<String>> getAttributes(Xref ref)
	throws IDMapperException {
		try {
			Map<String, Set<String>> results = new HashMap<String, Set<String>>();

			URL url = new URL (baseUrl + "/attributes/" + ref.getDataSource().getSystemCode() + "/" + 
					ref.getId());
			
			BufferedReader in; 
			try
			{
				in = new BufferedReader(new InputStreamReader(openUrlHelper(url)));
			} catch (IOException ex) { return results; }

			String line;
			while ((line = in.readLine()) != null) {
				String[] cols = line.split("\t", -1);
				Set<String> rs = results.get(cols[0]);
				if(rs == null) results.put(cols[0], rs = new HashSet<String>());
				rs.add(cols[1]);
			}
			in.close();
			return results;
		} catch (IOException ex) {
			throw new IDMapperException (ex);
		} 
	}
	
	/**
	 * Open an InputStream to a given URL. For certain requests, when there are 0 results, the
	 * BridgeWebservice helpfully redirects to an error page instead of simply returning
	 * an empty list. Here we detect that situation and throw IOException.
	 * @return inputstream to given url.
	 * @param url url to open inputstream for.
	 * @throws IOException when there is a timeout, or when the http response code is not 200 - OK 
	 */
	private InputStream openUrlHelper(URL url) throws IOException
	{
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setInstanceFollowRedirects(false);
		int response = con.getResponseCode();
		if (response != HttpURLConnection.HTTP_OK) 
			throw new IOException("HTTP response: " + con.getResponseCode() + " - " + con.getResponseMessage());
		return con.getInputStream();
	}
}
