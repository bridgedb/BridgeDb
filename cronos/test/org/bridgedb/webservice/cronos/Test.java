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
package org.bridgedb.webservice.cronos;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.bridgedb.BridgeDb;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.bio.BioDataSource;

import junit.framework.TestCase;

public class Test extends TestCase 
{
	@Override public void setUp() throws ClassNotFoundException
	{
		Class.forName ("org.bridgedb.webservice.cronos.IDMapperCronos");
	}
	
	public void testSimple() throws IDMapperException
	{
		IDMapper mapper = BridgeDb.connect ("idmapper-cronos:hsa");
		BioDataSource.init();
		Xref insr = new Xref ("3643", BioDataSource.ENTREZ_GENE);
		Set<Xref> result = mapper.mapID(insr, BioDataSource.ENSEMBL);
		for (Xref ref : result) System.out.println (ref);
	}
	
}
