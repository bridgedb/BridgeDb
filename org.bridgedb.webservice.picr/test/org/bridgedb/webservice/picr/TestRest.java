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
package org.bridgedb.webservice.picr;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

import junit.framework.TestCase;

public class TestRest extends TestCase 
{
	boolean eventReceived = false;
	
	public void setUp() throws ClassNotFoundException
	{
		Class.forName ("org.bridgedb.webservice.picr.IDMapperPicrRest");
	}

        public void testDataSources() throws IDMapperException {
            IDMapperPicrRest idMapper = new IDMapperPicrRest(true);
            System.out.println(idMapper.getCapabilities().getSupportedSrcDataSources().toString());
        }
	
	public void test() throws IDMapperException
	{
		IDMapper idmap = BridgeDb.connect ("idmapper-picr-rest:");
		
		Set<DataSource> dslist = idmap.getCapabilities().getSupportedTgtDataSources();

		final DataSource SGD = DataSource.getByFullName("SGD");
		final DataSource PDB  = DataSource.getByFullName("PDB");
		final DataSource ENSEMBL_YEAST = DataSource.getByFullName("ENSEMBL_S_CEREVISIAE");
		assertTrue (dslist.contains(SGD));
		assertTrue (dslist.contains(PDB));
		assertTrue (dslist.contains(ENSEMBL_YEAST));

		Xref src1 = new Xref ("YER095W", ENSEMBL_YEAST);
		for (DataSource ds : dslist) System.out.println (ds.getFullName());

		assertTrue(idmap.xrefExists(src1));

		Set<Xref> srcRefs = new HashSet<Xref>();
		srcRefs.add (src1);
		DataSource[] targets = new DataSource[] { SGD, PDB, ENSEMBL_YEAST };
		//TODO: disabled while mapID is not yet implemented.
//		Map<Xref, Set<Xref>> result = idmap.mapID(srcRefs, targets);

		/*
		This list is expected:
		RAD51 SGD
		YER095W SGD
		YER095W ENSEMBL_S_CEREVISIAE
		1SZP PDB
		S000000897 SGD
		*/
//		for (Xref ref : result.get(src1))
//                    System.out.println (ref.getId() + " "  + ref.getDataSource().getFullName());

	}
	
}
