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
package org.bridgedb;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
	 *   "idmapper-pgdb", and location is for example an url or a file, depending
	 *   on the protocol.<BR>
	 *   Note that you need to load the driver that implements the protocol first by loading its
	 *   class with Class.forName(). E.g. for idmapper-pgdb, the class "org.bridgedb.rdb.IDMapperRdb" needs
	 *   to be loaded.
	 * @return the newly instantiated IDMapper
	 * @throws IDMapperException when the right IDMapper implementation could not
	 *   be instantiated, or when the connection string is not formatted correctly
	 */
	public static IDMapper connect(String connectionString) throws IDMapperException
	{
		int pos = connectionString.indexOf(":");
		if (pos < 0) throw new IDMapperException("connection String must be of the form 'protocol:location'");
		String protocol = connectionString.substring(0, pos);
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
	
	/**
	 * Used by {@link Driver} implementations to register themselves and make
	 * themselves available to the world.
	 * @param protocol The protocol (part before ":" in connection string) that this Driver is for.
	 * @param driver An instance of the Driver.
	 */
	public static void register(String protocol, Driver driver)
	{
		drivers.put(protocol, driver);
	}

    private static volatile String version;

    /**
     * Returns the version of BridgeDb.
     */
    public static String getVersion() {
        if (version != null) return version;
        try (InputStream stream = BridgeDb.class.getResourceAsStream("/version.props")) {
            Properties props = new Properties();
            props.load(stream);
            version = props.getProperty("bridgedb.version");
            return version;
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

}
