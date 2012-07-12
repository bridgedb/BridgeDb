/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;
import org.bridgedb.linkset.*;

import java.io.IOException;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.utils.Reporter;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.OpenRDFException;

/**
 * @author Christian
 */
public class LinksetLoaderTest {
        
    @Test
    public void testLoader() throws IDMapperException, IOException, OpenRDFException  {
        
        Reporter.report("sample2to1.ttl");
        String[] args1 = {"../org.bridgedb.linksets/test-data/sample1to2.ttl", "testnew"};
        LinksetLoader.main (args1);
        Reporter.report("sample1to3.ttl");
        String[] args2 = {"../org.bridgedb.linksets/test-data/sample1to3.ttl", "test"};
        LinksetLoader.main (args2);
        Reporter.report("sample2to3.ttl");
        String[] args3 = {"../org.bridgedb.linksets/test-data/sample2to3.ttl", "test"};
        LinksetLoader.main (args3);
        Reporter.report("cw-cs.ttl");
        String[] args4 = {"../org.bridgedb.linksets/test-data/cw-cs.ttl", "test"};
        LinksetLoader.main (args4);
        Reporter.report("cw-cm.ttl");
        String[] args5 = {"../org.bridgedb.linksets/test-data/cw-cm.ttl", "test"};
        LinksetLoader.main (args5);
        Reporter.report("cw-dd.ttl");
        String[] args6 = {"../org.bridgedb.linksets/test-data/cw-dd.ttl", "test"};
        LinksetLoader.main (args6);
        Reporter.report("cw-ct.ttl");
        String[] args7 = {"../org.bridgedb.linksets/test-data/cw-ct.ttl", "test"};
        LinksetLoader.main (args7);
        Reporter.report("cw-dt.ttl");
        String[] args8 = {"../org.bridgedb.linksets/test-data/cw-dt.ttl", "test"};
        LinksetLoader.main (args8);
	}

}
