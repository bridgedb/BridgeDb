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
import org.pathvisio.core.debug.Logger;
import org.pathvisio.core.debug.StopWatch;
import org.pathvisio.core.preferences.PreferenceManager;

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
		Logger.log.setStream (System.out);
		String dbname = args[0];
		String file = args[1];

		Hmdb2Gdb h2g = new Hmdb2Gdb();
		PreferenceManager.init();

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
			Logger.log.error ("IDMapperException ", e);
		}
		catch (Exception e)
		{
			Logger.log.error ("Exception ", e);
		}
	}

	GdbConstruct simpleGdb;
	String dbName;

	StopWatch timer;

	private void init(String dbname, GdbConstruct simpleGdb) throws IDMapperException, ClassNotFoundException
	{
		timer = new StopWatch();

		this.simpleGdb = simpleGdb;
		this.dbName = dbname;

    	Logger.log.info("Timer started");
    	timer.start();
//		simpleGdb.connect (true);

		simpleGdb.createGdbTables();
		simpleGdb.preInsert();
    }

	private void done() throws IDMapperException
	{
		simpleGdb.commit();

    	Logger.log.info("Timer stopped: " + timer.stop());

    	//TODO
//    	Logger.log.info("total ids in gene table: " + simpleGdb.getGeneCount());
    	Logger.log.info("total errors (duplicates): " + error);

    	Logger.log.info("END processing text file");

    	Logger.log.info("Compacting database");

		Logger.log.info("Closing connections");


    	simpleGdb.finalize();
	}

	int error = 0;
	int progress = 0;

	private void addCompound (Compound c) throws IDMapperException
	{
		Xref ref = new Xref (c.idHmdb, BioDataSource.HMDB);
		error += simpleGdb.addGene(ref);
		error += simpleGdb.addLink(ref, ref);
		error += simpleGdb.addAttribute(ref, "Symbol", c.symbol);
		error += simpleGdb.addAttribute(ref, "BrutoFormula", c.formula);

		if (c.symbol != null)
		{
			// hmdb id is actually also the NUGOWIKI id.
			Xref right = new Xref (c.idHmdb, BioDataSource.NUGOWIKI);
			error += simpleGdb.addGene (right);
			error += simpleGdb.addLink (ref, right);
		}

		if (c.idKegg != null) for (String id : c.idKegg)
		{
			Xref right = new Xref (id, BioDataSource.KEGG_COMPOUND);
			error += simpleGdb.addGene(right);
			error += simpleGdb.addLink(ref, right);
		}

		if (c.idChebi != null) for (String id : c.idChebi)
		{
			Xref right = new Xref (id, BioDataSource.CHEBI);
			error += simpleGdb.addGene(right);
			error += simpleGdb.addLink(ref, right);
		}

		if (c.idPubchem != null) for (String id : c.idPubchem)
		{
			Xref right = new Xref (id, BioDataSource.PUBCHEM);
			error += simpleGdb.addGene(right);
			error += simpleGdb.addLink(ref, right);
		}

		if (c.idCas != null) for (String id : c.idCas)
		{
			Xref right = new Xref (id, BioDataSource.CAS);
			error += simpleGdb.addGene(right);
			error += simpleGdb.addLink(ref, right);
		}

		if (c.idWikipedia != null) for (String id : c.idWikipedia)
		{
			Xref right = new Xref (id, BioDataSource.WIKIPEDIA);
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
		StopWatch sw = new StopWatch();
		sw.start();
		LineNumberReader br = new LineNumberReader (new InputStreamReader(is));
		Compound c;
		try
		{
			while ((c = parser.readNext(br)) != null)
			{
				progress++;
				addCompound (c);
				if(progress % PROGRESS_INTERVAL == 0) {
					Logger.log.info("Processed " + progress + " record");
					simpleGdb.commit();
				}

				Logger.log.info (c.symbol + " added");
			}
			Logger.log.info ("Total: " + progress);
		}
		catch (ParseException pe)
		{
			System.err.println (pe.getMessage());
			System.err.println ("Please check that this is a valid metabocards file");
			pe.printStackTrace();
		}
		Logger.log.info ("Finished in " + sw.stop() + "ms");

	}

	private final static long PROGRESS_INTERVAL = 100;
}
