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
//@Ignore  //WARNING removing ignore will kill the load database!
public class MainLinksetLoaderTest {
        
    @Test
    public void testLoader() throws IDMapperException, IOException, OpenRDFException  {
        
        Reporter.report("sample2to1.ttl");
        String[] args1 = {"../org.bridgedb.linksets/test-data/sample1to2.ttl", "new"};
        LinksetLoader.main (args1);
        Reporter.report("sample1to3.ttl");
        String[] args2 = {"../org.bridgedb.linksets/test-data/sample1to3.ttl", "load"};
        LinksetLoader.main (args2);
        Reporter.report("sample2to3.ttl");
        String[] args3 = {"../org.bridgedb.linksets/test-data/sample2to3.ttl", "load"};
        LinksetLoader.main (args3);
	}

}
