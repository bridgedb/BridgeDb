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
package org.bridgedb.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

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
		
		String[] args = param.split ("&");
		for (String arg : args)
		{
			idx = param.indexOf("=");
			if (idx > -1)
			{
				String key = arg.substring (0, idx - 1);
				if (!allowedSet.contains(key))
				{
					throw new IllegalArgumentException("Unexpected property '" + key + "'");
				}
				String val = arg.substring (idx);
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

}
