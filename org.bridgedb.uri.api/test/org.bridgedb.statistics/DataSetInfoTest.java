/*
*BridgeDb,
*An abstraction layer for identifier mapping services, both local and online.
*Copyright (c) 2019 Manas Awasthi, <marvex17@gmail.com>
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

public class DataSetInfoTest {

    @org.junit.jupiter.api.Test
    public void getSysCode() {
        DataSetInfo dataSetInfo = new DataSetInfo("En", "Ensembl");
        assertEquals("En", dataSetInfo.getSysCode());
    }

    @org.junit.jupiter.api.Test
    public void getFullName() {
        DataSetInfo dataSetInfo = new DataSetInfo("En", "Ensembl");
        assertEquals("Ensembl", dataSetInfo.getFullName());
    }

    @org.junit.jupiter.api.Test
    public void equals() {
        DataSetInfo dataSetInfo = new DataSetInfo("En", "Ensembl");
        Object object = new DataSetInfo("En", "Ensembl");
        assertTrue(object.equals(dataSetInfo));
    }

    @org.junit.jupiter.api.Test
    public void compareTo() {
        DataSetInfo dataSetInfo = new DataSetInfo("En", "Ensembl");
        DataSetInfo other = new DataSetInfo("X", "Affy");
        DataSetInfo dataSetInfo1 = new DataSetInfo("En", "Ensembl");
        assertNotEquals(0, dataSetInfo.compareTo(other));
        assertEquals(0, dataSetInfo.compareTo(dataSetInfo1));
    }

    @org.junit.jupiter.api.Test
    public void testToString() {
        DataSetInfo dataSetInfo = new DataSetInfo(null, "Affy");
        assertEquals("Affy", dataSetInfo.toString());
        DataSetInfo dataSetInfo1 = new DataSetInfo("En", "Ensembl");
        assertEquals("En",dataSetInfo1.toString());
    }
