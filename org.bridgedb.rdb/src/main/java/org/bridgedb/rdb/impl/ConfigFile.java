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
package org.bridgedb.rdb.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bridgedb.IDMapperException;
import org.bridgedb.impl.InternalUtils;
import org.bridgedb.rdb.DbFormatDetector;

public class ConfigFile
{
	public ConfigFile(File f) throws IOException
	{
		System.out.println("Parsing gene database configuration: " + f.getAbsolutePath());

		parse(f);
	}
	
	private Map<String, List<String>> mappers = new HashMap<String, List<String>>();
	List<String> drivers = new ArrayList<String>();
	
	private void parse(File f) throws IOException
	{
		String section = "MAIN";
		BufferedReader in = new BufferedReader(new FileReader(f));
		String line = in.readLine();
		while(line != null) 
		{
			// strip comments
			int pos = line.indexOf('#');
			if (pos > -1)
				line = line.substring(0, pos);

			// strip whitespace, also the whitespace before a comment
			line = line.trim();

			// ignore empty lines
			if (line.equals("")) {}
			// section header
			else if (line.matches("^\\[.*\\]\\s*$"))
			{				
				section = line.trim().substring(1, line.length()-1); 
			}
			// regular line
			else
			{
				// handle differently for each section
				if (section.equalsIgnoreCase("MAIN"))
				{	
					String[] kv = line.split("\t");
					if(kv.length == 2) 
					{
						String key = kv[0];
						String value = kv[1];

						// a plain path instead of a connection string: add the protocol
						// that matches the format of the database file
						if (!value.startsWith("idmapper")) value = protocolFor(value) + value;

						InternalUtils.multiMapAdd(mappers, key, value);
					} else {
						System.out.println("Invalid key/value pair in gene database configuration: " + line);
					}
				} 
				else if (section.equalsIgnoreCase("drivers"))
				{
					drivers.add(line);
				}
				else
				{
					System.out.println ("Warning: Unknown section [" + section + "]");
				}
			}
			line = in.readLine();
		}
		in.close();

	}
	
	/**
	 * Returns the BridgeDb protocol for a database file, based on its format:
	 * "idmapper-pgdb:" for Derby and "idmapper-duckdb:" for DuckDB. If the format
	 * can't be detected, for example because the file doesn't exist, this returns
	 * "idmapper-pgdb:", the protocol that was used for all plain paths before
	 * BridgeDb 4.0.2, and connecting will report the problem.
	 */
	static String protocolFor(String path)
	{
		try
		{
			switch (DbFormatDetector.detect(new File(path)))
			{
			case DUCKDB:
				return "idmapper-duckdb:";
			case DERBY:
			default:
				return "idmapper-pgdb:";
			}
		}
		catch (IDMapperException e)
		{
			System.out.println("Warning: " + e.getMessage() + "; trying it as a Derby database");
			return "idmapper-pgdb:";
		}
	}

	public List<String> getDrivers()
	{
		return drivers;
	}
	
	public Map<String, List<String>> getMappers()
	{
		return mappers;
	}
}