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
package org.bridgedb.util.hmdb;

import java.io.IOException;
import java.io.LineNumberReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parse Metabocards from Hmdb
 */
public class ParseHmdb
{
	/**
	 * represents some information from
	 * a single HMDB metabocard
	 */
	public static class Compound
	{
		/** store a key, value field */
		void storeField (String key, String value)
		{
			// check for "Not Available" lines
			if (value.equals ("Not Available"))
			{
				value = null;
			}
			if (key.equals("name"))
			{
				symbol = value;
			}
			else if (key.equals("chemical_formula"))
			{
				formula = value;
			}
			else if (key.equals("kegg_compound_id"))
			{
				idKegg = value == null ? null : value.split("; ");
			}
			else if (key.equals("biocyc_id"))
			{
				idBioc = value == null ? null : value.split("; ");
			}
			else if (key.equals("pubchem_compound_id"))
			{
				idPubchem = value == null ? null : value.split("; ");
			}
			else if (key.equals("chebi_id"))
			{
				idChebi = value == null ? null : value.split("; ");
			}
			else if (key.equals("cas_number"))
			{
				idCas = value == null ? null : value.split("; ");
			}
			else if (key.equals("synonyms"))
			{
				synonyms = value == null ? null : value.split("; ");
			}
			else if (key.equals("wikipedia_link"))
			{
				idWikipedia = value == null ? null : value.split("; ");
			}
			else if (key.equals("smiles_canonical"))
			{
				smiles = value;
			}
			else if (key.equals("inchi_identifier"))	
			{
				inchi = value;
			}
		}

		public String idHmdb = null;
		public String symbol = null;
		public String formula = null;
		public String[] idKegg = null;
		public String[] idPubchem = null;
		public String[] idChebi = null;
		public String[] idCas = null;
		public String[] idBioc = null;
		public String[] idWikipedia = null;
		public String smiles = null;
		public String[] synonyms = null;
		public String inchi = null;
	}

	/**
	 * Signals error while parsing a Metabocards-formatted file.
	 * This exception means that either the file is corrupt,
	 * not a valid metabocards file, or (possibly)
	 * that the metabocards format has changed.
	 */
	public static class ParseException extends Exception
	{
		ParseException (String message)
		{
			super(message);
		}

		ParseException (String message, int lineNo, String line)
		{
			super("Parse error: " + message + " at " + lineNo + "\n" + line);
		}
	}

	/**
	 * Reads a single metabocard from a text file.
	 * returns null if there are no more records to read.
	 */
	public Compound readNext (LineNumberReader reader) throws IOException, ParseException
	{
		Compound result = new Compound();;

		int state = 0;
		String line;
		String key = null;
		String value = null;

		Pattern p1 = Pattern.compile ("#BEGIN_METABOCARD (HMDB\\d+)");
		Pattern p2 = Pattern.compile ("#END_METABOCARD (HMDB\\d+)");
		Pattern p3 = Pattern.compile ("# ([a-zA-Z0-9_]+):");

		while ((line = reader.readLine()) != null)
		{
			int newState = state;

			switch (state)
			{
			// state 0: expect begin
			case 0:
				Matcher m1 = p1.matcher(line);
				if (m1.matches())
				{
					newState = 1;
					result.idHmdb = m1.group(1);
				}
				else if (line.equals (""))
				{
					// ignore
				}
				else
				{
					throw new ParseException ("begin expected", reader.getLineNumber(), line);
				}
				break;
			// state 1: expect key, end or empty
			case 1:
				Matcher m2 = p2.matcher (line);
				Matcher m3 = p3.matcher (line);
				if (m2.matches())
				{
					// end
					return result;
				}
				else if (m3.matches())
				{
					// store unless this is the first key
					if (key != null) result.storeField (key, value);

					key = m3.group(1);
					value = null;
					newState = 1;
				}
				else if (line.equals (""))
				{
					// ignore
				}
				else
				{
					if (value == null)
					{
						value = line;
					}
					else
					{
						value += "\n" + line;
					}
				}
				break;
			}
			state = newState;
		}
		if (state != 0)
		{
			// The record was not properly terminated.
			throw new ParseException ("Parse error: unexpected end of file");
		}
		else
		{
			// end of file reached, no more records
			return null;
		}
	}

}
