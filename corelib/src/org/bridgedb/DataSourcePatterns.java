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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains regular expression patterns for identifiers
 * Can be used to guess the BioDataSource of an identifier
 * of unknown origin.
 */
public class DataSourcePatterns 
{
	protected static Map<DataSource, Pattern> patterns = new HashMap<DataSource, Pattern>();
	
	public static void registerPattern (DataSource key, Pattern value)
	{
		patterns.put (key, value);
	}
	
	/**
	 * Convenience method. 
	 * Returns a set of patterns which matches the given id.
	 */
	public static Set<DataSource> getDataSourceMatches (String id)
	{
		Set<DataSource> result = new HashSet<DataSource>();
		for (DataSource ds : patterns.keySet())
		{
			Matcher m = patterns.get(ds).matcher(id);					
			if (m.matches()) result.add (ds);			
		}
		return result;
	}
	
	/**
	 * Return all known data patterns, mapped to
	 * their BioDataSource.
	 * For example, this map will contain:
	 *    BioDataSource.ENSEMBL -> Pattern.compile("ENSG\d+")
	 *    
	 * There is not guaranteed to be a Pattern for every
	 * BioDataSource constant.
	 */
	public static Map<DataSource, Pattern> getPatterns()
	{
		return patterns;
	}
}
