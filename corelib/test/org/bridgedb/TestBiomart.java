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

import org.bridgedb.webservice.biomart.util.BiomartClient;
import buildsystem.Measure;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.bridgedb.webservice.biomart.IDMapperBiomart;
import org.bridgedb.webservice.biomart.*;

/**
 * Test identifier mapping using Biomart web service.
 */
public class TestBiomart extends TestCase
{
	// disabled test, because it takes several minutes to run
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

    public void testBioMartConnector() throws IOException, IDMapperException
    {
//        BiomartStub biomartStub = BiomartStub.getInstance();
//        Map<String, Database> reg = null;
//        try {
//            reg = biomartStub.getRegistry();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        Set<Database> dbs = new HashSet(reg.size());
//         for (Database db : reg.values()) {
//             //if (db.visible()) {
//                 dbs.add(db);
//             //}
//                System.out.println (db.getName());
//         }
        //BiomartStub biomartStub = BiomartStub.getInstance();

        //Set<Dataset> datasets = new HashSet(biomartStub.getAvailableDatasets("ensembl"));

         IDMapperBiomart mapper = new IDMapperBiomart("ensembl", "hsapiens_gene_ensembl");
         System.out.println("\n===Supported source data sources===");
         for (DataSource ds : mapper.getCapabilities().getSupportedSrcDataSources())
         {
        	 System.out.println (ds);
         }

         System.out.println("\n===Supported target data sources===");
         for (DataSource ds : mapper.getCapabilities().getSupportedTgtDataSources())
         {
        	 System.out.println (ds);
         }
    }

    public void testBioMartConnector2() throws IOException, IDMapperException, ClassNotFoundException
    {
        
        Class.forName("org.bridgedb.webservice.biomart.IDMapperBiomart");
        
         //IDMapperBiomart mapper = new IDMapperBiomart("hsapiens_gene_ensembl");
        IDMapper mapper = BridgeDb.connect ("idmapper-biomart:http://www.biomart.org/biomart/martservice?mart=ensembl&dataset=hsapiens_gene_ensembl");
        System.out.println("\n===Supported source data sources===");
         for (DataSource ds : mapper.getCapabilities().getSupportedSrcDataSources())
         {
        	 System.out.println (ds);
         }

         System.out.println("\n===Supported target data sources===");
         for (DataSource ds : mapper.getCapabilities().getSupportedTgtDataSources())
         {
        	 System.out.println (ds);
         }
    }

}
