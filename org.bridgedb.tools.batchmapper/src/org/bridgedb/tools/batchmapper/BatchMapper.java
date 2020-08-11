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
package org.bridgedb.tools.batchmapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperStack;
import org.bridgedb.Xref;
import org.bridgedb.bio.DataSourceTxt;

public class BatchMapper 
{
	private static class Settings
	{
		File fInput = null;
		File fOutput = null;
		File fReport = null;
		List<String> connectStrings = new ArrayList<String>();
		DataSource is = null;
		DataSource os = null;
		int inputColumn = 0; 
		int verbose = 0; // 0, 1 or 2
		int mode = 0; // 0 or 1
		int multiMap = 0; // 0 or 1
	}
	
	public static void main(String[] args)
	{
		BatchMapper mapper = new BatchMapper();
		mapper.run(args);
	}
	
	public void printUsage()
	{
		String version = "";
		try
		{
			Properties props = new Properties();
			props.load (BridgeDb.class.getResourceAsStream("BridgeDb.properties"));
			version = props.getProperty("bridgedb.version") + 
				" (r" + props.getProperty("REVISION") + ")";
		}
		catch (IOException ex) { version = ex.getMessage(); } 
		System.out.println ("BatchMapper version " + version);
		System.out.print (
				"BatchMapper is a tool for mapping biological identifiers.\n" +
				"Usage:\n"+
				"	batchmapper -ls \n" +
				"		List system codes \n" +
				" or\n" +
				"	batchmapper \n" +
				"		[-v|-vv] \n" +
				"		[-mm] \n" +
				"		[-g <gene database>] \n " +
				"		[-t <biomart text file>] \n " +
				"		[-i <input file>] \n" +
				"		-is <input system code or datasource name> \n" +
				"		-os <output system code or datasource name> \n" +
				"		[-o <output file>] \n" +
				"		[-c <input column, 0-based>]\n" +
				"		[-r <report file>] \n" +
				"\n" +
				"You should specify at least one -g or -t option.\n" +
				"Multiple -g or -t options will be combined transitively.\n");
	}
	
	private DataSource dsFromArg(String arg)
	{
		for (DataSource ds : DataSource.getDataSources())
		{
			if (arg.equals (ds.getSystemCode()) 
					|| arg.equals(ds.getFullName()))
			{
				return ds;
			}
		}
		System.out.println ("WARNING: " + arg + " is not a standard system code or DataSource name");
		return DataSource.getByFullName(arg);
	}
	
	public String parseArgs(Settings settings, String[] args)
	{
		int pos = 0;
		while (pos < args.length)
		{
			if (args[pos].equals ("-ls"))
			{
				settings.mode = 1;
			} 
			else if (args[pos].equals ("-v"))
			{
				settings.verbose = 1;
			} 
			else if (args[pos].equals("-vv"))
			{
				settings.verbose = 2;
			}
			else if (args[pos].equals("-g"))
			{
				pos++;
				if (pos > args.length) return "File expected after -g";
				File f = new File (args[pos]);
				if (!f.exists()) return "File " + args[pos] + " does not exist";
				settings.connectStrings.add ("idmapper-pgdb:" + f.getAbsolutePath());
			}
			else if (args[pos].equals("-t"))
			{
				pos++;
				if (pos > args.length) return "File expected after -t";
				File f = new File (args[pos]);
				if (!f.exists()) return "File " + args[pos] + " does not exist";
				try
				{
					settings.connectStrings.add ("idmapper-text:" + f.toURL());
				}
				catch (MalformedURLException ex)
				{
					return ex.getMessage();
				}
			}
			else if (args[pos].equals("-i"))
			{
				pos++;
				if (pos > args.length) return "File expected after -i";
				settings.fInput = new File (args[pos]);
				if (!settings.fInput.exists()) return "File " + args[pos] + " does not exist";
			}
			else if (args[pos].equals("-r"))
			{
				pos++;
				if (pos > args.length) return "File expected after -r";
				settings.fReport = new File (args[pos]);
			}
			else if (args[pos].equals("-c"))
			{
				pos++;
				try
				{
					settings.inputColumn = Integer.parseInt (args[pos]);
				}
				catch (NumberFormatException ex)
				{
					return ex.getMessage();
				}
			}
			else if (args[pos].equals("-o"))
			{
				pos++;
				if (pos > args.length) return "File expected after -o";
				settings.fOutput = new File (args[pos]);
			}
			else if (args[pos].equals("-is"))
			{
				pos++;
				if (pos > args.length) return "System code expected after -is";
				settings.is = dsFromArg(args[pos]);
			}			
			else if (args[pos].equals("-os"))
			{
				pos++;
				if (pos > args.length) return "System code expected after -os";
				settings.os = dsFromArg(args[pos]);
			}
			else if (args[pos].equals("-mm"))
			{
				settings.multiMap = 1;
			}
			else
			{
				return "Unrecognized option " + args[pos];
			}
			pos++;
		}
		if (settings.mode == 1)
		{
			if (settings.is != null ||
				settings.os != null ||
				settings.connectStrings.size() > 0 ||
				settings.fInput != null ||
				settings.fOutput != null ||
				settings.inputColumn != 0 ||
				settings.multiMap != 0 ||
				settings.fReport != null)
			{
				return "-ls option can't be combined with -g, -t, -i, -is, -os, -o, -mm or -r options";
			}
		}
		else
		{
			if (settings.connectStrings.size() == 0) return "Missing -t or -g options";
			if (settings.is == null) return "Missing -is option";
			if (settings.os == null) return "Missing -os option";
		}
		return null;
	}
	
	
	public static class Mapper
	{
		private List<String> connections = null;
		private File fInput = null;
		private File fOutput = null;
		private File fReport = null;
		private DataSource is = null;
		private DataSource os = null;
		private int inputColumn = 0; 
		private int verbose = 0; // 0, 1 or 2
		private int multiMap = 0; // 0 or 1

		PrintStream report = System.out;
		private IDMapperStack gdb;
		
		private List<Xref> missing = new ArrayList<Xref>();
		private List<Xref> ambiguous = new ArrayList<Xref>();
		int totalLines = 0;
		int okLines = 0;

		public Mapper(List<String> connections, File fInput, File fOutput, File fReport, DataSource is, DataSource os, int inputColumn, int verbose, int multiMap)
		{
			this.connections = connections;
			this.fInput = fInput;
			this.fOutput = fOutput;
			this.fReport = fReport;
			this.is = is;
			this.os = os;
			this.inputColumn = inputColumn;
			this.verbose = verbose;
			this.multiMap = multiMap;
		}
		
		private void connectGdb() throws IDMapperException
		{
			gdb = new IDMapperStack();
			for (String connectionString : connections)
			{
				gdb.addIDMapper(connectionString);
			}
			gdb.setTransitive(true);
		}
		
		public void writeMapping() throws IOException, IDMapperException
		{
			LineNumberReader reader;
			PrintWriter writer;
			if (fInput != null)
			{
				reader = new LineNumberReader(new FileReader (fInput));
			}
			else
			{
				reader = new LineNumberReader(new InputStreamReader(System.in));
			}
			String line;
			if (fOutput != null)
			{
				writer = new PrintWriter (new FileWriter (fOutput));
			}
			else
			{
				writer = new PrintWriter (System.out);
			}
			while ((line = reader.readLine()) != null)
			{
				String[] fields = line.split("\t");
				if (fields.length > inputColumn && fields[inputColumn] != null)
				{
					Xref srcRef = new Xref(fields[inputColumn], is);
					Set<Xref> srcSet = new HashSet<Xref>();
					srcSet.add(srcRef);
					Map<Xref, Set<Xref>> mapresult = gdb.mapID(srcSet, os);
					Set<Xref> destRefs = mapresult.get (srcRef);
					if (destRefs == null || destRefs.size() == 0)
					{
						missing.add (srcRef);
					}
					else if (destRefs.size() >= 2)
					{
						ambiguous.add (srcRef);
					}
					
					if (destRefs != null && destRefs.size() > 0)
					{
						okLines++;
						if (multiMap == 0)
						{
							// use first one
							writer.print(destRefs.toArray(new Xref[0])[0].getId());
						}
						else
						{
							// concatenate all, with " /// " as separator
							boolean first = true;
							for (Xref ref : destRefs)
							{
								if (first)
								{
									first = false;
								}
								else
								{
									writer.print (" /// ");
								}
								writer.print(ref.getId());
							}
						}
						
					}
					totalLines++;
				}
				writer.println("\t" + line);
			}
			reader.close();
			writer.close();
		}
		
		public void reportMapping()
		{
			report.println ("Missing   : " + missing.size());
			report.println ("Ambiguous : " + ambiguous.size());
			report.println ("Ok        : " + okLines);
			report.println ("           _______ +");
			report.println ("Total     : " + totalLines);
			report.println();
			if (verbose >= 1)
			{
				// missing id's
				report.println ("Missing id's:");
				for (int i = 0; i < missing.size(); ++i)
				{
					report.print (missing.get(i));
					if (i < missing.size()-1) report.print (", ");
					if (i % 5 == 4) report.println();
				}
				report.println();

				// ambiguous id's
				report.println ("Ambiguous id's:");
				for (int i = 0; i < ambiguous.size(); ++i)
				{
					report.print (ambiguous.get(i));
					if (i < ambiguous.size()-1) report.print (", ");
					if (i % 5 == 4) report.println();
				}
				report.println();
			}
		}

		public void run()
		{
			try
			{
				if (fReport != null)
				{
					report = new PrintStream(new FileOutputStream(fReport));
				}
				connectGdb();
				writeMapping();
				reportMapping();
				if (fReport != null)
				{
					report.close();
				}
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
			catch (IDMapperException ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	public void reportSystemCodes()
	{
		List<DataSource> sortedList = new ArrayList<DataSource>();
		sortedList.addAll (DataSource.getDataSources());
		Collections.sort (sortedList, new Comparator<DataSource>() {

			public int compare(DataSource a, DataSource b) 
			{
				return a.getSystemCode().compareTo(b.getSystemCode());
			}} ); 
		
		for (DataSource ds : sortedList)
		{
			System.out.printf("%4s %-20s %-40s\n", ds.getSystemCode(), ds.getFullName(), ds.getExample().getId()); 
		}
	}
	
	public void run(String[] args)
	{
		DataSourceTxt.init();
		Settings settings = new Settings();
		String error = parseArgs(settings, args);
		if (error != null)
		{
			System.err.println ("Error: " + error);
			printUsage();
			System.exit(1);
		}
		try
		{
			Class.forName("org.bridgedb.file.IDMapperText");
			Class.forName("org.bridgedb.rdb.IDMapperRdb");
		}
		catch (ClassNotFoundException ex)
		{
			ex.printStackTrace();
			//TODO: better exception handling
		}
		if (settings.mode == 0)
		{			
			Mapper mapper = new Mapper(
					settings.connectStrings, 
					settings.fInput, settings.fOutput, settings.fReport, 
					settings.is, settings.os, settings.inputColumn, 
					settings.verbose, settings.multiMap);
			mapper.run();
		}
		else
		{
			reportSystemCodes();
		}
	}		
}
