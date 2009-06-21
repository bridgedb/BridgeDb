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

import org.bridgedb.file.IDMapperFile;
import org.bridgedb.file.IDMapperText;

import junit.framework.TestCase;

public class TestFile extends TestCase 
{
	Measure measure;
	public void setUp()
	{
		measure = new Measure("bridgedb_timing.txt");
	}
	
	public void testRead() throws IDMapperException, IOException
	{
		File f = new File ("../test-data/yeast_id_mapping.txt");
		assertTrue (f.exists());
		
		
		IDMapperFile idMapper = new IDMapperText (f.toURL(), 
				new String[] { "\t"} , 
				new String[] { ","} );
		
		long start = System.currentTimeMillis();
		idMapper.read();
		long end = System.currentTimeMillis();
		long delta = end - start;
		System.out.println (delta);
		measure.add ("timing::read yeast id's", "" + delta, "msec");
		
	}
}
