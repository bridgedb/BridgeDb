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
package org.bridgedb.rdb.mysql;

/**
 * MySQL plugin for org.bridgedb.rdb. Loading this class loads the MySQL JDBC driver, so that
 * the protocol idmapper-jdbc of org.bridgedb.rdb can connect to MySQL and MariaDB databases,
 * as in idmapper-jdbc:mysql://host/database.
 * <p>
 * Loading org.bridgedb.rdb.IDMapperRdb also loads this class, if it is available.
 */
public final class IDMapperMySQL
{
	private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

	// Must not initialize IDMapperRdb, as that class initializes this one.
	static
	{
		try
		{
			// not found automatically in OSGi, nor in fat jars with several JDBC drivers
			Class.forName(DRIVER);
		}
		catch (ClassNotFoundException e)
		{
			// must not break org.bridgedb.rdb, so this is only reported when connecting
		}
	}

	/** private constructor to prevent instantiation. */
	private IDMapperMySQL() { }
}
