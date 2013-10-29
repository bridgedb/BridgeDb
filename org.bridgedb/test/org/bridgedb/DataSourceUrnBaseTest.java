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
    public void testRegisterUrnBase() throws IDMapperException{
        String fullName = "DataSourceUrnBase_TestRegisterUrnBase";
        String rootURL = "http://identifiers.org/" + fullName;
        String urnBase = "urn:miriam:" + fullName;
		DataSource source = DataSource.register(fullName,  fullName)
                .urnBase(urnBase)
                .asDataSource();
        String id = "1234";
        String result = source.getURN(id);
        String expected = urnBase + ":" + id;
        Assert.assertEquals(expected, result);
        result = source.getIdentifiersOrgUri(id);
        expected = rootURL + "/" + id;
        Assert.assertEquals(expected, result);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testRegisterUrnShortFirst() throws IDMapperException{
        String shortBase = "shortBase";
        String fullName = "DataSourceUrnBase_testRegisterUrnShortFirst";
		DataSource source1 = DataSource.register(null,  fullName)
                .urnBase(shortBase)
                .asDataSource();
    }

    @Test (expected = IllegalArgumentException.class)
    public void testRegisterDifferentUrns(){
        String fullName = "DataSourceUrnBase_testRegisterDifferentUrns";
        String urnBase1 = "urn:miriam:testUrnBase3a";
        String urnBase2 = "urn:miriam:testUrnBase3b";
		DataSource source1 = DataSource.register(null,  fullName)
                .urnBase(urnBase1)
                .asDataSource();
		DataSource source2 = DataSource.register(null,  fullName)
                .urnBase(urnBase2)
                .asDataSource();
    }

    @Test  
    public void testRegisterSameUrn(){
        String fullName = "DataSourceUrnBase_testRegisterSameUrn";
        String urnBase = "urn:miriam:testRegisterSameUrn";
		DataSource source1 = DataSource.register(fullName,  fullName)
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
    public void testSetIdentifiersOrgUri() throws IDMapperException{
        String fullName = "DataSourceUrnBase_TestIdentifiersOrgUri";
        String rootURL = "http://identifiers.org/" + fullName;
        String urnBase = "urn:miriam:" + fullName;
		DataSource source = DataSource.register(fullName,  fullName).identifiersOrgBase(rootURL).asDataSource();
        String id = "1234";
        String result = source.getURN(id);
        String expected = urnBase + ":" + id;
        Assert.assertEquals(expected, result);        
        result = source.getIdentifiersOrgUri(id);
        expected = rootURL + "/" + id;
        Assert.assertEquals(expected, result);        
    }
    
    @Test
    public void testRegisterBoth() throws IDMapperException{
        String fullName = "DataSourceUrnBase_testRegisterBoth()";
        String rootURL = "http://identifiers.org/" + fullName;
        String urnBase = "urn:miriam:" + fullName;
		DataSource source1 = DataSource.register(fullName, fullName)
                .identifiersOrgBase(rootURL)
                .asDataSource();
		DataSource source2 = DataSource.register(fullName, fullName)
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

    @Test (expected = IllegalArgumentException.class)
    public void testRegisterDifferentUrnBaseToUrn() throws IDMapperException{
        String fullName = "DataSourceUrnBase_testRegisterDifferentUrnBaseToUrn";
        String rootURL = "http://identifiers.org/" + fullName + "A";
        String urnBase = "urn:miriam:" + fullName + "B";
		DataSource source1 = DataSource.register(fullName, fullName)
                .identifiersOrgBase(rootURL)
                .asDataSource();
		DataSource source2 = DataSource.register(fullName, fullName)
                .urnBase(urnBase)
                .asDataSource();
    }

    @Test (expected = IllegalArgumentException.class)   
    public void testSetDifferentUrnBaseToUrn2() throws IDMapperException{
        String fullName = "DataSourceUrnBase_TestDifferentUrnBaseToUrn2";
        String rootURL = "http://identifiers.org/" + fullName + "A";
        String urnBase = "urn:miriam:" + fullName + "B";
		DataSource source = DataSource.register(fullName, fullName)
                .urnBase(urnBase)
                .identifiersOrgBase(rootURL)
                .asDataSource();
    }

    @Test  
    public void testSetUrnBaseSysCode() throws IDMapperException{
        String sysCode = "DataSourceUrnBase-TestSetUrnBaseNonMiram";
        String fullName = "DataSourceUrnBase_TestSetUrnBaseNonMiram";
		DataSource source = DataSource.register(sysCode, fullName)
                .urnBase(sysCode)
                .asDataSource();
    }

    @Test (expected = IllegalArgumentException.class)   
    public void testSetUrnBaseNonMiram() throws IDMapperException{
        String sysCode = "DataSourceUrnBase-TestSetUrnBaseNonMiram";
        String fullName = "DataSourceUrnBase_TestSetUrnBaseNonMiram";
		DataSource source = DataSource.register(sysCode, fullName)
                .urnBase(fullName)
                .asDataSource();
    }

    @Test (expected = IllegalArgumentException.class)   
    public void testIdentifiersOverWriteNonMiriam() throws IDMapperException{
        String fullName = "DataSourceUrnBase_testIdentifiersOverWriteNonMiriam";
        String rootURL = "http://identifiers.org/" + fullName + "A";
        String urnBase = fullName + "B";
		DataSource source = DataSource.register(fullName, fullName)
                .urnBase(urnBase)
                .identifiersOrgBase(rootURL)
                .asDataSource();
    }

    @Test
    public void testGetBy() throws IDMapperException{
        String fullName = "DataSourceUrnBase_testRegisterBoth()";
        String rootURL = "http://identifiers.org/" + fullName;
        String urnBase = "urn:miriam:" + fullName;
		DataSource source1 = DataSource.register(fullName, fullName).identifiersOrgBase(rootURL)
                .asDataSource();
		DataSource source2 = DataSource.getByUrnBase(urnBase);
        Assert.assertEquals(source1, source2);        
		DataSource source3 = DataSource.getByIdentiferOrgBase(urnBase);
        Assert.assertEquals(source1, source2);        
        
    }

    
}
