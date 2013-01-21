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

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the DataSource class
 *
 * @author Christian
 */
public class DataSourceUrnBaseTest{

    @Test
    public void testRegisterUrnBaseVersion1() throws IDMapperException{
        String fullName = "DataSourceUrnBase_TestRegisterUrnBaseVersion1";
        String rootURL = "http://identifiers.org/" + fullName;
        String urnBase = "urn:miriam:" + fullName;
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.VERSION1);
		DataSource source = DataSource.register(null,  fullName)
                .urnBase(urnBase)
                .asDataSource();
        String id = "1234";
        String result = source.getURN(id);
        String expected = urnBase + ":" + id;
        Assert.assertEquals(result, expected);
        result = source.getIdentifiersOrgUri(id);
        expected = rootURL + "/" + id;
        Assert.assertEquals(result, expected);        
    }

    @Test
    public void testRegisterUrnBaseController() throws IDMapperException{
        String fullName = "DataSourceUrnBase_TestRegisterUrnBaseControlled";
        String rootURL = "http://identifiers.org/" + fullName;
        String urnBase = "urn:miriam:" + fullName;
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.CONTROLLED);
		DataSource source = DataSource.register(null,  fullName)
                .urnBase(urnBase)
                .asDataSource();
        String id = "1234";
        String result = source.getURN(id);
        String expected = urnBase + ":" + id;
        Assert.assertEquals(result, expected);
        result = source.getIdentifiersOrgUri(id);
        expected = rootURL + "/" + id;
        Assert.assertEquals(result, expected);        
    }

    @Test
    public void testRegisterUrnBaseStrict() throws IDMapperException{
        String fullName = "DataSourceUrnBase_TestRegisterUrnBaseStrict";
        String rootURL = "http://identifiers.org/" + fullName;
        String urnBase = "urn:miriam:" + fullName;
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.STRICT);
		DataSource source = DataSource.register(null,  fullName)
                .urnBase(urnBase)
                .asDataSource();
        String id = "1234";
        String result = source.getURN(id);
        String expected = urnBase + ":" + id;
        Assert.assertEquals(result, expected);
        result = source.getIdentifiersOrgUri(id);
        expected = rootURL + "/" + id;
        Assert.assertEquals(result, expected);        
    }

    @Test
    public void testRegisterUrnShortFirstVersion1() throws IDMapperException{
        String shortBase = "shortBase";
        String fullName = "DataSourceUrnBase_testRegisterUrnShortFirstVersion1";
        String rootURL = "http://identifiers.org/" + fullName;
        String urnBase = "urn:miriam:" + fullName;
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.VERSION1);
		DataSource source1 = DataSource.register(null,  fullName)
                .urnBase(shortBase)
                .asDataSource();
        String id = "1234";
        String result = source1.getURN(id);
        String expected = shortBase + ":" + id;
        Assert.assertEquals(expected, result);
        result = source1.getIdentifiersOrgUri(id);
        assertNull(result);  
		DataSource source2 = DataSource.register(null,  fullName)
                .urnBase(urnBase)
                .asDataSource();
        Assert.assertEquals(source1, source2);
        //Note this change is desirable as the miriamUrn is better!
        result = source1.getURN(id);
        expected = urnBase + ":" + id;
        Assert.assertEquals(result, expected);
        result = source2.getIdentifiersOrgUri(id);
        expected = rootURL + "/" + id;
        Assert.assertEquals(expected, result);        
    }

    @Test
    public void testRegisterUrnShortFirstControlled() throws IDMapperException{
        String shortBase = "shortBase";
        String fullName = "DataSourceUrnBase_testRegisterUrnShortFirstControlled";
        String rootURL = "http://identifiers.org/" + fullName;
        String urnBase = "urn:miriam:" + fullName;
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.CONTROLLED);
		DataSource source1 = DataSource.register(null,  fullName)
                .urnBase(shortBase)
                .asDataSource();
        String id = "1234";
        String result = source1.getURN(id);
        String expected = shortBase + ":" + id;
        Assert.assertEquals(expected, result);
        result = source1.getIdentifiersOrgUri(id);
        assertNull(result);  
		DataSource source2 = DataSource.register(null,  fullName)
                .urnBase(urnBase)
                .asDataSource();
        Assert.assertEquals(source1, source2);
        //Note this change is desirable as the miriamUrn is better!
        result = source1.getURN(id);
        expected = urnBase + ":" + id;
        Assert.assertEquals(result, expected);
        result = source2.getIdentifiersOrgUri(id);
        expected = rootURL + "/" + id;
        Assert.assertEquals(expected, result);        
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testRegisterUrnShortFirstStrict() throws IDMapperException{
        String shortBase = "shortBase";
        String fullName = "DataSourceUrnBase_testRegisterUrnShortFirstStrict";
        String rootURL = "http://identifiers.org/" + fullName;
        String urnBase = "urn:miriam:" + fullName;
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.STRICT);
		DataSource source1 = DataSource.register(null,  fullName)
                .urnBase(shortBase)
                .asDataSource();
		DataSource source2 = DataSource.register(null,  fullName)
                .urnBase(urnBase)
                .asDataSource();
    }
    
    @Test
    public void testRegisterUrnLongFirstVersion1() throws IDMapperException{
        String shortBase = "shortBase";
        String fullName = "DataSourceUrnBase_testRegisterUrnShortFirstVersion1";
        String rootURL = "http://identifiers.org/" + fullName;
        String urnBase = "urn:miriam:" + fullName;
		DataSource.setOverwriteLevel(DataSourceOverwriteLevel.VERSION1);
        DataSource source1 = DataSource.register(null,  fullName)
                .urnBase(urnBase)
                .asDataSource();
        String id = "1234";
        String result = source1.getURN(id);
        String expectedUrnlONG = urnBase + ":" + id;
        Assert.assertEquals(expectedUrnlONG, result);
        result = source1.getIdentifiersOrgUri(id);
        String expectedIO = rootURL + "/" + id;
        Assert.assertEquals(expectedIO, result);
		DataSource source2 = DataSource.register(null,  fullName)
                .urnBase(shortBase)
                .asDataSource();
        Assert.assertEquals(source1, source2);
        //Is this change desirable?
        result = source1.getURN(id);
        String expectedUrnShort = shortBase + ":" + id;
        Assert.assertEquals(expectedUrnShort, result);
        result = source2.getIdentifiersOrgUri(id);
        //urnBase is no longer convertable to Identifers.org
        Assert.assertEquals(null, result);        
    }

    @Test
    public void testRegisterUrnLongFirstControlled() throws IDMapperException{
        System.out.println("RegisterUrnLongFirstControlled");
        String shortBase = "shortBase";
        String fullName = "DataSourceUrnBase_testRegisterUrnShortFirstControlled";
        String rootURL = "http://identifiers.org/" + fullName;
        String urnBase = "urn:miriam:" + fullName;
		DataSource.setOverwriteLevel(DataSourceOverwriteLevel.CONTROLLED);
        DataSource source1 = DataSource.register(null,  fullName)
                .urnBase(urnBase)
                .asDataSource();
        String id = "1234";
        String result = source1.getURN(id);
        String expectedUrn = urnBase + ":" + id;
        Assert.assertEquals(expectedUrn, result);
        result = source1.getIdentifiersOrgUri(id);
        String expectedIO = rootURL + "/" + id;
        Assert.assertEquals(expectedIO, result);
		DataSource source2 = DataSource.register(null,  fullName)
                .urnBase(shortBase)
                .asDataSource();
        Assert.assertEquals(source1, source2);
        //Note This behaviour is differentto VERSION ! urn base not overwritten!
        result = source1.getURN(id);
        //Note so this is still true too!
        Assert.assertEquals(expectedUrn, result);
        //Note This behaviour is differentto VERSION ! urn base not overwritten!
        result = source2.getIdentifiersOrgUri(id);
        //Note so this is still true too!
        Assert.assertEquals(expectedIO, result);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testRegisterUrnLongFirstStrict() throws IDMapperException{
        System.out.println("RegisterUrnLongFirstStrict");
        String shortBase = "shortBase";
        String fullName = "DataSourceUrnBase_testRegisterUrnShortFirst";
        String rootURL = "http://identifiers.org/" + fullName;
        String urnBase = "urn:miriam:" + fullName;
		DataSource.setOverwriteLevel(DataSourceOverwriteLevel.STRICT);
        DataSource source1 = DataSource.register(null,  fullName)
                .urnBase(urnBase)
                .asDataSource();
		DataSource source2 = DataSource.register(null,  fullName)
                .urnBase(shortBase)
                .asDataSource();
    }

    @Test  
    public void testRegisterDifferentUrnsVersion1(){
        String fullName = "DataSourceUrnBase_testRegisterDifferentUrnsVersion1";
        String urnBase1 = "urn:miriam:testUrnBase3a";
        String urnBase2 = "urn:miriam:testUrnBase3b";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.VERSION1);
		DataSource source1 = DataSource.register(null,  fullName)
                .urnBase(urnBase1)
                .asDataSource();
        String id = "1234";
        String result = source1.getURN(id);
        String expected = urnBase1 + ":" + id;
        Assert.assertEquals(expected, result);
		DataSource source2 = DataSource.register(null,  fullName)
                .urnBase(urnBase2)
                .asDataSource();
        Assert.assertEquals(source1, source2);
        expected = urnBase2 + ":" + id;
        result = source2.getURN(id);
        Assert.assertEquals(expected, result);
        //Is it desirable that the old urnPattern is overwritten
        result = source1.getURN(id);
        Assert.assertEquals(expected, result);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testRegisterDifferentUrnsControlled(){
        String fullName = "DataSourceUrnBase_testRegisterDifferentUrnsControlled";
        String urnBase1 = "urn:miriam:testUrnBase3a";
        String urnBase2 = "urn:miriam:testUrnBase3b";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.CONTROLLED);
		DataSource source1 = DataSource.register(null,  fullName)
                .urnBase(urnBase1)
                .asDataSource();
		DataSource source2 = DataSource.register(null,  fullName)
                .urnBase(urnBase2)
                .asDataSource();
    }

    @Test (expected = IllegalArgumentException.class)
    public void testRegisterDifferentUrnsStrict(){
        String fullName = "DataSourceUrnBase_testRegisterDifferentUrnsStrict";
        String urnBase1 = "urn:miriam:testUrnBase3a";
        String urnBase2 = "urn:miriam:testUrnBase3b";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.STRICT);
		DataSource source1 = DataSource.register(null,  fullName)
                .urnBase(urnBase1)
                .asDataSource();
		DataSource source2 = DataSource.register(null,  fullName)
                .urnBase(urnBase2)
                .asDataSource();
    }

    @Test  
    public void testRegisterSameUrnVersion1(){
        String fullName = "DataSourceUrnBase_testRegisterSameUrn";
        String urnBase = "urn:miriam:testRegisterSameUrn";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.VERSION1);
		DataSource source1 = DataSource.register(null,  fullName)
                .urnBase(urnBase)
                .asDataSource();
        String id = "1234";
        String result = source1.getURN(id);
        String expected = urnBase + ":" + id;
        Assert.assertEquals(expected, result);
		DataSource source2 = DataSource.register(fullName,  fullName)
                .urnBase(urnBase)
                .asDataSource();
        Assert.assertEquals(source1, source2);
        result = source2.getURN(id);
        Assert.assertEquals(expected, result);
        //Is it desirable that the old urnPattern is overwritten
        result = source1.getURN(id);
        Assert.assertEquals(expected, result);
    }

    @Test  
    public void testRegisterSameUrnControlled(){
        String fullName = "DataSourceUrnBase_testRegisterSameUrnControlled";
        String urnBase = "urn:miriam:testRegisterSameUrn";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.CONTROLLED);
		DataSource source1 = DataSource.register(null,  fullName)
                .urnBase(urnBase)
                .asDataSource();
        String id = "1234";
        String result = source1.getURN(id);
        String expected = urnBase + ":" + id;
        Assert.assertEquals(expected, result);
		DataSource source2 = DataSource.register(fullName,  fullName)
                .urnBase(urnBase)
                .asDataSource();
        Assert.assertEquals(source1, source2);
        result = source2.getURN(id);
        Assert.assertEquals(expected, result);
        //Is it desirable that the old urnPattern is overwritten
        result = source1.getURN(id);
        Assert.assertEquals(expected, result);
    }

    @Test  
    public void testRegisterSameUrnStrict(){
        String fullName = "DataSourceUrnBase_testRegisterSameUrnStrict";
        String urnBase = "urn:miriam:testRegisterSameUrn";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.STRICT);
		DataSource source1 = DataSource.register(null,  fullName)
                .urnBase(urnBase)
                .asDataSource();
        String id = "1234";
        String result = source1.getURN(id);
        String expected = urnBase + ":" + id;
        Assert.assertEquals(expected, result);
		DataSource source2 = DataSource.register(null,  fullName)
                .urnBase(urnBase)
                .asDataSource();
        Assert.assertEquals(source1, source2);
        result = source2.getURN(id);
        Assert.assertEquals(expected, result);
        //Is it desirable that the old urnPattern is overwritten
        result = source1.getURN(id);
        Assert.assertEquals(expected, result);
    }

    //New meathod so only version always acts strict!
    //No equivellent method via the builder ever exisited.
    @Test
    public void testSetIdentifiersOrgUri() throws IDMapperException{
        String fullName = "DataSourceUrnBase_TestIdentifiersOrgUri";
        String rootURL = "http://identifiers.org/" + fullName;
        String urnBase = "urn:miriam:" + fullName;
		DataSource source = DataSource.register(null,  fullName).asDataSource();
        source.setIdentifiersOrgUriBase(rootURL);
        String id = "1234";
        String result = source.getURN(id);
        String expected = urnBase + ":" + id;
        Assert.assertEquals(expected, result);        
        result = source.getIdentifiersOrgUri(id);
        expected = rootURL + "/" + id;
        Assert.assertEquals(expected, result);        
    }

    //Test the extra slash at the end
    @Test
    public void testSetIdentifiersOrgUri2() throws IDMapperException{
        String fullName = "DataSourceUrnBase_TestIdentifiersOrgUri2";
        String rootURL = "http://identifiers.org/" + fullName;
        String urnBase = "urn:miriam:" + fullName;
		DataSource source = DataSource.register(null,  fullName).asDataSource();
        source.setIdentifiersOrgUriBase(rootURL + "/");
        String id = "1234";
        String result = source.getURN(id);
        String expected = urnBase + ":" + id;
        Assert.assertEquals(expected, result);        
        result = source.getIdentifiersOrgUri(id);
        expected = rootURL + "/" + id;
        Assert.assertEquals(expected, result);        
    }

    @Test
    public void testRegisterBothVersion1() throws IDMapperException{
        String fullName = "DataSourceUrnBase_testRegisterBothVersion1)";
        String rootURL = "http://identifiers.org/" + fullName;
        String urnBase = "urn:miriam:" + fullName;
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.VERSION1);
        DataSource source1 = DataSource.register(null, fullName)
                .asDataSource();
        //no builder method exists
        source1.setIdentifiersOrgUriBase(rootURL);
		DataSource source2 = DataSource.register(null, fullName)
                .urnBase(urnBase)
                .asDataSource();
        Assert.assertEquals(source1, source2);        
        String id = "1234";
        String result = source1.getURN(id);
        String expected = urnBase + ":" + id;
        Assert.assertEquals(expected, result);        
        result = source2.getIdentifiersOrgUri(id);
        expected = rootURL + "/" + id;
        Assert.assertEquals(expected, result);        
    }

    @Test
    public void testRegisterBothControlled() throws IDMapperException{
        String fullName = "DataSourceUrnBase_testRegisterBothControlled";
        String rootURL = "http://identifiers.org/" + fullName;
        String urnBase = "urn:miriam:" + fullName;
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.CONTROLLED);
		DataSource source1 = DataSource.register(null, fullName)
                .asDataSource();
        //no builder method exists
        source1.setIdentifiersOrgUriBase(rootURL);
		DataSource source2 = DataSource.register(null, fullName)
                .urnBase(urnBase)
                .asDataSource();
        Assert.assertEquals(source1, source2);        
        String id = "1234";
        String result = source1.getURN(id);
        String expected = urnBase + ":" + id;
        Assert.assertEquals(expected, result);        
        result = source2.getIdentifiersOrgUri(id);
        expected = rootURL + "/" + id;
        Assert.assertEquals(expected, result);        
    }

    @Test
    public void testRegisterBothStrict() throws IDMapperException{
        String fullName = "DataSourceUrnBase_testRegisterBoth()";
        String rootURL = "http://identifiers.org/" + fullName;
        String urnBase = "urn:miriam:" + fullName;
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.STRICT);
		DataSource source1 = DataSource.register(null, fullName)
                .asDataSource();
        //no builder method exists
        source1.setIdentifiersOrgUriBase(rootURL);
		DataSource source2 = DataSource.register(null, fullName)
                .urnBase(urnBase)
                .asDataSource();
        Assert.assertEquals(source1, source2);        
        String id = "1234";
        String result = source1.getURN(id);
        String expected = urnBase + ":" + id;
        Assert.assertEquals(expected, result);        
        result = source2.getIdentifiersOrgUri(id);
        expected = rootURL + "/" + id;
        Assert.assertEquals(expected, result);        
    }

    @Test
    public void testRegisterDifferentUrnBaseToUrnVersion1() throws IDMapperException{
        String fullName = "DataSourceUrnBase_testRegisterDifferentUrnBaseToUrnVersion1";
        String rootURL = "http://identifiers.org/" + fullName + "A";
        String urnBase = "urn:miriam:" + fullName + "B";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.VERSION1);
		DataSource source1 = DataSource.register(null, fullName)
                .asDataSource();
        source1.setIdentifiersOrgUriBase(rootURL);
        String id = "1234";
        String result = source1.getURN(id);
        String expected = "urn:miriam:" + fullName + "A" + ":" + id;
        Assert.assertEquals(expected, result);        
        result = source1.getIdentifiersOrgUri(id);
        expected = rootURL + "/" + id;
        Assert.assertEquals(expected, result);        
		DataSource source2 = DataSource.register(null, fullName)
                .urnBase(urnBase)
                .asDataSource();
        assertEquals(source1, source2);
        //This now different is this good?
        expected = urnBase + ":" + id;
        result = source1.getURN(id);
        Assert.assertEquals(expected, result);        
        //This now different is this good?
        result = source1.getIdentifiersOrgUri(id);
        expected = "http://identifiers.org/" + fullName + "B" + "/" + id;
        Assert.assertEquals(expected, result);               
    }

    @Test (expected = IllegalArgumentException.class)
    public void testRegisterDifferentUrnBaseToUrnControlled() throws IDMapperException{
        String fullName = "DataSourceUrnBase_testRegisterDifferentUrnBaseToUrnControlled";
        String rootURL = "http://identifiers.org/" + fullName + "A";
        String urnBase = "urn:miriam:" + fullName + "B";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.CONTROLLED);
		DataSource source1 = DataSource.register(null, fullName)
                .asDataSource();
        source1.setIdentifiersOrgUriBase(rootURL);
		DataSource source2 = DataSource.register(null, fullName)
                .urnBase(urnBase)
                .asDataSource();
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testRegisterDifferentUrnBaseToUrnStrict() throws IDMapperException{
        String fullName = "DataSourceUrnBase_testRegisterDifferentUrnBaseToUrnStrict";
        String rootURL = "http://identifiers.org/" + fullName + "A";
        String urnBase = "urn:miriam:" + fullName + "B";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.STRICT);
		DataSource source1 = DataSource.register(null, fullName)
                .asDataSource();
        source1.setIdentifiersOrgUriBase(rootURL);
		DataSource source2 = DataSource.register(null, fullName)
                .urnBase(urnBase)
                .asDataSource();
    }

    //As setIdentifiersOrgUriBase is only since Version 2 it is always STRICT
    @Test (expected = IDMapperException.class)   
    public void testSetDifferentUrnBaseToUrn2() throws IDMapperException{
        String fullName = "DataSourceUrnBase_TestDifferentUrnBaseToUrn2";
        String rootURL = "http://identifiers.org/" + fullName + "A";
        String urnBase = "urn:miriam:" + fullName + "B";
		DataSource source = DataSource.register(null, fullName)
                .urnBase(urnBase)
                .asDataSource();
        source.setIdentifiersOrgUriBase(rootURL);
    }

    @Test
    public void testSetUrnBaseNonMiram() throws IDMapperException{
        String fullName = "DataSourceUrnBase_TestSetUrnBaseNonMiram";
		DataSource source = DataSource.register(null, fullName)
                .urnBase(fullName)
                .asDataSource();
        String id = "1234";
        String result = source.getURN(id);
        String expected = fullName + ":" + id;
        Assert.assertEquals(result, expected);
        result = source.getIdentifiersOrgUri(id);
        expected = null;
        Assert.assertEquals(expected, result);        
    }

    @Test
    public void testSetUrnBaseNonMiramVersion1() throws IDMapperException{
        String fullName = "DataSourceUrnBase_TestSetUrnBaseNonMiramVersion1";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.VERSION1);
		DataSource source = DataSource.register(null, fullName)
                .urnBase(fullName)
                .asDataSource();
        String id = "1234";
        String result = source.getURN(id);
        String expected = fullName + ":" + id;
        Assert.assertEquals(result, expected);
        result = source.getIdentifiersOrgUri(id);
        expected = null;
        Assert.assertEquals(expected, result);        
    }

    @Test
    public void testSetUrnBaseNonMiramControlled() throws IDMapperException{
        String fullName = "DataSourceUrnBase_TestSetUrnBaseNonMiramControlled";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.CONTROLLED);
		DataSource source = DataSource.register(null, fullName)
                .urnBase(fullName)
                .asDataSource();
        String id = "1234";
        String result = source.getURN(id);
        String expected = fullName + ":" + id;
        Assert.assertEquals(result, expected);
        result = source.getIdentifiersOrgUri(id);
        expected = null;
        Assert.assertEquals(expected, result);        
    }

    @Test
    public void testRegisterUrnBaseNonMiramStrict() throws IDMapperException{
        String fullName = "DataSourceUrnBase_TestRegisterUrnBaseNonMiramStrict";
        DataSource.setOverwriteLevel(DataSourceOverwriteLevel.STRICT);
		DataSource source = DataSource.register(fullName,  fullName)
                .urnBase(fullName)
                .asDataSource();
        String id = "1234";
        String result = source.getURN(id);
        String expected = fullName + ":" + id;
        Assert.assertEquals(result, expected);
        result = source.getIdentifiersOrgUri(id);
        expected = null;
        Assert.assertEquals(expected, result);        
    }
}
