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

import buildsystem.Measure;

import java.io.File;
import java.io.IOException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.bridgedb.file.IDMapperFile;
import org.bridgedb.file.IDMapperText;

/**
 * Test identifier mapping using a tab-delimited text file.
 */
public class TestFile extends TestCase 
{
	private Measure measure;
	
	@Override public void setUp()
	{
		measure = new Measure("bridgedb_timing.txt");
	}
	
	public void testRead() throws IDMapperException, IOException
	{
		File f = new File ("../test-data/yeast_id_mapping.txt");
		assertTrue (f.exists());
		
		
		IDMapperFile idMapper = new IDMapperText (f.toURL());

//        IDMapperFile idMapper = new IDMapperText (f.toURL(),
//                new char[] {'\t'},
//                null,
//                true);

		DataSource ensYeast = DataSource.getByFullName("Ensembl Yeast");
        Xref xref = new Xref("YHR055C", ensYeast);
        Set<Xref> srcXrefs = new HashSet<Xref>();
        srcXrefs.add(xref);

        Set<DataSource> tgtDataSources = new HashSet<DataSource>();
        tgtDataSources.add(ensYeast);
        DataSource entrez = DataSource.getByFullName("Entrez Gene");
        DataSource embl = DataSource.getByFullName("EMBL");
        tgtDataSources.add(entrez);
        tgtDataSources.add(embl);
		
		long start = System.currentTimeMillis();
        // mapID for the first time will trigger reading
		Map<Xref, Set<Xref>> mapXrefs = idMapper.mapID(srcXrefs, tgtDataSources);
		long end = System.currentTimeMillis();
		long delta = end - start;
		System.out.println (delta);
		measure.add ("timing::read yeast id's", "" + delta, "msec");
        
		Set<Xref> expected = new HashSet<Xref>();
        expected.addAll (Arrays.asList(
        		new Xref("YHR055C", ensYeast), 
        		new Xref("U00061", embl), 
        		new Xref("K02204", embl),
        		new Xref("AY558517", embl),
        		new Xref("AY693077", embl),
        		new Xref("856452", entrez),
        		new Xref("856450", entrez)
        		));
        Set<Xref> xrefs = mapXrefs.get(xref);
        assertEquals (expected, xrefs);
        
        if (xrefs!=null && !xrefs.isEmpty())
        for (Xref xr : xrefs) {
            System.out.println(xr.getDataSource().getFullName() + ": " + xr.getId());
        }
        
	}
}
