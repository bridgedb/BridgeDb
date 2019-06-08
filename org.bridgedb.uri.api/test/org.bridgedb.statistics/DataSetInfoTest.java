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
