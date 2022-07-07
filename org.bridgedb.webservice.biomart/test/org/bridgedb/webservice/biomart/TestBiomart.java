/* BridgeDb,
 * An abstraction layer for identifier mapping services, both local and online.
 *
 * Copyright 2006-2009  BridgeDb developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bridgedb.webservice.biomart;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Set;

import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


/**
 * Test identifier mapping using Biomart web service.
 */
@Disabled("Service seems offline")
public class TestBiomart // do not need to extend TestCase
{

	@Tag("webservice")
	@Test
    public void _testBiomartStub() throws IOException, IDMapperException {
        BiomartStub biomartStub = BiomartStub.getInstance();

        Set<String> marts = biomartStub.availableMarts();

        for (String mart : marts) {
            System.out.println (mart);
            Set<String> datasets;
            try {
                datasets = biomartStub.availableDatasets(mart);
            } catch (IDMapperException e) {
                e.printStackTrace();
                continue;
            }

            int nds = datasets.size();
            for (String ds : datasets) {
            	System.out.println ("\t" + ds);
                IDMapperBiomart idMapper = new IDMapperBiomart(mart, ds);
                //IDMapper idMapper = BridgeDb.connect("idmapper-biomart:dataset="+ds.getName());
                IDMapperCapabilities cap = idMapper.getCapabilities();
                if (cap.getSupportedSrcDataSources().isEmpty()
                        || cap.getSupportedTgtDataSources().isEmpty()) {
//                    System.out.println("\tds\t"+ds.getName());
                    nds--;
                }
                for (DataSource dsx : cap.getSupportedSrcDataSources())
                {
               	 System.out.println ("\t\t" + dsx);
                }
                for (DataSource dsx : cap.getSupportedTgtDataSources())
                {
               	 System.out.println ("\t\t" + dsx);
                }
            }
//            if (nds==0) {
//                System.out.println("\tdb\t"+db.getName());
//            }
        }
    }

	@Tag("webservice")
    @Test
    public void testBioMartConnector() throws IOException, IDMapperException, ClassNotFoundException
    {
    /*
        BiomartStub biomartStub = BiomartStub.getInstance();
        Map<String, Database> reg = null;
        try {
            reg = biomartStub.getRegistry();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Set<Database> dbs = new HashSet(reg.size());
         for (Database db : reg.values()) {
             //if (db.visible()) {
                 dbs.add(db);
             //}
                System.out.println (db.getName());
         }
        //BiomartStub biomartStub = BiomartStub.getInstance();

        //Set<Dataset> datasets = new HashSet(biomartStub.getAvailableDatasets("ensembl"));
    */

    	IDMapperBiomart mapper = new IDMapperBiomart("ensembl", "hsapiens_gene_ensembl");
    	//Class.forName("org.bridgedb.webservice.biomart.IDMapperBiomart");
    	//IDMapper mapper = BridgeDb.connect ("idmapper-biomart:http://www.biomart.org/biomart/martservice?mart=ensembl&dataset=hsapiens_gene_ensembl");
 		Set<DataSource> dest = mapper.getCapabilities().getSupportedTgtDataSources(); 
		 
		assertTrue (dest.size() > 0);
		assertTrue (dest.contains (DataSource.getExistingByFullName("entrezgene")));

		Set<DataSource> src = mapper.getCapabilities().getSupportedSrcDataSources();
		assertTrue (src.size() > 0);
		assertTrue (dest.contains (DataSource.getExistingByFullName("entrezgene")));
    }

	@Tag("webservice")
	@Test
    public void testBioMartConnector2() throws IOException, IDMapperException, ClassNotFoundException
    {
		Class.forName("org.bridgedb.webservice.biomart.IDMapperBiomart");
		
		IDMapper mapper = BridgeDb.connect ("idmapper-biomart:http://www.biomart.org/biomart/martservice?mart=ensembl&dataset=hsapiens_gene_ensembl");
		Set<DataSource> dest = mapper.getCapabilities().getSupportedTgtDataSources(); 
		 
		assertTrue (dest.size() > 0);
		assertTrue (dest.contains (DataSource.getExistingByFullName("entrezgene")));

		Set<DataSource> src = mapper.getCapabilities().getSupportedSrcDataSources();
		assertTrue (src.size() > 0);
		assertTrue (dest.contains (DataSource.getExistingByFullName("entrezgene")));
	}

    //TODO: put in Utility class
    private String setRep(Set<Xref> refs)
    {
    	StringBuilder result = new StringBuilder("[");
    	
    	int remain = refs.size();
    	int count = 0;
    	
    	for (Xref ref : refs)
    	{
    		result.append (ref);
    		remain--;
    		if (remain > 0) result.append (", ");
    		count++;
    		if (count > 3 && remain > 2) break;
    	}
    	if (remain > 0) result.append ("... " + remain + " more ...");
    	
    	result.append ("]");
    	return result.toString();
    }
    
	@Tag("webservice")    
    @Test
    public void testBioMartMapping() throws IOException, IDMapperException, ClassNotFoundException
    {
       Class.forName("org.bridgedb.webservice.biomart.IDMapperBiomart");
        
       IDMapper mapper = BridgeDb.connect ("idmapper-biomart:http://www.biomart.org/biomart/martservice?mart=ensembl&dataset=hsapiens_gene_ensembl");
       
       Set<Xref> result = mapper.mapID(
    		   new Xref("ENSG00000171105", DataSource.getExistingByFullName("ensembl_gene_id")),
    		   DataSource.getExistingByFullName("entrezgene"));
       for (Xref ref : result)
       {
    	   System.out.println (ref);
       }
       
       assertTrue (
    		   result.contains (new Xref ("3643", DataSource.getExistingByFullName("entrezgene"))), "Expected entrezgene:3643. Got " + setRep (result));
   }
}
