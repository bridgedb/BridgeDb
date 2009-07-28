// BridgeDb,
// An abstraction layer for identifer mapping services, both local and online.
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

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.bridgedb.rdb.DataDerby;
import org.bridgedb.rdb.SimpleGdb;
import org.bridgedb.rdb.SimpleGdbFactory;

public class Test extends TestCase 
{
	//TODO
	static final String GDB_HUMAN = 
		System.getProperty ("user.home") + File.separator + 
		"PathVisio-Data/gene databases/Hs_Derby_20081119.pgdb";
	static final String GDB_RAT = 
		System.getProperty ("user.home") + File.separator + 
		"PathVisio-Data/gene databases/Rn_Derby_20081119.pgdb";
	
	public void testGdbConnect() throws IDMapperException
	{
		assertTrue (new File (GDB_HUMAN).exists()); // if gdb can't be found, rest of test doesn't make sense. 
		SimpleGdb gdb = SimpleGdbFactory.createInstance (GDB_HUMAN, new DataDerby(), 0);
		
		gdb.close();
	}
	
	/**
	 * From schema v2 to v3 there was a change in how the backpage was stored.
	 * In schema v2 there was a backpage column in the datanode table
	 * In schema v3 the backpage is split in several attributes.
	 * For backwards compatibility, SimpleGdbImpl2 fakes these new attributes. 
	 * This is tested here. 
	 */
	public void testGdbAttributes() throws IDMapperException
	{
		// test special attributes that are grabbed from backpage
		// since this is a Schema v2 database
		SimpleGdb gdb = SimpleGdbFactory.createInstance (GDB_HUMAN, new DataDerby(), 0);
		Xref ref = new Xref ("26873", DataSource.getBySystemCode("L"));
		assertTrue (gdb.getAttributes(ref, "Synonyms").contains ("5-Opase|DKFZP434H244|OPLA"));
		assertTrue (gdb.getAttributes(ref, "Description").contains ("5-oxoprolinase (EC 3.5.2.9) (5-oxo-L-prolinase) (5-OPase) (Pyroglutamase) [Source:UniProtKB/Swiss-Prot.Acc:O14841]"));
		assertTrue (gdb.getAttributes(ref, "Chromosome").contains ("8"));
		assertTrue (gdb.getAttributes(ref, "Symbol").contains ("OPLAH"));
		
		Set<String> allExpectedAttributes = new HashSet<String>();
		allExpectedAttributes.add ("26873");
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
