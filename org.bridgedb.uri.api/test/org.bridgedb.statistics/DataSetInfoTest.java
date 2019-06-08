/*
*BridgeDb,
*An abstraction layer for identifier mapping services, both local and online.
*Copyright (c) 2006-2009 BridgeDb developers
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

import org.junit.Assert;

public class DataSetInfoTest {

    @org.junit.Test
    public void getSysCode() {
        DataSetInfo dataSetInfo = new DataSetInfo("En", "Ensembl");
        Assert.assertEquals("En", dataSetInfo.getSysCode());
    }

    @org.junit.Test
    public void getFullName() {
        DataSetInfo dataSetInfo = new DataSetInfo("En", "Ensembl");
        Assert.assertEquals("Ensembl", dataSetInfo.getFullName());
    }

    @org.junit.Test
    public void equals() {
        DataSetInfo dataSetInfo = new DataSetInfo("En", "Ensembl");
        Object object = new DataSetInfo("En", "Ensembl");
        Assert.assertTrue(object.equals(dataSetInfo));
    }

    @org.junit.Test
    public void compareTo() {
        DataSetInfo dataSetInfo = new DataSetInfo("En", "Ensembl");
        DataSetInfo other = new DataSetInfo("X", "Affy");
        DataSetInfo dataSetInfo1 = new DataSetInfo("En", "Ensembl");
        Assert.assertNotEquals(0, dataSetInfo.compareTo(other));
        Assert.assertEquals(0, dataSetInfo.compareTo(dataSetInfo1));
    }

    @org.junit.Test
    public void testToString() {
        DataSetInfo dataSetInfo = new DataSetInfo(null, "Affy");
        Assert.assertEquals("Affy", dataSetInfo.toString());
        DataSetInfo dataSetInfo1 = new DataSetInfo("En", "Ensembl");
        Assert.assertEquals("En",dataSetInfo1.toString());
    }
}
