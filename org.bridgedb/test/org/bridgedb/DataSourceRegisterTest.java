// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright      2012  Egon Willighagen <egonw@users.sf.net>
// Copyright      2012  OpenPhacts 
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

import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the DataSource class
 *
 * @author Christian
 */
public class DataSourceRegisterTest {

    private static Set<String> NO_ALTERNATIVES = new HashSet<String>();
    
    @Test
    public void testBothVersion1() throws IDMapperException{
        String sysCode = "DataSourceRegistryTest_BothVersion1";
        String fullName = "DataSourceRegistryTest_testBothVersion1";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.VERSION1);
        DataSource ds = DataSource.register(sysCode, fullName).asDataSource();
        Assert.assertEquals(fullName, ds.getFullName());
        Assert.assertEquals(sysCode, ds.getSystemCode());
        Set<String> alternativeNames = ds.getAlternativeFullNames();
        Assert.assertEquals(NO_ALTERNATIVES , alternativeNames);
    }

    @Test
    public void testBothControlled() throws IDMapperException{
        String sysCode = "DataSourceRegistryTest_BothControlled";
        String fullName = "DataSourceRegistryTest_testBothControlled";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.CONTROLLED);
        DataSource ds = DataSource.register(sysCode, fullName).asDataSource();
        Assert.assertEquals(fullName, ds.getFullName());
        Assert.assertEquals(sysCode, ds.getSystemCode());
        Set<String> alternativeNames = ds.getAlternativeFullNames();
        Assert.assertEquals(NO_ALTERNATIVES , alternativeNames);
    }

    @Test
    public void testBothStrict() throws IDMapperException{
        String sysCode = "DataSourceRegistryTest_BothStrict";
        String fullName = "DataSourceRegistryTest_testBothStrict";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.STRICT);
        DataSource ds = DataSource.register(sysCode, fullName).asDataSource();
        Assert.assertEquals(fullName, ds.getFullName());
        Assert.assertEquals(sysCode, ds.getSystemCode());
        Set<String> alternativeNames = ds.getAlternativeFullNames();
        Assert.assertEquals(NO_ALTERNATIVES , alternativeNames);
    }

    @Test
    public void testFullNameOnlyVersion1() throws IDMapperException{
        String fullName = "DataSourceRegistryTest_testFullNameOnlyVersion1";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.VERSION1);
        DataSource ds = DataSource.register(null, fullName).asDataSource();
        Assert.assertEquals(fullName, ds.getFullName());
        Assert.assertEquals(null, ds.getSystemCode());
        Set<String> alternativeNames = ds.getAlternativeFullNames();
        Assert.assertEquals(NO_ALTERNATIVES , alternativeNames);
    }
    
    @Test
    public void testFullNameOnlyControlled() throws IDMapperException{
        String fullName = "DataSourceRegistryTest_testFullNameOnlyControlled";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.CONTROLLED);
        DataSource ds = DataSource.register(null, fullName).asDataSource();
        Assert.assertEquals(fullName, ds.getFullName());
        Assert.assertEquals(null, ds.getSystemCode());
        Set<String> alternativeNames = ds.getAlternativeFullNames();
        Assert.assertEquals(NO_ALTERNATIVES , alternativeNames);
    }
    
    @Test
    public void testFullNameOnlyStrict() throws IDMapperException{
        String fullName = "DataSourceRegistryTest_testFullNameOnlyStrict";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.STRICT);
        DataSource ds = DataSource.register(null, fullName).asDataSource();
        Assert.assertEquals(fullName, ds.getFullName());
        Assert.assertEquals(null, ds.getSystemCode());
        Set<String> alternativeNames = ds.getAlternativeFullNames();
        Assert.assertEquals(NO_ALTERNATIVES , alternativeNames);
    }
        
    @Test
    public void testSysCodeOnlyVersion1() throws IDMapperException{
        String sysCode = "DataSourceRegistryTest_SysCodeOnlyVersion1";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.VERSION1);
        DataSource ds = DataSource.register(sysCode, null).asDataSource();
        Assert.assertEquals(null, ds.getFullName());
        Assert.assertEquals(sysCode, ds.getSystemCode());
        Set<String> alternativeNames = ds.getAlternativeFullNames();
        Assert.assertEquals(NO_ALTERNATIVES , alternativeNames);
    }
    
    @Test
    public void testSysCodeOnlyControlled() throws IDMapperException{
        String sysCode = "DataSourceRegistryTest_SysCodeOnlyControlled";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.CONTROLLED);
        DataSource ds = DataSource.register(sysCode, null).asDataSource();
        Assert.assertEquals(null, ds.getFullName());
        Assert.assertEquals(sysCode, ds.getSystemCode());
        Set<String> alternativeNames = ds.getAlternativeFullNames();
        Assert.assertEquals(NO_ALTERNATIVES , alternativeNames);
    }
    
    @Test
    public void testSysCodeOnlystrict() throws IDMapperException{
        String sysCode = "DataSourceRegistryTest_SysCodeOnlyStrict";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.STRICT);
        DataSource ds = DataSource.register(sysCode, null).asDataSource();
        Assert.assertEquals(null, ds.getFullName());
        Assert.assertEquals(sysCode, ds.getSystemCode());
        Set<String> alternativeNames = ds.getAlternativeFullNames();
        Assert.assertEquals(NO_ALTERNATIVES , alternativeNames);
    }
    
    @Test 
    public void testSecondFullNameVersion1() throws IDMapperException{
        String sysCode = "DataSourceRegistryTest_SecondFullNameVersion1";
        String fullName1 = "DataSourceRegistryTest_testSecondFullNameVersion1";
        String fullName2 = "DataSourceRegistryTest_testSecondFullNameVersion1";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.STRICT);
        DataSource ds1 = DataSource.register(sysCode, fullName1).asDataSource();
        DataSource ds2 = DataSource.register(sysCode, fullName2).asDataSource();
        //Is this behaviour desirable????
        Assert.assertEquals(ds1, ds2);
        //Is this behaviour desirable????
        Assert.assertEquals(fullName2, ds1.getFullName());
        Assert.assertEquals(sysCode, ds1.getSystemCode());
        Assert.assertEquals(fullName2, ds2.getFullName());
        Assert.assertEquals(sysCode, ds2.getSystemCode());
        DataSource ds3 = DataSource.getByFullName(fullName1);
        Assert.assertEquals(ds1, ds3);
        //Is this behaviour desirable????
        Assert.assertEquals(fullName2, ds3.getFullName());
        DataSource ds4 = DataSource.getByFullName(fullName2);
        Assert.assertEquals(ds2, ds4);
        Assert.assertEquals(fullName2, ds4.getFullName());
        Set<String> alternativeNames = ds1.getAlternativeFullNames();
        Assert.assertEquals(NO_ALTERNATIVES , alternativeNames);
    }

    @Test 
    public void testSecondFullNameControlled() throws IDMapperException{
        String sysCode = "DataSourceRegistryTest_SecondFullNameControlled";
        String fullName1 = "DataSourceRegistryTest_testSecondFullNameControlled1";
        String fullName2 = "DataSourceRegistryTest_testSecondFullNameControlled2";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.CONTROLLED);
        DataSource ds1 = DataSource.register(sysCode, fullName1).asDataSource();
        DataSource ds2 = DataSource.register(sysCode, fullName2).asDataSource();
        //Now this is desirable
        Assert.assertEquals(ds1, ds2);
        //Now this is desireable
        Assert.assertEquals(fullName2, ds1.getFullName());
        Assert.assertEquals(sysCode, ds1.getSystemCode());
        Assert.assertEquals(fullName2, ds2.getFullName());
        Assert.assertEquals(sysCode, ds2.getSystemCode());
        DataSource ds3 = DataSource.getByFullName(fullName1);
        Assert.assertEquals(ds1, ds3);
        //Now this is desirable.
        Assert.assertEquals(fullName2, ds3.getFullName());
        DataSource ds4 = DataSource.getByFullName(fullName2);
        Assert.assertEquals(ds2, ds4);
        Assert.assertEquals(fullName2, ds4.getFullName());
        Set<String> alternativeNames = ds4.getAlternativeFullNames();
        Set<String> expectedAN = new HashSet<String>();
        expectedAN.add(fullName1);
        Assert.assertEquals(expectedAN , alternativeNames);
    }

    @Test (expected =  IllegalArgumentException.class)
    public void testSecondFullNameStrict() throws IDMapperException{
        System.out.println("SecondFullNameStrict");
        String sysCode = "DataSourceRegistryTest_SecondFullNameStrict";
        String fullName1 = "DataSourceRegistryTest_testSecondFullNameStrict1";
        String fullName2 = "DataSourceRegistryTest_testSecondFullNameStrict2";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.STRICT);
        DataSource ds1 = DataSource.register(sysCode, fullName1).asDataSource();
        DataSource ds2 = DataSource.register(sysCode, fullName2).asDataSource();
    }

    @Test 
    public void testSecondNullFullNameVersion1() throws IDMapperException{
        System.out.println("SecondNullFullNameVersion1");
        String sysCode = "DataSourceRegistryTest_SecondNullFullNameVersion1";
        String fullName1 = "DataSourceRegistryTest_SecondNullFullNameVersion1";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.VERSION1);
        DataSource ds1 = DataSource.register(sysCode, fullName1).asDataSource();
        DataSource ds2 = DataSource.register(sysCode, null).asDataSource();
        //Is this behaviour desirable????
        Assert.assertEquals(ds1, ds2);
        //Is this behaviour desirable????
        Assert.assertEquals(null, ds1.getFullName());
        Assert.assertEquals(sysCode, ds1.getSystemCode());
        Assert.assertEquals(null, ds2.getFullName());
        Assert.assertEquals(sysCode, ds2.getSystemCode());
        DataSource ds3 = DataSource.getByFullName(fullName1);
        Assert.assertEquals(ds1, ds3);
        //Is this behaviour desirable????
        Assert.assertEquals(null, ds3.getFullName());
        Set<String> alternativeNames = ds3.getAlternativeFullNames();
        Assert.assertEquals(NO_ALTERNATIVES , alternativeNames);
    }
    
    @Test 
    public void testSecondNullFullNameControlled() throws IDMapperException{
        System.out.println("SecondNullFullNameControlled");
        String sysCode = "DataSourceRegistryTest_SecondNullFullNameControlled";
        String fullName1 = "DataSourceRegistryTest_SecondNullFullNameControlled";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.CONTROLLED);
        DataSource ds1 = DataSource.register(sysCode, fullName1).asDataSource();
        System.out.println("ds1 = " + ds1.toString());
        DataSource ds2 = DataSource.register(sysCode, null).asDataSource();
        //This behaviour is now desirable
        Assert.assertEquals(ds1, ds2);
        //Different to Version1
        System.out.println("ds1 = " + ds1.toString());
        Assert.assertEquals(fullName1, ds1.getFullName());
        Assert.assertEquals(sysCode, ds1.getSystemCode());
        //Different to Version1
        Assert.assertEquals(fullName1, ds2.getFullName());
        Assert.assertEquals(sysCode, ds2.getSystemCode());
        DataSource ds3 = DataSource.getByFullName(fullName1);
        Assert.assertEquals(ds1, ds3);
        //Different to Version1
        Assert.assertEquals(fullName1, ds3.getFullName());
        Set<String> alternativeNames = ds3.getAlternativeFullNames();
        Assert.assertEquals(NO_ALTERNATIVES , alternativeNames);
    }

    @Test (expected =  IllegalArgumentException.class)
    public void testSecondNullFullNameStrict() throws IDMapperException{
        System.out.println("SecondNullFullNameStrict");
        String sysCode = "DataSourceRegistryTest_SecondNullFullNameStrict";
        String fullName1 = "DataSourceRegistryTest_SecondNullFullNameStrict";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.STRICT);
        DataSource ds1 = DataSource.register(sysCode, fullName1).asDataSource();
        DataSource ds2 = DataSource.register(sysCode, null).asDataSource();
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.STRICT);
    }
    
    @Test 
    public void testNullThenFullNameVersion1() throws IDMapperException{
        String sysCode = "DataSourceRegistryTest_NullThenFullNameVersion1";
        String fullName2 = "DataSourceRegistryTest_testNullThenFullNameVersion1";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.CONTROLLED);
        DataSource ds1 = DataSource.register(sysCode, null).asDataSource();
        DataSource ds2 = DataSource.register(sysCode, fullName2).asDataSource();
        //Is this behaviour desirable????
        Assert.assertEquals(ds1, ds2);
        //Is this behaviour desirable????
        Assert.assertEquals(fullName2, ds1.getFullName());
        Assert.assertEquals(sysCode, ds1.getSystemCode());
        Assert.assertEquals(fullName2, ds2.getFullName());
        Assert.assertEquals(sysCode, ds2.getSystemCode());
        DataSource ds4 = DataSource.getByFullName(fullName2);
        Assert.assertEquals(ds2, ds4);
        Assert.assertEquals(fullName2, ds4.getFullName());
        Set<String> alternativeNames = ds4.getAlternativeFullNames();
        Assert.assertEquals(NO_ALTERNATIVES , alternativeNames);
    }
    
    @Test 
    public void testNullThenFullNameControlled() throws IDMapperException{
        String sysCode = "DataSourceRegistryTest_NullThenFullNameControlled";
        String fullName2 = "DataSourceRegistryTest_testNullThenFullNameControlled";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.CONTROLLED);
        DataSource ds1 = DataSource.register(sysCode, null).asDataSource();
        DataSource ds2 = DataSource.register(sysCode, fullName2).asDataSource();
        //This behaviour is desirable
        Assert.assertEquals(ds1, ds2);
        //This behaviour is desirable
        Assert.assertEquals(fullName2, ds1.getFullName());
        Assert.assertEquals(sysCode, ds1.getSystemCode());
        Assert.assertEquals(fullName2, ds2.getFullName());
        Assert.assertEquals(sysCode, ds2.getSystemCode());
        DataSource ds4 = DataSource.getByFullName(fullName2);
        Assert.assertEquals(ds2, ds4);
        Assert.assertEquals(fullName2, ds4.getFullName());
        Set<String> alternativeNames = ds4.getAlternativeFullNames();
        Assert.assertEquals(NO_ALTERNATIVES , alternativeNames);
    }
    
    @Test (expected =  IllegalArgumentException.class)
    public void testNullThenFullNameStrict() throws IDMapperException{
        String sysCode = "DataSourceRegistryTest_NullThenFullNameStrict";
        String fullName2 = "DataSourceRegistryTest_testNullThenFullNameStrict";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.STRICT);
        DataSource ds1 = DataSource.register(sysCode, null).asDataSource();
        DataSource ds2 = DataSource.register(sysCode, fullName2).asDataSource();
    }
    
    @Test 
    public void testSecondSysCodeVersion1() throws IDMapperException{
        String sysCode1 = "DataSourceRegistryTest_SecondSysCodeVersion1";
        String sysCode2 = "DataSourceRegistryTest_SecondSysCodeVersion1";
        String fullName = "DataSourceRegistryTest_testSecondSysCodeVersion1";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.VERSION1);
        DataSource ds1 = DataSource.register(sysCode1, fullName).asDataSource();
        DataSource ds2 = DataSource.register(sysCode2, fullName).asDataSource();
        //Is this behaviour desirable????
        Assert.assertEquals(ds1, ds2);
        //Is this behaviour desirable????
        Assert.assertEquals(fullName, ds1.getFullName());
        //Is this behaviour desirable????
        Assert.assertEquals(sysCode2, ds1.getSystemCode());
        Assert.assertEquals(fullName, ds2.getFullName());
        Assert.assertEquals(sysCode2, ds2.getSystemCode());
        DataSource ds3 = DataSource.getBySystemCode(sysCode1);
        Assert.assertEquals(ds1, ds3);
        //Is this behaviour desirable????
        Assert.assertEquals(sysCode2, ds3.getSystemCode());
        DataSource ds4 = DataSource.getBySystemCode(sysCode2);
        Assert.assertEquals(ds2, ds4);
        Assert.assertEquals(sysCode2, ds4.getSystemCode());
        Set<String> alternativeNames = ds2.getAlternativeFullNames();
        Assert.assertEquals(NO_ALTERNATIVES , alternativeNames);
    }

    /**
     * Note: There is currently no know case of assigning the same sysCode to two different full names.
     * However if required this should be supported!
     * So the behaviour displayed in this test could be CHANGED!
     * @throws IDMapperException 
     */
    @Test (expected =  IllegalArgumentException.class)
    public void testSecondSysCodeControlled() throws IDMapperException{
        String sysCode1 = "DataSourceRegistryTest_SecondSysCodeControlled1";
        String sysCode2 = "DataSourceRegistryTest_SecondSysCodeControlled2";
        String fullName = "DataSourceRegistryTest_testSecondSysCodeControlled";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.CONTROLLED);
        DataSource ds1 = DataSource.register(sysCode1, fullName).asDataSource();
        DataSource ds2 = DataSource.register(sysCode2, fullName).asDataSource();
    }

    @Test (expected =  IllegalArgumentException.class)
    public void testSecondSysCodeStrict() throws IDMapperException{
        String sysCode1 = "DataSourceRegistryTest_SecondSysCodeStrict1";
        String sysCode2 = "DataSourceRegistryTest_SecondSysCodeStrict2";
        String fullName = "DataSourceRegistryTest_testFactorySecondSysCode";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.STRICT);
        DataSource ds1 = DataSource.register(sysCode1, fullName).asDataSource();
        DataSource ds2 = DataSource.register(sysCode2, fullName).asDataSource();
    }

    @Test 
    public void testSecondNullSysCodeVersion1() throws IDMapperException{
        String sysCode1 = "DataSourceRegistryTest_SecondNullSysCodeVersion1";
        String fullName = "DataSourceRegistryTest_testSecondNullSysCodeVersion1";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.VERSION1);
        DataSource ds1 = DataSource.register(sysCode1, fullName).asDataSource();
        DataSource ds2 = DataSource.register(null, fullName).asDataSource();
        //Is this behaviour desirable????
        Assert.assertEquals(ds1, ds2);
        Assert.assertEquals(fullName, ds1.getFullName());
        //Is this behaviour desirable????
        Assert.assertEquals(null, ds1.getSystemCode());
        Assert.assertEquals(fullName, ds2.getFullName());
        //Is this behaviour desirable????
        Assert.assertEquals(null, ds2.getSystemCode());
        DataSource ds3 = DataSource.getBySystemCode(sysCode1);
        Assert.assertEquals(ds1, ds3);
        //Is this behaviour desirable????
        Assert.assertEquals(null, ds3.getSystemCode());
        Set<String> alternativeNames = ds3.getAlternativeFullNames();
        Assert.assertEquals(NO_ALTERNATIVES , alternativeNames);
    }

    @Test 
    public void testSecondNullSysCodeControlled() throws IDMapperException{
        String sysCode1 = "DataSourceRegistryTest_SecondNullSysCodeControlled";
        String fullName = "DataSourceRegistryTest_SecondNullSysCodeControlled";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.CONTROLLED);
        DataSource ds1 = DataSource.register(sysCode1, fullName).asDataSource();
        DataSource ds2 = DataSource.register(null, fullName).asDataSource();
        //This is now desirable
        Assert.assertEquals(ds1, ds2);
        Assert.assertEquals(fullName, ds1.getFullName());
        //This is different to Version1
        Assert.assertEquals(sysCode1, ds1.getSystemCode());
        Assert.assertEquals(fullName, ds2.getFullName());
        //This is different to Version1
        Assert.assertEquals(sysCode1, ds2.getSystemCode());
        DataSource ds3 = DataSource.getBySystemCode(sysCode1);
        Assert.assertEquals(ds1, ds3);
        //This is different to Version1
        Assert.assertEquals(sysCode1, ds3.getSystemCode());
        Set<String> alternativeNames = ds3.getAlternativeFullNames();
        Assert.assertEquals(NO_ALTERNATIVES , alternativeNames);
    }

    @Test (expected =  IllegalArgumentException.class)
    public void testSecondNullSysCodeStrict() throws IDMapperException{
        String sysCode1 = "DataSourceRegistryTest_RegisterSecondNullSysCodeStrict1";
        String fullName = "DataSourceRegistryTest_testSecondNullSysCodeStrict";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.STRICT);
        DataSource ds1 = DataSource.register(sysCode1, fullName).asDataSource();
        DataSource ds2 = DataSource.register(null, fullName).asDataSource();
    }

    @Test 
    public void testNullThenSysCodeVersion1() throws IDMapperException{
        String sysCode2 = "DataSourceRegistryTest_NullThenSysCodeVersion1";
        String fullName = "DataSourceRegistryTest_NullThenSysCodeVersion1";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.VERSION1);
        DataSource ds1 = DataSource.register(null, fullName).asDataSource();
        DataSource ds2 = DataSource.register(sysCode2, fullName).asDataSource();
        //Is this behaviour desirable????
        Assert.assertEquals(ds1, ds2);
        Assert.assertEquals(fullName, ds1.getFullName());
        //Is this behaviour desirable????
        Assert.assertEquals(sysCode2, ds1.getSystemCode());
        Assert.assertEquals(fullName, ds2.getFullName());
        Assert.assertEquals(sysCode2, ds2.getSystemCode());
        DataSource ds4 = DataSource.getBySystemCode(sysCode2);
        Assert.assertEquals(ds2, ds4);
        Assert.assertEquals(sysCode2, ds4.getSystemCode());
        Set<String> alternativeNames = ds4.getAlternativeFullNames();
        Assert.assertEquals(NO_ALTERNATIVES , alternativeNames);
    }

    @Test 
    public void testNullThenSysCodeControlled() throws IDMapperException{
        String sysCode2 = "DataSourceRegistryTest_NullThenSysCodeControlled";
        String fullName = "DataSourceRegistryTest_testNullThenSysCodeControlled";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.CONTROLLED);
        DataSource ds1 = DataSource.register(null, fullName).asDataSource();
        DataSource ds2 = DataSource.register(sysCode2, fullName).asDataSource();
        //This behaviour is now desirable
        Assert.assertEquals(ds1, ds2);
        Assert.assertEquals(fullName, ds1.getFullName());
        //This behavior is now desirable
        Assert.assertEquals(sysCode2, ds1.getSystemCode());
        Assert.assertEquals(fullName, ds2.getFullName());
        Assert.assertEquals(sysCode2, ds2.getSystemCode());
        DataSource ds4 = DataSource.getBySystemCode(sysCode2);
        Assert.assertEquals(ds2, ds4);
        Assert.assertEquals(sysCode2, ds4.getSystemCode());
        Set<String> alternativeNames = ds4.getAlternativeFullNames();
        Assert.assertEquals(NO_ALTERNATIVES , alternativeNames);
    }

    @Test (expected =  IllegalArgumentException.class)
    public void testNullThenSysCodeStrict() throws IDMapperException{
        String sysCode2 = "DataSourceRegistryTest_NullThenSysCodeStrict";
        String fullName = "DataSourceRegistryTest_NullThenSysCodeStrict";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.STRICT);
        DataSource ds1 = DataSource.register(null, fullName).asDataSource();
        DataSource ds2 = DataSource.register(sysCode2, fullName).asDataSource();
    }

    @Test 
    public void testFullNameNullVersion1() throws IDMapperException{
        System.out.println("FullNameNullVersion1");
        String sysCode = "DataSourceRegistryTest_FullNameNullVersion1";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.VERSION1);
        DataSource ds1 = DataSource.register(sysCode, null).asDataSource();
        DataSource ds2 = DataSource.register(sysCode, null).asDataSource();
        //Is this behaviour desirable????
        Assert.assertEquals(ds1, ds2);
        //Is this behaviour desirable????
        Assert.assertEquals(null, ds1.getFullName());
        Assert.assertEquals(sysCode, ds1.getSystemCode());
        Assert.assertEquals(null, ds2.getFullName());
        Assert.assertEquals(sysCode, ds2.getSystemCode());
    }

    @Test 
    public void testFullNameNullControlled() throws IDMapperException{
        System.out.println("FullNameNullControlled");
        String sysCode = "DataSourceRegistryTest_FullNameNullControlled";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.CONTROLLED);
        DataSource ds1 = DataSource.register(sysCode, null).asDataSource();
        DataSource ds2 = DataSource.register(sysCode, null).asDataSource();
        Assert.assertEquals(ds1, ds2);
        System.out.println(ds1);
        Assert.assertEquals(null, ds1.getFullName());
        Assert.assertEquals(sysCode, ds1.getSystemCode());
        Assert.assertEquals(null, ds2.getFullName());
        Assert.assertEquals(sysCode, ds2.getSystemCode());
    }
    
    @Test 
    public void testFullNameNullStrict() throws IDMapperException{
        System.out.println("FullNameNullStrict");
        String sysCode = "DataSourceRegistryTest_FullNameNullStrict";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.STRICT);
        DataSource ds1 = DataSource.register(sysCode, null).asDataSource();
        DataSource ds2 = DataSource.register(sysCode, null).asDataSource();
        //Is this behaviour desirable????
        Assert.assertEquals(ds1, ds2);
        //Is this behaviour desirable????
        Assert.assertEquals(null, ds1.getFullName());
        Assert.assertEquals(sysCode, ds1.getSystemCode());
        Assert.assertEquals(null, ds2.getFullName());
        Assert.assertEquals(sysCode, ds2.getSystemCode());
    }
}
