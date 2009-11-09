package org.bridgedb.examples;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bridgedb.DataSource;
import org.bridgedb.DataSourcePatterns;
import org.bridgedb.IDMapperException;
import org.bridgedb.bio.BioDataSource;

public class ExGuessing 
{

	public static void main(String args[]) throws ClassNotFoundException, IDMapperException
	{
		// This example shows how to guess the type of an identifier
		
		// We have to initialize DataSource information,
		// but we don't need a driver
		BioDataSource.init();
		
		String query = "NP_036430";
		System.out.println ("Which patterns match " + query + "?");
		
		// DataSourcePatterns holds a registry of patterns
		Map<DataSource, Pattern> patterns = DataSourcePatterns.getPatterns();
		
		// loop over all patterns
		for (DataSource key : patterns.keySet())
		{
			// create a matcher for this pattern
			Matcher matcher = patterns.get(key).matcher(query);
			
			// see if the input matches, and print a message
			if (matcher.matches())
			{
				System.out.println (key.getFullName() + " matches!");
			}
		}		
	}
	
}
