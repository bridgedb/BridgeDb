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

import buildsystem.Measure;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.bridgedb.webservice.IDMapperBiomart;
import org.bridgedb.webservice.biomart.*;

/**
 * Test identifier mapping using Biomart web service.
 */
public class TestBiomart extends TestCase
{
    public void testBiomartStub() throws IOException, IDMapperException {
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
        }

        for (Database db : dbs) {
            Set<Dataset> datasets = new HashSet(biomartStub.getAvailableDatasets(db.getName()));
            int nds = datasets.size();
            for (Dataset ds : datasets) {
                IDMapperBiomart idMapper = new IDMapperBiomart(ds.getName());
                IDMapperCapabilities cap = idMapper.getCapabilities();
                if (cap.getSupportedSrcDataSources().isEmpty()
                        || cap.getSupportedTgtDataSources().isEmpty()) {
                    System.out.println("ds\t"+ds.getName());
                    nds--;
                }
            }
            if (nds==0) {
                System.out.println("db\t"+db.getName());
            }
        }
    }
	
}
