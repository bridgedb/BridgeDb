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
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bridgedb.AttributeMapper;
import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.impl.InternalUtils;
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
                	// replace all spaces by "%20" to access organisms such as "Arabidopsis theliana"
			return new BridgeRest(location.replaceAll(" ", "%20"));
		}
	}

	private final class RestCapabilities implements IDMapperCapabilities {
		private Map<String, String> properties = new HashMap<String, String>();
		private boolean freeSearchSupported;

		/** 
		 * Capabilities for the BridgeRest IDMapper. 
		 * @throws IDMapperException when the webservice was not available.
		 */
		public RestCapabilities() throws IDMapperException {
			try {
				loadProperties();
				loadFreeSearchSupported();
			} catch(IOException e) {
				throw new IDMapperException(e);
			}
		}

		/** 
		 * Helper method, reads a list of supported datasources from the webservice.
		 * @param cmd can be sourceDataSources or targetDataSources
		 * @return Set of DataSources
		 * @throws IDMapperException when service is unavailable.
		 */
		private Set<DataSource> loadDataSources(String cmd) throws IDMapperException
		{
			try
			{
				Set<DataSource> results = new HashSet<DataSource>();
	
				BufferedReader in = new UrlBuilder (cmd).openReader();
				String line = null;
				while((line = in.readLine()) != null) {
					results.add(DataSource.getByFullName(line));
				}
				in.close();
				return results;
			}
			catch (IOException ex)
			{
				throw new IDMapperException (ex);
			}
		}

		/** 
		 * Helper method, checks if free search is supported by the webservice.
		 * @throws IOException when service is unavailable.
		 */
		private void loadFreeSearchSupported() throws IOException {
			BufferedReader in = new UrlBuilder ("isFreeSearchSupported").openReader();
			String line = null;
			while((line = in.readLine()) != null) {
				freeSearchSupported = Boolean.parseBoolean(line);
			}
			in.close();
		}

		/** 
		 * Helper method, reads properties from the webservice.
		 * @throws IOException when service is unavailable.
		 */
		private void loadProperties() throws IOException {
			BufferedReader in = new UrlBuilder ("properties").openReader();
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
			throws IDMapperException 
		{
			if (supportedSrcDataSources==null) {
				supportedSrcDataSources = loadDataSources("sourceDataSources");
			}
			return supportedSrcDataSources;
		}

		/** {@inheritDoc} */
		public Set<DataSource> getSupportedTgtDataSources()
			throws IDMapperException 
		{
			if (supportedTgtDataSources==null) {
				supportedTgtDataSources = loadDataSources("targetDataSources");
			}
			return supportedTgtDataSources;
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
				BufferedReader in = new UrlBuilder("isMappingSupported")
					.ordered(src.getSystemCode(), tgt.getSystemCode()).openReader();
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

	private Set<DataSource> supportedSrcDataSources = null;
	private Set<DataSource> supportedTgtDataSources = null;
	private Set<String> attributeSet = null;

	/**
	 * Helper class for constructing URL of a BridgeRest webservice command.
	 */
	private final class UrlBuilder
	{
		private StringBuilder builder = new StringBuilder(baseUrl);
		
		/**
		 * Start building a new Url for the BridgeRest webservice.
		 * @param cmd the command, or the first parameter after the base Url. For example "properties".
		 * 	This param will be added after the baseUrl, separated by a "/"
		 */
		private UrlBuilder(String cmd)
		{
			builder.append ("/");
			builder.append (cmd);
		}
		
		/**
		 * Ordered, unnamed arguments.
		 * @param args Optional arguments, these will be URLencoded and separated by "/"
		 * @return this, for chaining purposes
		 * @throws IOException in case there is an encoding problem (really unlikely) 
		 */
		UrlBuilder ordered (String... args) throws IOException
		{
			for (String arg : args)
			{
				builder.append ("/");
				builder.append (URLEncoder.encode (arg, "UTF-8"));
			}
			return this;
		}

		private boolean hasQMark = false;
		
		/**
		 * Named parameters, these will be appended after ? and formatted as key=val pairs,
		 * separated by &
		 * @param key name of parameter
		 * @param val value of parameter
		 * @throws IOException in case there is an encoding problem (really unlikely) 
		 * @return this, for chaining purposes
		 */
		UrlBuilder named (String key, String val) throws IOException
		{
			if (!hasQMark)
			{
				builder.append ("?");
				hasQMark = true;
			}
			else
			{
				builder.append ("&");
			}
			builder.append (URLEncoder.encode (key, "UTF-8"));
			builder.append ("=");
			builder.append (URLEncoder.encode (val, "UTF-8"));
			return this;
		}

		/**
		 * Open an InputStream to a given URL. For certain requests, when there are 0 results, the
		 * BridgeWebservice helpfully redirects to an error page instead of simply returning
		 * an empty list. Here we detect that situation and throw IOException.
		 * @return inputstream to given url.
		 * @throws IOException when there is a timeout, or when the http response code is not 200 - OK 
		 */
		private BufferedReader openReader() throws IOException
		{
			URL url = new URL (builder.toString()); 
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setInstanceFollowRedirects(false);
			int response = con.getResponseCode();
			if (response < HttpURLConnection.HTTP_OK || response >= HttpURLConnection.HTTP_MULT_CHOICE ) 
				throw new IOException("HTTP response: " + con.getResponseCode() + " - " + con.getResponseMessage());
			return new BufferedReader(new InputStreamReader(con.getInputStream()));
		}
	}
	
	/**
	 * @param baseUrl base Url, e.g. http://webservice.bridgedb.org/Human or
	 * 	http://localhost:8182
	 * @throws IDMapperException when service is unavailable 
	 */
	BridgeRest (String baseUrl) throws IDMapperException
	{
		this.baseUrl = baseUrl;

		//Get the capabilities
		capabilities = new RestCapabilities();
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
			BufferedReader r = new UrlBuilder ("search").ordered(text).openReader();
			Set<Xref> result = new HashSet<Xref>();

			{
				Xref dest;
				while ((dest = parseLine(r)) != null) result.add (dest);
			}
			
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
			DataSource... tgtDataSources) throws IDMapperException 
	{
		return InternalUtils.mapMultiFromSingle(this, srcXrefs, tgtDataSources);
	}

	/** {@inheritDoc} */
	public Set<Xref> mapID(Xref src,
			DataSource... tgtDataSources) throws IDMapperException 
			{
		Set<DataSource> dsFilter = new HashSet<DataSource>();
		if(tgtDataSources != null) dsFilter.addAll(Arrays.asList(tgtDataSources));

		try
		{
			UrlBuilder builder = new UrlBuilder ("xrefs")
				.ordered(src.getDataSource().getSystemCode(), src.getId());
			if (tgtDataSources.length == 1) 
				builder = builder.named("dataSource", tgtDataSources[0].getSystemCode());
			BufferedReader r = builder.openReader();
			Set<Xref> result = new HashSet<Xref>();
			{
				Xref dest;
				while ((dest = parseLine(r)) != null)
				{
					if (dsFilter.size() == 0 || dsFilter.contains(dest.getDataSource()))
					{
						result.add (dest);
					}
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
	 * parse a single line of input.
	 * @param reader reader to read line from
	 * @return Xref or null if end of stream was reached
	 * @throws IOException if there was an error while reading (not EOF!) */
	private Xref parseLine(BufferedReader reader) throws IOException
	{
		StringBuilder builder = new StringBuilder();
		String id = null;
		int c;
		while (true)
		{
			// read char by char and use switch statement
			// for efficiency
			c = reader.read();
			switch (c)
			{
			case -1:
				return null; // end of the stream
			case '\n': 
				DataSource ds = DataSource.getByFullName(builder.toString());
				return new Xref(id, ds);
			case '\t':
				id = builder.toString();
				builder = new StringBuilder();
				break;
			default:
				builder.append((char)c);
			}
		}
	}

	/** {@inheritDoc} */
	public boolean xrefExists(Xref xref) throws IDMapperException {
		try {
			boolean exists = false;
			BufferedReader in = new UrlBuilder ("xrefExists")
				.ordered(xref.getDataSource().getSystemCode(), xref.getId())
				.openReader();
			String line = in.readLine();
			exists = Boolean.parseBoolean(line);
			in.close();
			return exists;
		} catch(IOException e) {
			throw new IDMapperException(e);
		}
	}

	/**
	 *
	 * @return true
	 */
	public boolean isFreeAttributeSearchSupported()
	{
		return true;
	}

	/** {@inheritDoc} */
	public Map<Xref, String> freeAttributeSearch(String query, String attrType,
			int limit) throws IDMapperException {
		try {
			Map<Xref, String> result = new HashMap<Xref, String>();
			
			BufferedReader in = new UrlBuilder("attributeSearch")
				.ordered (query).named("limit", "" + limit)
				.named ("attrName", attrType)
				.openReader();
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

			BufferedReader in = new UrlBuilder ("attributes")
				.ordered(ref.getDataSource().getSystemCode(), ref.getId())
				.named ("attrName", attrType)
				.openReader();

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
		if (attributeSet==null) {
			try {
				Set<String> results = new HashSet<String>();

				BufferedReader in = new UrlBuilder ("attributeSet")
					.openReader();
				String line;
				while ((line = in.readLine()) != null) {
					results.add(line);
				}
				in.close();
				attributeSet = results;
			} catch (IOException ex) {
				throw new IDMapperException (ex);
			}
                }

                return attributeSet;
	}

	/** {@inheritDoc} */
	public Map<String, Set<String>> getAttributes(Xref ref)
	throws IDMapperException {
		try {
			Map<String, Set<String>> results = new HashMap<String, Set<String>>();

			BufferedReader in = new UrlBuilder ("attributes")
				.ordered(ref.getDataSource().getSystemCode(), ref.getId())
				.openReader();
			
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
	
}
