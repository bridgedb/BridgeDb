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
package org.bridgedb.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * To prevent duplication and redundancy, functions that are common to 
 * multiple IDMapper implementations
 * can be placed here.
 * <p>
 * <b>Warning!</b> This class is not part of the public API of BridgeDb. Methods in this class
 * may disappear or change in backwards-incompatible ways. <b>This class should not be used by applications!</b>
 */
public final class InternalUtils 
{
	/** private constructor to prevent instantiation. */
	private InternalUtils() {}
	
	/**
	 * call the "single" mapID (Xref, ...) multiple times
	 * to perform mapping of a Set.
	 * <p> 
	 * This is intended for IDMappers that don't gain any advantage of mapping
	 * multiple ID's at a time. They can implement mapID(Xref, ...), and
	 * use mapMultiFromSingle to simulate mapID(Set, ...)
	 * @param mapper used for performing a single mapping
	 * @param srcXrefs xrefs to translate
	 * @param tgt DataSource(s) to map to, optional.
	 * @return mappings with the translated result for each input Xref. Never returns null, but
	 *    not each input is a key in the output map.
	 * @throws IDMapperException when mapper.mapID throws IDMapperException
	 */
	public static Map<Xref, Set<Xref>> mapMultiFromSingle(IDMapper mapper, Collection<Xref> srcXrefs, DataSource... tgt)
		throws IDMapperException
	{
		final Map<Xref, Set<Xref>> result = new HashMap<Xref, Set<Xref>>();
		for (Xref src : srcXrefs)
		{
			final Set<Xref> refs = mapper.mapID(src, tgt);
			if (refs.size() > 0)
				result.put (src, refs);
		}
		return result;
	}

	/**
	 * call the "multi" mapID (Set, ...) using a Set with one item
	 * to perform mapping of a single ID.
	 * <p> 
	 * This is intended for IDMappers that have a performance advantage of mapping
	 * multiple ID's at a time. They can implement mapID(Set, ...), and
	 * use mapSingleFromMulti to simulate mapID(Xref, ...)
	 * @param mapper used for performing a multi-mapping
	 * @param src xref to translate
	 * @param tgt DataSource(s) to map to, optional.
	 * @return Set of translated Xrefs, or an empty set if none were found.
	 * @throws IDMapperException when mapper.mapID throws IDMapperException
	 */
	public static Set<Xref> mapSingleFromMulti(IDMapper mapper, Xref src, DataSource... tgt)
		throws IDMapperException
	{
		Set<Xref> srcXrefs = new HashSet<Xref>();
		srcXrefs.add(src);
		Map<Xref, Set<Xref>> mapXrefs = mapper.mapID(srcXrefs, tgt);
		if (mapXrefs.containsKey(src))
			return mapXrefs.get(src);
		else
			return Collections.emptySet();
	}
	
	/**
	 * parse configuration params of the connection string. Connection strings are
	 * expected to have a formatting like @code{base?arg1=val&arg2=val}.
	 * @param location configuration string to parse.
	 * @param allowedParams allowed argument names to appear before =
	 * @return key / value Map of configuration arguments. The base (the part before the ?)
	 * 	is returned in the special key "BASE". If the part before ? is empty, 
	 *  the "BASE" key is not created.
	 * @throws IllegalArgumentException if arguments do not follow the key=val structure, or 
	 * 	if the key is not in allowedParams
	 */
	public static Map<String, String> parseLocation (String location, String... allowedParams)
	{
		Map<String, String> result = new HashMap<String, String>();
		Set<String> allowedSet = new HashSet<String>(Arrays.asList(allowedParams));
		
		String param = location;
		
		int idx = location.indexOf('?');
		if (idx > -1) 
		{
			// do not add empty string.
			if (idx > 0) 
				result.put ("BASE", location.substring(0,idx));

			param = location.substring(idx+1);
		}
		
		if ("".equals(param)) return result;
		
		String[] args = param.split ("&");
		for (String arg : args)
		{
			idx = arg.indexOf("=");
			if (idx > -1)
			{
				String key = arg.substring (0, idx);
				if (!allowedSet.contains(key))
				{
					throw new IllegalArgumentException("Unexpected property '" + key + "'");
				}
				String val = arg.substring (idx + 1);
				result.put (key, val);
			}
			else
			{
				throw new IllegalArgumentException("Could not parse argument " + arg + 
						". Expected key=val format");
			}
		}
		
		return result;
	}

    public static InputStream getInputStream(String source) throws IOException {
        URL url = new URL(source);
        return getInputStream(url);
    }
    
    private static final int MS_CONNECTION_TIMEOUT = 2000;
    //TODO: test when IOException is thrown
    
    /**
     * Start downloading a file from the web and open an InputStream to it.
     * @param source location of file to download.
     * @return InputStream
     * @throws IOException after a number of attempts to connect to the remote server have failed.
     */
    public static InputStream getInputStream(URL source) throws IOException {
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

    /**
     * Generic method for multimaps, a map that can contain multiple values per key.
     * Unlike a regular map, if you insert a value for a key that already exists, the previous value 
     * will not be discared. 
     * @param <T> key type of multimap
     * @param <U> value type of multimap
     * @param map multimap to work on
     * @param key key of the value to insert
     * @param val value to insert.
    */
    public static <T, U> void multiMapPut(Map<T, Set<U>> map, T key, U val)
    {
    	Set<U> set;
    	if (map.containsKey (key))
    	{
    		set = map.get(key);
    	}
    	else
    	{
    		set = new HashSet<U>();
    		map.put(key, set);
    	}
    	set.add (val);
    }

    /**
     * Generic method for multimaps, a map that can contain multiple values per key.
     * Unlike a regular map, if you insert a value for a key that already exists, the previous value 
     * will not be discarded.
     * <p>
     * This is like multiMapPut, but uses a list instead of a set for each value of the map. 
     * @param <T> key type of multimap
     * @param <U> value type of multimap
     * @param map multimap to work on
     * @param key key of the value to insert
     * @param val value to insert.
    */
    public static <T, U> void multiMapAdd(Map<T, List<U>> map, T key, U val)
    {
    	List<U> list;
    	if (map.containsKey (key))
    	{
    		list = map.get(key);
    	}
    	else
    	{
    		list = new ArrayList<U>();
    		map.put(key, list);
    	}
    	list.add (val);
    }

    /**
     * Generic method for multimaps, a map that can contain multiple values per key.
     * Unlike a regular map, if you insert a value for a key that already exists, the previous value 
     * will not be discarded.
     * <p>
     * multiMapPutAll let's you insert a collection of items at once. 
     * @param <T> key type of multimap
     * @param <U> value type of multimap
     * @param map multimap to work on
     * @param key key of the value to insert
     * @param vals values to insert.
     */
    public static <T, U> void multiMapPutAll(Map<T, Set<U>> map, T key, Collection<U> vals)
    {
    	Set<U> set;
    	if (map.containsKey (key))
    	{
    		set = map.get(key);
    	}
    	else
    	{
    		set = new HashSet<U>();
    		map.put(key, set);
    	}
    	set.addAll (vals);
    }

    /**
     * Split a heterogeneous {@link Xref} set into multiple homogeneous Xref sets.
     * <p>
     * If the input contains {L:3643, L:1234, X:1004_at, X:1234_at},
     * then the output will contain
     * { L=> {L:3643, L:1234}, X=> {X:1004_at, X:1234_at} }.
     * @param srcXrefs the set to split
     * @return map with datasources as keys and homogeneous sets as values.
     */
	public static Map<DataSource, Set<Xref>> groupByDataSource(Collection<Xref> srcXrefs)
	{
		Map<DataSource, Set<Xref>> result = new HashMap<DataSource, Set<Xref>>();
		for (Xref ref : srcXrefs) 
		{  
			multiMapPut(result, ref.getDataSource(), ref);
		}
		return result;
	}
	
	/**
	 * Join the ID part of a collection of Xrefs with a custom separator.
	 * @param refs Xrefs from which the ids will be concatenated
	 * @param sep separator string.
	 * @return concatenation of the ids.
	 */
	public static String joinIds (Collection<Xref> refs, String sep)
	{
		boolean first = true;
		StringBuilder builder = new StringBuilder();
		for (Xref ref : refs)
		{
			if (!first) builder.append (sep);
			builder.append (ref.getId());
			first = false;
		}
		return builder.toString();
	}

	/** read a configuration file in the bridgedb xml format */
	public static void readXmlConfig(InputSource is) throws ParserConfigurationException, SAXException, IOException
	{	
		SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        SAXParser saxParser = spf.newSAXParser();

        XMLReader xmlReader = saxParser.getXMLReader();
        xmlReader.setContentHandler(new ConfigXmlHandler());
        xmlReader.parse(is);
	}
	
	private static class ConfigXmlHandler extends DefaultHandler
	{
		DataSource current = null;
		
		@Override
		public void startElement(String namespaceURI, String localName,
				String qName, Attributes atts)
		throws SAXException
		{
			if ("datasource".equals (localName))
			{
				String fullname = atts.getValue("fullname");
				if (fullname == null) throw new SAXException ("missing attribute fullname");
				current = DataSource.getByFullName(fullname);
			}
			
			if ("alias".equals (localName))
			{
				String alias = atts.getValue ("name");
				if (alias != null && current != null)
					current.registerAlias(alias);
			}
		}
		
		@Override
		public void endElement(String namespaceURI, String localName, String qName) throws SAXException
		{
			if ("datasource".equals (localName))
			{
				current = null;
			}
		}

	}

}
