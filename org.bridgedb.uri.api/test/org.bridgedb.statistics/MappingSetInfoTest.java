/*
 *BridgeDb,
 *An abstraction layer for identifier mapping services, both local and online.
 *Copyright (c) 2019, Manas Awasthi
 *
 *Licensed under the Apache License, Version 2.0 (the "License");
 *you may not use this file except in compliance with the License.
 *You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *See the License for the specific language governing permissions and limitations under the License.
 */

package org.bridgedb.statistics;

import static org.junit.jupiter.api.Assertions.*;

class MappingSetInfoTest {

    @org.junit.jupiter.api.Test
     public void getStringId() {
        DataSetInfo sourceDataSetInfo = new DataSetInfo("En", "Ensembl");
        DataSetInfo targetDataSetInfo = new DataSetInfo("X", "Affy");
        MappingSetInfo mappingSetInfo = new MappingSetInfo(1, sourceDataSetInfo, "http://www.ebi.ac.uk/embl/", targetDataSetInfo, "centre","none", "Ensembl", 0, 2, 1, 1);
        assertEquals(Integer.toString(1), mappingSetInfo.getStringId());
        assertNotEquals(Integer.toString(0), mappingSetInfo.getStringId());
    }

    @org.junit.jupiter.api.Test
    public void getIntId() {
        DataSetInfo sourceDataSetInfo = new DataSetInfo("En", "Ensembl");
        DataSetInfo targetDataSetInfo = new DataSetInfo("X", "Affy");
        MappingSetInfo mappingSetInfo = new MappingSetInfo(1, sourceDataSetInfo, "http://www.ebi.ac.uk/embl/", targetDataSetInfo, "centre","none", "Ensembl", 0, 2, 1, 1);
        assertEquals(1, mappingSetInfo.getIntId());
        assertNotEquals(0, mappingSetInfo.getIntId());
    }

    @org.junit.jupiter.api.Test
    public void combineIds() {
        DataSetInfo sourceDataSetInfo = new DataSetInfo("En", "Ensembl");
        DataSetInfo targetDataSetInfo = new DataSetInfo("X", "Affy");
        MappingSetInfo mappingSetInfo = new MappingSetInfo(1, sourceDataSetInfo, "http://www.ebi.ac.uk/embl/", targetDataSetInfo, "centre","none", "Ensembl", 0, 2, 1, 1);
        MappingSetInfo mappingSetInfo1 = new MappingSetInfo(2, targetDataSetInfo, "http://www.ebi.ac.uk/embl/", sourceDataSetInfo, "centre","none", "Affy", 0, 2, 1, 1);
        mappingSetInfo.combineIds(mappingSetInfo1);
        assertEquals("1,2",mappingSetInfo.getStringId());
    }

    @org.junit.jupiter.api.Test
    public void getPredicate() {
        DataSetInfo sourceDataSetInfo = new DataSetInfo("En", "Ensembl");
        DataSetInfo targetDataSetInfo = new DataSetInfo("X", "Affy");
        MappingSetInfo mappingSetInfo = new MappingSetInfo(1, sourceDataSetInfo, "http://www.ebi.ac.uk/embl/", targetDataSetInfo, "centre","none", "Ensembl", 0, 2, 1, 1);
        assertEquals("http://www.ebi.ac.uk/embl/", mappingSetInfo.getPredicate());
    }

    @org.junit.jupiter.api.Test
    public void predicateLocalName() {
        DataSetInfo sourceDataSetInfo = new DataSetInfo("Sl", "SwissLipids");
        DataSetInfo targetDataSetInfo = new DataSetInfo("X", "Affy");
        MappingSetInfo mappingSetInfo = new MappingSetInfo(1, sourceDataSetInfo, "http://www.swisslipids.org/#/entity/$id/", targetDataSetInfo, "centre","none", "SwissLipids", 0, 2, 1, 1);
        assertEquals("/entity/$id/", mappingSetInfo.predicateLocalName());

    }

    @org.junit.jupiter.api.Test
    public void isSymmetric() {
        DataSetInfo sourceDataSetInfo = new DataSetInfo("Sl", "SwissLipids");
        DataSetInfo targetDataSetInfo = new DataSetInfo("X", "Affy");
        MappingSetInfo mappingSetInfo = new MappingSetInfo(1, sourceDataSetInfo, "http://www.swisslipids.org/#/entity/$id/", targetDataSetInfo, "centre", "none", "SwissLipids", 2, 2, 1, 1);
        assertTrue(mappingSetInfo.isSymmetric());
        MappingSetInfo mappingSetInfo1 = new MappingSetInfo(1, sourceDataSetInfo, "http://www.swisslipids.org/#/entity/$id/", targetDataSetInfo, "centre", "none", "SwissLipids", 0, 2, 1, 1);
        assertFalse(mappingSetInfo1.isSymmetric());
    }

    @org.junit.jupiter.api.Test
    public void hasOrIsSymmetric() {
        DataSetInfo sourceDataSetInfo = new DataSetInfo("Sl", "SwissLipids");
        DataSetInfo targetDataSetInfo = new DataSetInfo("X", "Affy");
        MappingSetInfo mappingSetInfo = new MappingSetInfo(1, sourceDataSetInfo, "http://www.swisslipids.org/#/entity/$id/", targetDataSetInfo, "centre", "none", "SwissLipids", 2, 2, 1, 1);
        assertTrue(mappingSetInfo.hasOrIsSymmetric());
        MappingSetInfo mappingSetInfo1 = new MappingSetInfo(1, sourceDataSetInfo, "http://www.swisslipids.org/#/entity/$id/", targetDataSetInfo, "centre", "none", "SwissLipids", 0, 2, 1, 1);
        assertFalse(mappingSetInfo1.hasOrIsSymmetric());
    }

    @org.junit.jupiter.api.Test
    public void getNumberOfLinks() {
        DataSetInfo sourceDataSetInfo = new DataSetInfo("Sl", "SwissLipids");
        DataSetInfo targetDataSetInfo = new DataSetInfo("X", "Affy");
        MappingSetInfo mappingSetInfo = new MappingSetInfo(1, sourceDataSetInfo, "http://www.swisslipids.org/#/entity/$id/", targetDataSetInfo, "centre", "none", "SwissLipids", 2, 2, 1, 1);
        assertEquals(2,mappingSetInfo.getNumberOfLinks());
    }

    @org.junit.jupiter.api.Test
    public void setNumberOfLinks() {
        DataSetInfo sourceDataSetInfo = new DataSetInfo("Sl", "SwissLipids");
        DataSetInfo targetDataSetInfo = new DataSetInfo("X", "Affy");
        MappingSetInfo mappingSetInfo = new MappingSetInfo(1, sourceDataSetInfo, "http://www.swisslipids.org/#/entity/$id/", targetDataSetInfo, "centre", "none", "SwissLipids", 2, 2, 1, 1);
        mappingSetInfo.setNumberOfLinks(3);
        assertEquals(3, mappingSetInfo.getNumberOfLinks());
    }

    @org.junit.jupiter.api.Test
    public void toString1() {
        DataSetInfo sourceDataSetInfo = new DataSetInfo("Sl", "SwissLipids");
        DataSetInfo targetDataSetInfo = new DataSetInfo("X", "Affy");
        MappingSetInfo mappingSetInfo = new MappingSetInfo(1, sourceDataSetInfo, "http://www.swisslipids.org/#/entity/$id/", targetDataSetInfo, "centre", "none", "SwissLipids", 2, 2, 1, 1);
        String test = 1
                + "\n\tsource:" + mappingSetInfo.getSource()
                + "\n\tpredicate:" + mappingSetInfo.getPredicate()
                + "\n\ttarget: " + mappingSetInfo.getTarget()
                + "\n\tsymetric: " + mappingSetInfo.getSymmetric()
                + "\n\tnumberOfLinks: " + mappingSetInfo.getNumberOfLinks()
                //+ "\n\tmappingName: " + this.mappingName
                //+ "\n\tmappingUri: " + this.mappingUri
                + "\n";
        assertEquals(test, mappingSetInfo.toString());
    }

    @org.junit.jupiter.api.Test
    public void getJustification() {
        DataSetInfo sourceDataSetInfo = new DataSetInfo("Sl", "SwissLipids");
        DataSetInfo targetDataSetInfo = new DataSetInfo("X", "Affy");
        MappingSetInfo mappingSetInfo = new MappingSetInfo(1, sourceDataSetInfo, "http://www.swisslipids.org/#/entity/$id/", targetDataSetInfo, "centre", "none", "SwissLipids", 2, 2, 1, 1);
        assertEquals("centre", mappingSetInfo.getJustification());
    }

    @org.junit.jupiter.api.Test
    public void justificationLocalName() {
        DataSetInfo sourceDataSetInfo = new DataSetInfo("Sl", "SwissLipids");
        DataSetInfo targetDataSetInfo = new DataSetInfo("X", "Affy");
        MappingSetInfo mappingSetInfo = new MappingSetInfo(1, sourceDataSetInfo, "http://www.swisslipids.org/#/entity/$id/", targetDataSetInfo, "http://www.swisslipids.org/#/entity/$id/", "none", "SwissLipids", 2, 2, 1, 1);
        assertEquals("/entity/$id/", mappingSetInfo.justificationLocalName());
    }

    @org.junit.jupiter.api.Test
    public void getSymmetric() {
        DataSetInfo sourceDataSetInfo = new DataSetInfo("Sl", "SwissLipids");
        DataSetInfo targetDataSetInfo = new DataSetInfo("X", "Affy");
        MappingSetInfo mappingSetInfo = new MappingSetInfo(1, sourceDataSetInfo, "http://www.swisslipids.org/#/entity/$id/", targetDataSetInfo, "http://www.swisslipids.org/#/entity/$id/", "none", "SwissLipids", 2, 2, 1, 1);
        assertEquals(2, mappingSetInfo.getSymmetric());
    }

    @org.junit.jupiter.api.Test
    public void getSource() {
        DataSetInfo sourceDataSetInfo = new DataSetInfo("Sl", "SwissLipids");
        DataSetInfo targetDataSetInfo = new DataSetInfo("X", "Affy");
        MappingSetInfo mappingSetInfo = new MappingSetInfo(1, sourceDataSetInfo, "http://www.swisslipids.org/#/entity/$id/", targetDataSetInfo, "http://www.swisslipids.org/#/entity/$id/", "none", "SwissLipids", 2, 2, 1, 1);
        assertEquals(sourceDataSetInfo, mappingSetInfo.getSource());
        assertNotEquals(targetDataSetInfo, mappingSetInfo.getSource());
    }

    @org.junit.jupiter.api.Test
    public void getTarget() {
        DataSetInfo sourceDataSetInfo = new DataSetInfo("Sl", "SwissLipids");
        DataSetInfo targetDataSetInfo = new DataSetInfo("X", "Affy");
        MappingSetInfo mappingSetInfo = new MappingSetInfo(1, sourceDataSetInfo, "http://www.swisslipids.org/#/entity/$id/", targetDataSetInfo, "http://www.swisslipids.org/#/entity/$id/", "none", "SwissLipids", 2, 2, 1, 1);
        assertEquals(targetDataSetInfo, mappingSetInfo.getTarget());
        assertNotEquals(sourceDataSetInfo, mappingSetInfo.getTarget());
    }

    @org.junit.jupiter.api.Test
    public void isTransitive() {
        DataSetInfo sourceDataSetInfo = new DataSetInfo("Sl", "SwissLipids");
        DataSetInfo targetDataSetInfo = new DataSetInfo("X", "Affy");
        MappingSetInfo mappingSetInfo = new MappingSetInfo(1, sourceDataSetInfo, "http://www.swisslipids.org/#/entity/$id/", targetDataSetInfo, "http://www.swisslipids.org/#/entity/$id/", "none", "SwissLipids", 2, 2, 1, 1);
        assertFalse(mappingSetInfo.isTransitive());
    }

    @org.junit.jupiter.api.Test
    public void getMappingResource() {
        DataSetInfo sourceDataSetInfo = new DataSetInfo("Sl", "SwissLipids");
        DataSetInfo targetDataSetInfo = new DataSetInfo("X", "Affy");
        MappingSetInfo mappingSetInfo = new MappingSetInfo(1, sourceDataSetInfo, "http://www.swisslipids.org/#/entity/$id/", targetDataSetInfo, "http://www.swisslipids.org/#/entity/$id/", "Affy", "SwissLipids", 2, 2, 1, 1);
        assertEquals("Affy", mappingSetInfo.getMappingResource());
        assertNotEquals("SwissLipids", mappingSetInfo.getMappingResource());
    }

    @org.junit.jupiter.api.Test
    public void resourceLocalName() {
        DataSetInfo sourceDataSetInfo = new DataSetInfo("Sl", "SwissLipids");
        DataSetInfo targetDataSetInfo = new DataSetInfo("X", "Affy");
        MappingSetInfo mappingSetInfo = new MappingSetInfo(1, sourceDataSetInfo, "http://www.swisslipids.org/#/entity/$id/", targetDataSetInfo, "http://www.swisslipids.org/#/entity/$id/", "https://www.affymetrix.com/LinkServlet?probeset=$id", "SwissLipids.", 2, 2, 1, 1);
        assertEquals("LinkServlet?probeset=$id", mappingSetInfo.resourceLocalName());
    }

    @org.junit.jupiter.api.Test
    public void getMappingSource() {
        DataSetInfo sourceDataSetInfo = new DataSetInfo("Sl", "SwissLipids");
        DataSetInfo targetDataSetInfo = new DataSetInfo("X", "Affy");
        MappingSetInfo mappingSetInfo = new MappingSetInfo(1, sourceDataSetInfo, "http://www.swisslipids.org/#/entity/$id/", targetDataSetInfo, "http://www.swisslipids.org/#/entity/$id/", "https://www.affymetrix.com/LinkServlet?probeset=$id", "SwissLipids", 2, 2, 1, 1);
        assertEquals("SwissLipids", mappingSetInfo.getMappingSource());
    }

    @org.junit.jupiter.api.Test
    public void sourceLocalName() {
        DataSetInfo sourceDataSetInfo = new DataSetInfo("Sl", "SwissLipids");
        DataSetInfo targetDataSetInfo = new DataSetInfo("X", "Affy");
        MappingSetInfo mappingSetInfo = new MappingSetInfo(1, sourceDataSetInfo, "http://www.swisslipids.org/#/entity/$id/", targetDataSetInfo, "http://www.swisslipids.org/#/entity/$id/", "https://www.affymetrix.com/LinkServlet?probeset=$id", "http://www.swisslipids.org/#/entity/$id/", 2, 2, 1, 1);
        assertEquals("/entity/$id/", mappingSetInfo.sourceLocalName());
    }

    @org.junit.jupiter.api.Test
    public void getNumberOfSources() {
        DataSetInfo sourceDataSetInfo = new DataSetInfo("Sl", "SwissLipids");
        DataSetInfo targetDataSetInfo = new DataSetInfo("X", "Affy");
        MappingSetInfo mappingSetInfo = new MappingSetInfo(1, sourceDataSetInfo, "http://www.swisslipids.org/#/entity/$id/", targetDataSetInfo, "http://www.swisslipids.org/#/entity/$id/", "Affy", "SwissLipids", 2, 2, 1, 1);
        assertEquals(1, mappingSetInfo.getNumberOfSources());
        assertNotEquals(2, mappingSetInfo.getNumberOfSources());
    }

    @org.junit.jupiter.api.Test
    public void getNumberOfTargets() {
        DataSetInfo sourceDataSetInfo = new DataSetInfo("Sl", "SwissLipids");
        DataSetInfo targetDataSetInfo = new DataSetInfo("X", "Affy");
        MappingSetInfo mappingSetInfo = new MappingSetInfo(1, sourceDataSetInfo, "http://www.swisslipids.org/#/entity/$id/", targetDataSetInfo, "http://www.swisslipids.org/#/entity/$id/", "Affy", "SwissLipids", 2, 2, 1, 1);
        assertEquals(1, mappingSetInfo.getNumberOfTargets());
        assertNotEquals(2, mappingSetInfo.getNumberOfTargets());
    }
}
