/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;
import org.bridgedb.linkset.*;

import java.io.IOException;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Reporter;
import org.junit.Test;
import org.openrdf.OpenRDFException;

/**
 * @author Christian
 */
public class LinksetLoaderTest {
        
    @Test
    public void testLoader() throws IDMapperException, IOException, OpenRDFException  {
        setupDatasources();
        
        Reporter.report("sample2to1.ttl");
        String[] args1 = {"../org.bridgedb.linksets/test-data/sample1to2.ttl", "testnew"};
        LinksetLoader.main (args1);
        Reporter.report("sample1to3.ttl");
        String[] args2 = {"../org.bridgedb.linksets/test-data/sample1to3.ttl", "test"};
        LinksetLoader.main (args2);
        Reporter.report("sample2to3.ttl");
        String[] args3 = {"../org.bridgedb.linksets/test-data/sample2to3.ttl", "testforce"};
        LinksetLoader.main (args3);
	}

    //copied from IDMapperTestBase
    private void setupDatasources() throws IDMapperException{
        String goodId1 = "123";
        DataSource DataSource1 = DataSource.register("TestDS1", "TestDS1"). urlPattern("http://www.foo.com/$id")
                .idExample(goodId1).asDataSource();
        DataSource DataSource2 = DataSource.register("TestDS2", "TestDS2").urlPattern("http://www.example.com/$id")
                .idExample(goodId1).asDataSource();
        DataSource DataSource3 = DataSource.register("TestDS3", "TestDS3").URISpace("http://www.example.org#")
                .idExample(goodId1).asDataSource();
        //This DataSource MUST not be supported
        DataSource DataSourceBad = DataSource.register("TestDSBad", "TestDSBad")
                .URISpace("www.NotInTheURlMapper.com#").asDataSource();
    }
}
