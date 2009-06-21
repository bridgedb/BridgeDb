// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2009 BiGCaT Bioinformatics
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
package org.bridgedb;

import java.util.HashMap;
import java.util.Map;

/**
 * Central access point for connecting to IDMappers.
 */
public final class BridgeDb
{
	private static Map<String, Driver> drivers = new HashMap<String, Driver>();
	
	/** Private, to prevent instantiation of Utility class. */
	private BridgeDb() {}
	
	/**
	 * Finds the correct implementation of the {@link IDMapper} interface and instantiates it.
	 * @param connectionString used to configure a mapping resource. The connectionString
	 *   has the form "protocol:location", where protocol can be e.g. "idmapper-text" or
	 *   "idmapper-derby", and location is for example an url or a file, depending
	 *   on the protocol.
	 * @return the newly instantiated IDMapper
	 * @throws IDMapperException when the right IDMapper implementation could not
	 *   be instantiated 
	 */
	public static IDMapper connect(String connectionString) throws IDMapperException
	{
		int pos = connectionString.indexOf(":");
		if (pos < 0) throw new IllegalArgumentException("connection String must be of the form 'protocol:location'");
		String protocol = connectionString.substring(0, pos - 1);
		String location = connectionString.substring(pos + 1);
		
		if (drivers.containsKey(protocol))
		{
			return drivers.get(protocol).connect(location);
		}
		else
		{
			throw new IDMapperException ("Unknown protocol: " + protocol);
		}
	}
	
	public static void register(String protocol, Driver driver)
	{
		drivers.put(protocol, driver);
	}
}
