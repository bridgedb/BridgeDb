/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb;

import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Christian
 */
public class DataSourceAlternativeNameTest {
 
    //Only CONTROLLED will set alternativeNames via Register. As Tested in DataSourceRegisterTest
    
    @Test 
    public void testSecondFullNameControlled() throws IDMapperException{
        String sysCode = "DataSourceAlternativeNameTest_SecondFullNameControlled";
        String fullName1 = "DataSourceAlternativeNameTest_testSecondFullNameControlled1";
        String fullName2 = "DataSourceAlternativeNameTest_testSecondFullNameControlled2";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.CONTROLLED);
        DataSource ds1 = DataSource.register(sysCode, fullName1).asDataSource();
        DataSource ds2 = DataSource.register(sysCode, fullName2).asDataSource();
        Set<String> alternativeNames = ds1.getAlternativeFullNames();
        Set<String> expectedAN = new HashSet<String>();
        expectedAN.add(fullName1);
        Assert.assertEquals(expectedAN , alternativeNames);
    }

    @Test 
    public void testThirdFullNameControlled() throws IDMapperException{
        String sysCode = "DataSourceAlternativeNameTest_ThirdFullNameControlled";
        String fullName1 = "DataSourceAlternativeNameTest_testThirdFullNameControlled1";
        String fullName2 = "DataSourceAlternativeNameTest_testThirdFullNameControlled2";
        String fullName3 = "DataSourceAlternativeNameTest_testThirdFullNameControlled3";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.CONTROLLED);
        DataSource ds1 = DataSource.register(sysCode, fullName1).asDataSource();
        DataSource ds2 = DataSource.register(sysCode, fullName2).asDataSource();
        DataSource ds3 = DataSource.register(sysCode, fullName3).asDataSource();
        Set<String> alternativeNames = ds1.getAlternativeFullNames();
        Set<String> expectedAN = new HashSet<String>();
        expectedAN.add(fullName1);
        expectedAN.add(fullName2);
        Assert.assertEquals(expectedAN , alternativeNames);
    }

    @Test 
    public void test121FullNameControlled() throws IDMapperException{
        System.out.println("121FullNameControlled");
        String sysCode = "DataSourceAlternativeNameTest_121FullNameControlled";
        String fullName1 = "DataSourceAlternativeNameTest_test121FullNameControlled1";
        String fullName2 = "DataSourceAlternativeNameTest_test121FullNameControlled2";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.CONTROLLED);
        DataSource ds1 = DataSource.register(sysCode, fullName1).asDataSource();
        DataSource ds2 = DataSource.register(sysCode, fullName2).asDataSource();
        System.out.println("now 3");
        DataSource ds3 = DataSource.register(sysCode, fullName1).asDataSource();
        Assert.assertEquals(ds1,ds3);
        Set<String> alternativeNames = ds1.getAlternativeFullNames();
        Set<String> expectedAN = new HashSet<String>();
        expectedAN.add(fullName2);
        Assert.assertEquals(expectedAN , alternativeNames);
    }

    @Test 
    public void testSetAlternativeName() throws IDMapperException{
        System.out.println("SetAlternativeName");
        String sysCode = "DataSourceAlternativeNameTest_SetAlternativeName";
        String fullName1 = "DataSourceAlternativeNameTest_testSetAlternativeName1";
        String fullName2 = "DataSourceAlternativeNameTest_testSetAlternativeName2";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.CONTROLLED);
        DataSource ds1 = DataSource.register(sysCode, fullName1)
                .alternativeFullName(fullName2)
                .asDataSource();
        Set<String> alternativeNames = ds1.getAlternativeFullNames();
        Set<String> expectedAN = new HashSet<String>();
        expectedAN.add(fullName2);
        Assert.assertEquals(expectedAN , alternativeNames);
    }
   
    @Test 
    public void testSetRepeatAlternativeName() throws IDMapperException{
        String sysCode = "DataSourceAlternativeNameTest_SetRepeatAlternativeName";
        String fullName1 = "DataSourceAlternativeNameTest_testSetRepeatAlternativeName1";
        String fullName2 = "DataSourceAlternativeNameTest_testSetRepeatAlternativeName2";
        String fullName2a = "DataSourceAlternativeNameTest_testSetRepeatAlternativeName2";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.CONTROLLED);
        DataSource ds1 = DataSource.register(sysCode, fullName1)
                .alternativeFullName(fullName2)
                .alternativeFullName(fullName2a)
                .asDataSource();
        Set<String> alternativeNames = ds1.getAlternativeFullNames();
        Set<String> expectedAN = new HashSet<String>();
        expectedAN.add(fullName2);
        Assert.assertEquals(expectedAN , alternativeNames);
    }
   
    @Test 
    public void testSetAlternativeNameAsFullName() throws IDMapperException{
        System.out.println("SetAlternativeNameAsFullName");
        String sysCode = "DataSourceAlternativeNameTest_SetAlternativeNameAsFullName";
        String fullName1 = "DataSourceAlternativeNameTest_testSetAlternativeNameAsFullName1";
        String fullName2 = "DataSourceAlternativeNameTest_testSetAlternativeNameAsFullName2";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.CONTROLLED);
        DataSource ds1 = DataSource.register(sysCode, fullName1)
                .alternativeFullName(fullName2)
                .asDataSource();
        DataSource ds2 = DataSource.register(sysCode, fullName2).asDataSource();
        Assert.assertEquals(ds1, ds2);
        Set<String> alternativeNames = ds1.getAlternativeFullNames();
        Set<String> expectedAN = new HashSet<String>();
        expectedAN.add(fullName1);
        Assert.assertEquals(expectedAN , alternativeNames);
    }
   
    @Test (expected = IllegalArgumentException.class)
    public void testSetFullNameAsAlternativeName() throws IDMapperException{
        System.out.println("SetFullNameAsAlternativeName");
        String sysCode1 = "DataSourceAlternativeNameTest_SetFullNameAsAlternativeName1";
        String sysCode2 = "DataSourceAlternativeNameTest_SetFullNameAsAlternativeName2";
        String fullName1 = "DataSourceAlternativeNameTest_testSetFullNameAsAlternativeName1";
        String fullName2 = "DataSourceAlternativeNameTest_testSetFullNameAsAlternativeName2";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.CONTROLLED);
        DataSource ds2 = DataSource.register(sysCode1, fullName2).asDataSource();
        DataSource ds1 = DataSource.register(sysCode2, fullName1)
                .alternativeFullName(fullName2)
                .asDataSource();
    }
   
   
}
