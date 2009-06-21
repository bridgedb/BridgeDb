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

import java.io.File;
import java.util.Set;

import org.bridgedb.rdb.DataDerby;
import org.bridgedb.rdb.SimpleGdb;
import org.bridgedb.rdb.SimpleGdbFactory;

import junit.framework.TestCase;

public class Test extends TestCase 
{
	//TODO
	static final String GDB_HUMAN = 
		System.getProperty ("user.home") + File.separator + 
		"PathVisio-Data/gene databases/Hs_Derby_20081119.pgdb";
	static final String GDB_RAT = 
		System.getProperty ("user.home") + File.separator + 
		"PathVisio-Data/gene databases/Rn_Derby_20081119.pgdb";

	boolean eventReceived = false;
	
	public void testGdbConnect() throws IDMapperException
	{
		assertTrue (new File (GDB_HUMAN).exists()); // if gdb can't be found, rest of test doesn't make sense. 
		SimpleGdb gdb = SimpleGdbFactory.createInstance (GDB_HUMAN, new DataDerby(), 0);
		
		gdb.close();
	}
	
	public void testRegisterDataSource()
	{
		DataSource.register("@@", "ZiZaZo");
		
		DataSource ds2 = DataSource.getBySystemCode ("@@");
		DataSource ds3 = DataSource.getByFullName ("ZiZaZo");
		assertEquals (ds2, ds3);
		
		// assert that you can refer to 
		// undeclared systemcodes if necessary.
		assertNotNull (DataSource.getBySystemCode ("##"));		
	}
}
