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
package org.bridgedb;

//import buildsystem.Measure;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bridgedb.file.IDMapperFile;
import org.bridgedb.file.IDMapperText;
import org.junit.Before;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test identifier mapping using a tab-delimited text file.
 */
public class TestFile {
	//private Measure measure;
	
	private static final File YEAST_IDS = new File ("test-data/yeast_id_mapping.txt");
	private static final DataSource ENS_YEAST = DataSource.getByFullName("Ensembl Yeast");
    private static final DataSource ENTREZ = DataSource.getByFullName("Entrez Gene");
    private static final DataSource EMBL = DataSource.getByFullName("EMBL");
    private static final Xref XREF1 = new Xref("YHR055C", ENS_YEAST);
	
	@Before public void setUp()
	{
		//measure = new Measure("bridgedb_timing.txt");
	}
	
	@Test public void testFiles()
	{
		Assert.assertTrue (YEAST_IDS.exists());
	}
	
	@Test public void testRead() throws IDMapperException, IOException
	{
		IDMapperFile idMapper = new IDMapperText (YEAST_IDS.toURL());

        Set<Xref> srcXrefs = new HashSet<Xref>();
        srcXrefs.add(XREF1);

        DataSource[] tgtDataSources = new DataSource[] { ENS_YEAST, ENTREZ, EMBL };

		long start = System.currentTimeMillis();
        // mapID for the first time will trigger reading
		Map<Xref, Set<Xref>> mapXrefs = idMapper.mapID(srcXrefs, tgtDataSources);
		long end = System.currentTimeMillis();
		long delta = end - start;
		System.out.println (delta);
		//measure.add ("timing::text file non-transitive", "" + delta, "msec");

		Set<Xref> expected = new HashSet<Xref>();
        expected.addAll (Arrays.asList(
        		new Xref("YHR055C", ENS_YEAST),
        		new Xref("U00061", EMBL),
        		new Xref("K02204", EMBL),
        		new Xref("AY558517", EMBL),
        		new Xref("AY693077", EMBL),
        		new Xref("856452", ENTREZ),
        		new Xref("856450", ENTREZ)
        		));
        Set<Xref> xrefs = mapXrefs.get(XREF1);
        Assert.assertEquals (expected, xrefs);

        for (Xref xr : xrefs) {
            System.out.println(xr.getDataSource().getFullName() + ": " + xr.getId());
        }
        
        Xref nonsense = new Xref ("Humbug", DataSource.getByFullName("Ebenizer Scrooge"));
        // non-existent id should just return empty list.
        Assert.assertEquals (0, idMapper.mapID(nonsense).size());

	}

	public void _testTransitive() throws MalformedURLException, IDMapperException
	{
		IDMapperFile idMapper = new IDMapperText (YEAST_IDS.toURL(),
				new char[] { '\t' },
				new char[] { ',' },
				true);

        Set<Xref> srcXrefs = new HashSet<Xref>();
        srcXrefs.add(XREF1);

        DataSource[] tgtDataSources = new DataSource[] { ENS_YEAST, ENTREZ, EMBL };

		long start = System.currentTimeMillis();
        // mapID for the first time will trigger reading
		Map<Xref, Set<Xref>> mapXrefs = idMapper.mapID(srcXrefs, tgtDataSources);
		long end = System.currentTimeMillis();
		long delta = end - start;
		System.out.println (delta);
		//measure.add ("timing::text file transitive", "" + delta, "msec");

		System.out.println (mapXrefs);
        Set<Xref> xrefs = mapXrefs.get(XREF1);

        for (Xref xr : xrefs) {
            System.out.println(xr.getDataSource().getFullName() + ": " + xr.getId());
        }
	}
	
}
