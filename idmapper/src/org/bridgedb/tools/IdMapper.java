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
package org.bridgedb.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.bridgedb.DataDerby;
import org.bridgedb.DataException;
import org.bridgedb.Gdb;
import org.bridgedb.DataSource;
import org.bridgedb.SimpleGdbFactory;
import org.bridgedb.Xref;

public class IdMapper 
{
	private static class Settings
	{
		File fGdb = null;
		File fInput = null;
		File fOutput = null;
		File fReport = null;
		DataSource is = null;
		DataSource os = null;
		int inputColumn = 0; 
		int verbose = 0; // 0, 1 or 2
	}
	
	public static void main(String[] args)
	{
		IdMapper mapper = new IdMapper();
		mapper.run(args);
	}
	
	public void printUsage()
	{
		System.out.print (
				"Usage:\n"+
				"	mapper \n" +
				"		[-v|-vv] \n" +
				"		-g <gene database> \n " +
				"		-i <input file> \n" +
				"		-is <input system code> \n" +
				"		-os <output system code> \n" +
				"		-c <input column, 0-based> \n" +
				"		-o <output file> \n" +
				"		[-r <report file>] \n");
	}
	
	public String parseArgs(Settings settings, String[] args)
	{
		int pos = 0;
		while (pos < args.length)
		{
			if (args[pos].equals ("-v"))
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
				if (pos > args.length) return "File expected after -f";
				settings.fGdb = new File (args[pos]);
				if (!settings.fGdb.exists()) return "File " + args[pos] + " does not exist";
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
				settings.is = DataSource.getBySystemCode(args[pos]);
			}			
			else if (args[pos].equals("-os"))
			{
				pos++;
				if (pos > args.length) return "System code expected after -os";
				settings.os = DataSource.getBySystemCode(args[pos]);
			}			
			else
			{
				return "Unrecognized option " + args[pos];
			}
			pos++;
		}
		if (settings.fGdb == null) return "Missing -g option";
		if (settings.fOutput == null) return "Missing -o option";
		if (settings.fInput == null) return "Misisng -i option";
		if (settings.is == null) return "Missing -is option";
		if (settings.os == null) return "Missing -os option";
		return null;
	}
	
	
	public static class Mapper
	{
		private File fGdb = null;
		private File fInput = null;
		private File fOutput = null;
		private File fReport = null;
		private DataSource is = null;
		private DataSource os = null;
		private int inputColumn = 0; 
		private int verbose = 0; // 0, 1 or 2

		PrintStream report = System.out;
		private Gdb gdb;
		
		private List<Xref> missing = new ArrayList<Xref>();
		private List<Xref> ambiguous = new ArrayList<Xref>();
		int totalLines = 0;
		int okLines = 0;

		public Mapper(File fGdb, File fInput, File fOutput, File fReport, DataSource is, DataSource os, int inputColumn, int verbose)
		{
			this.fGdb = fGdb;
			this.fInput = fInput;
			this.fOutput = fOutput;
			this.fReport = fReport;
			this.is = is;
			this.os = os;
			this.inputColumn = inputColumn;
			this.verbose = verbose;
		}
		
		private void connectGdb() throws DataException
		{
			gdb = SimpleGdbFactory.createInstance("" + fGdb, new DataDerby(), 0);
		}
		
		public void writeMapping() throws IOException, DataException
		{
			LineNumberReader reader = new LineNumberReader(new FileReader (fInput));
			String line;
			PrintWriter writer = new PrintWriter (new FileWriter (fOutput));
			while ((line = reader.readLine()) != null)
			{
				String[] fields = line.split("\t");
				if (fields.length > inputColumn && fields[inputColumn] != null)
				{
					Xref srcRef = new Xref(fields[inputColumn], is); 
					List<Xref> destRefs = gdb.getCrossRefs(srcRef, os);
					if (destRefs.size() == 0)
					{
						missing.add (srcRef);
					}
					else if (destRefs.size() >= 2)
					{
						ambiguous.add (srcRef);
					}
					
					if (destRefs.size() > 0)
					{
						okLines++;
						// use first one
						writer.print(destRefs.get(0).getId());
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
			catch (DataException ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	public void run(String[] args)
	{
		Settings settings = new Settings();
		String error = parseArgs(settings, args);
		if (error != null)
		{
			System.err.println ("Error: " + error);
			printUsage();
			System.exit(1);
		}
		Mapper mapper = new Mapper(settings.fGdb, 
				settings.fInput, settings.fOutput, settings.fReport, 
				settings.is, settings.os, settings.inputColumn, settings.verbose);
		mapper.run();
	}		
}
