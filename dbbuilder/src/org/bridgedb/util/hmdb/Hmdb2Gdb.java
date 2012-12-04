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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipFile;

import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.bio.BioDataSource;
import org.bridgedb.rdb.construct.DBConnector;
import org.bridgedb.rdb.construct.DataDerby;
import org.bridgedb.rdb.construct.GdbConstruct;
import org.bridgedb.rdb.construct.GdbConstructImpl3;
import org.bridgedb.util.hmdb.ParseHmdb.Compound;
import org.bridgedb.util.hmdb.ParseHmdb.ParseException;

/**
 * Program to create a metabolite database based on a
 * metabocards flat text file, which can be downloaded from http://www.hmdb.ca
 *
 * In fall '08 HMDB changed the metabocard file format,
 * This program is requires the newer format.
 */
public class Hmdb2Gdb
{
	/**
	 * @param args command line arguments
	 *
	 * Commandline:
	 * - output database: .pgdb
	 * - input metabocards .txt file
	 */
	public static void main(String[] args)
	{
		String dbname = args[0];
		String file = args[1];

		Hmdb2Gdb h2g = new Hmdb2Gdb();

    	try
    	{
			GdbConstruct simpleGdb = GdbConstructImpl3.createInstance(dbname, new DataDerby(), DBConnector.PROP_RECREATE);

    		h2g.init (dbname, simpleGdb);
    		
    		InputStream is;
    		if (file.toLowerCase().endsWith(".zip"))
    		{
    			ZipFile zip = new ZipFile(file);
    			is = zip.getInputStream(zip.entries().nextElement());
//    			is = new ZipInputStream(new FileInputStream(new File(file)));
    		}
    		else
    		{
    			is = new FileInputStream (new File (file));
    		}
    		
			h2g.run(is);
    		h2g.done();
    	}
		catch (IDMapperException e)
		{
			 e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	GdbConstruct simpleGdb;
	String dbName;

	private void init(String dbname, GdbConstruct simpleGdb) throws IDMapperException, ClassNotFoundException
	{
		this.simpleGdb = simpleGdb;
		this.dbName = dbname;

//		simpleGdb.connect (true);

		simpleGdb.createGdbTables();
		simpleGdb.preInsert();
		
		String dateStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
		simpleGdb.setInfo("BUILDDATE", dateStr);
		simpleGdb.setInfo("DATASOURCENAME", "HMDB");
		simpleGdb.setInfo("DATASOURCEVERSION", "metabocards_" + dateStr);
		simpleGdb.setInfo("DATATYPE", "Metabolite");
		simpleGdb.setInfo("SERIES", "standard_metabolite");
    }

	private void done() throws IDMapperException
	{
		simpleGdb.commit();

    	//TODO
//    	System.out.println("total ids in gene table: " + simpleGdb.getGeneCount());
    	System.out.println("total errors (duplicates): " + error);

    	System.out.println("END processing text file");

    	System.out.println("Compacting database");

		System.out.println("Closing connections");


    	simpleGdb.finalize();
	}

	int error = 0;
	int progress = 0;

	private void addCompound (Compound c) throws IDMapperException
	{
		Xref ref = c.idHmdb;
		error += simpleGdb.addGene(ref);
		error += simpleGdb.addLink(ref, ref);
		error += simpleGdb.addAttribute(ref, "Symbol", c.symbol);
		error += simpleGdb.addAttribute(ref, "BrutoFormula", c.formula);

		if (c.symbol != null)
		{
			// hmdb id is actually also the NUGOWIKI id.
			Xref right = c.idHmdb;
			error += simpleGdb.addGene (right);
			error += simpleGdb.addLink (ref, right);
		}
		
		if (c.inchi != null)
		{
			error += simpleGdb.addAttribute(ref, "InChI", c.inchi);
		}
		
		for (Xref right : c.idKegg)
		{
			error += simpleGdb.addGene(right);
			error += simpleGdb.addLink(ref, right);
		}

		for (Xref right : c.idChebi)
		{
			error += simpleGdb.addGene(right);
			error += simpleGdb.addLink(ref, right);
		}

		for (Xref right : c.idPubchem)
		{
			error += simpleGdb.addGene(right);
			error += simpleGdb.addLink(ref, right);
		}

		for (Xref right : c.idCas)
		{
			error += simpleGdb.addGene(right);
			error += simpleGdb.addLink(ref, right);
		}

		for (Xref right : c.idWikipedia)
		{
			error += simpleGdb.addGene(right);
			error += simpleGdb.addLink(ref, right);
		}

		if (c.smiles != null)
		{
			error += simpleGdb.addAttribute(ref, "SMILES", c.smiles);
		}

		if (c.synonyms != null) for (String synonym : c.synonyms)
		{
			error += simpleGdb.addAttribute(ref, "Synonym", synonym);
		}
	}

	private void run(InputStream is) throws IOException, IDMapperException
	{
		ParseHmdb parser = new ParseHmdb();
		LineNumberReader br = new LineNumberReader (new InputStreamReader(is));
		Compound c;
		try
		{
			while ((c = parser.readNext(br)) != null)
			{
				progress++;
				addCompound (c);
				if(progress % PROGRESS_INTERVAL == 0) {
					System.out.println("Processed " + progress + " record");
					simpleGdb.commit();
				}

				System.out.println (c.symbol + " added");
			}
			System.out.println ("Total: " + progress);
		}
		catch (ParseException pe)
		{
			System.err.println (pe.getMessage());
			System.err.println ("Please check that this is a valid metabocards file");
			pe.printStackTrace();
		}

	}

	private final static long PROGRESS_INTERVAL = 100;
}
