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
package org.bridgedb.tools.qc;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.bridgedb.DataSource;
import org.bridgedb.DataSourcePatterns;
import org.bridgedb.IDMapperException;
import org.bridgedb.bio.BioDataSource;
import org.bridgedb.rdb.construct.DBConnector;
import org.bridgedb.rdb.construct.DataDerby;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

/**
 * Script to check the Id's of one or more derby databases against the patterns registerd in BioDataSources.
 * <p>
 * This will check all Id's against the patterns, count how many do not match, and print a few example id's
 * that do not match.
 * <p>
 * Not to be confused with the BridgeQC script, which really compares the contents of two databases. 
 */
public class PatternChecker 
{
	private Multiset<DataSource> allMisses = new HashMultiset<DataSource>();
	private Multiset<DataSource> allTotals = new HashMultiset<DataSource>();
	
	public void run (File f) throws SQLException, IDMapperException
	{
		String database = "" + f;
		//TODO: we can use the new Iterator interface here...
		DBConnector con = new DataDerby();
		Connection sqlcon = null;
		sqlcon = con.createConnection(database, 0); 
		
		Multimap<DataSource, String> missExamples = new HashMultimap<DataSource, String>();
		Multiset<DataSource> misses = new HashMultiset<DataSource>();
		Multiset<DataSource> totals = new HashMultiset<DataSource>();
		Map<DataSource, Pattern> patterns = DataSourcePatterns.getPatterns();

//		String url = "jdbc:derby:jar:(" + f + ")database";
//		IDMapperRdb gdb = SimpleGdbFactory.createInstance("" + f, url);
		
		Statement st = sqlcon.createStatement();
		ResultSet rs = st.executeQuery("select id, code from datanode");
		
		while (rs.next())
		{
			String id = rs.getString(1);
			DataSource ds = DataSource.getBySystemCode(rs.getString(2));
			if (patterns.get(ds) == null) continue; // skip if there is no pattern defined.
			
			Set<DataSource> matches = DataSourcePatterns.getDataSourceMatches(id);
			if (!matches.contains(ds))
			{
				if (missExamples.get(ds).size() < 10) missExamples.put(ds, id);
				misses.add (ds);
			}
			totals.add (ds);
		}
			
			
//			String code = rs.getString (2);
			//System.out.println (id + "\t" + code);
			
		for (DataSource ds : totals.elementSet())
		{
			int miss = misses.count(ds);
			int total = totals.count(ds);
			
			if (miss > 0)
			{
				String severity = miss < (total / 25) ? "WARNING" : "ERROR";
				System.out.println (severity + ": " + miss + "/" + total + " (" +
						miss * 100 / total + "%) ids do not match expected pattern for " + ds);
				System.out.println (severity + ": expected pattern is '" + patterns.get(ds) + "'");
				boolean first = true;
				for (String id : missExamples.get(ds))
				{
					System.out.print (first ? severity + ": aberrant ids are e.g. " : ", ");
					first = false;
					System.out.print ("'" + id + "'");
				}
				System.out.println();
			}
		}
			
		allMisses.addAll(misses);
		allTotals.addAll(totals);
	}	

	/** 
	 * when the script is run on mutliple databases in one go, finalReport will give a summary
	 * across databases 
	 */
	private void finalReport()
	{
		System.out.println ("=========== FINAL REPORT OF ID PATTERNS =============");
		for (DataSource ds : allTotals.elementSet())
		{
			int miss = allMisses.count(ds);
			int total = allTotals.count(ds);
			System.out.println (ds + "\t" + miss + "\t" + total + "\t" +
						miss * 100 / total + "%");
		}
	}
	
	/**
	 * Script can be run in two ways
	 * 1) as part of BridgeQC, to check a single database. Pass one argument with a derby database filename.
	 * 2) standalone, to check a set of databases. Specify each database on the command line separately.
	 */
	public static void main (String[] args) throws IDMapperException, SQLException
	{
		BioDataSource.init();
		PatternChecker checker = new PatternChecker();
		
		if (args.length == 0)
		{
			System.err.println ("Argument expected: pgdb file to check");
			System.exit(1);
		}
		for (String arg : args)
		{
			File f = new File (arg);
			checker.run(f);
		}		
		if (args.length > 1)
		{
			checker.finalReport();
		}
	}

}
