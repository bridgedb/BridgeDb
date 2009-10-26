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

package org.bridgedb.webservice.synergizer;

import junit.framework.TestCase;

import java.io.IOException;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

public class Test extends TestCase 
{
	boolean eventReceived = false;
	
	public void setUp() throws ClassNotFoundException
	{
		Class.forName ("org.bridgedb.webservice.synergizer.IDMapperSynergizer");
	}

        public void testSynergizerStub() throws IOException, IDMapperException {
            SynergizerStub client = SynergizerStub.getInstance();
            System.out.println(client.availableAuthorities());
            System.out.println(client.availableSpecies("ensembl"));
            System.out.println(client.availableDomains("ensembl", "Homo sapiens"));
            System.out.println(client.availableRanges("ensembl", "Homo sapiens", "hgnc_symbol"));
        }

        public void testIDMapperSynergizer() throws IOException, IDMapperException {
//            SynergizerStub client = SynergizerStub.getInstance();
//            for (String auth : client.availableAuthorities()) {
//                System.out.println("Authority: "+auth);
//                for (String species : client.availableSpecies(auth)) {
//                    System.out.println("  Species: "+species);
//                    IDMapperSynergizer mapper = new IDMapperSynergizer(auth, species);
//                    System.out.println("    Supported source data sources:");
//                    for (DataSource ds : mapper.getCapabilities().getSupportedSrcDataSources()) {
//                        System.out.println("      "+ds.getFullName());
//                    }
//                    System.out.println("    Supported target data sources:");
//                    for (DataSource ds : mapper.getCapabilities().getSupportedTgtDataSources()) {
//                        System.out.println("      "+ds.getFullName());
//                    }
//                }
//            }

            IDMapper mapper = BridgeDb.connect("idmapper-synergizer:authority=ensembl&species=Homo sapiens");

            DataSource srcDs = DataSource.getByFullName("hgnc_symbol");
            assertTrue(mapper.xrefExists(new Xref("pja1", srcDs)));

            Set<Xref> srcXrefs = new HashSet();
            srcXrefs.add(new Xref("snph", srcDs));
            srcXrefs.add(new Xref("chac1", srcDs));
            srcXrefs.add(new Xref("actn3", srcDs));
            srcXrefs.add(new Xref("maybe_a_typo", srcDs));
            srcXrefs.add(new Xref("almost certainly a typo", srcDs));
            srcXrefs.add(new Xref("pja1", srcDs));
            srcXrefs.add(new Xref("prkdc", srcDs));
            srcXrefs.add(new Xref("RAD21L1", srcDs));
            srcXrefs.add(new Xref("Rorc", srcDs));
            srcXrefs.add(new Xref("kcnk16", srcDs));

            Map<Xref,Set<Xref>> res = mapper.mapID(srcXrefs, DataSource.getByFullName("entrezgene"));
            for (Map.Entry<Xref,Set<Xref>> entry : res.entrySet()) {
                System.out.println(entry.getKey().getId());
                for (Xref tgt : entry.getValue()) {
                    System.out.println("    "+tgt.getId());
                }
            }
        }

	
	
}
