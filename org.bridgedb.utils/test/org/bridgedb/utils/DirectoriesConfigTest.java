/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.utils;

import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
public class DirectoriesConfigTest extends TestUtils {
    
    public DirectoriesConfigTest() {
    }

   /**
     * Test of getVoidDirectory method, of class DirectoriesConfig.
     */
    @Test
    public void testGetVoidDirectory() throws Exception {
        report("getVoidDirectory");
        File result = DirectoriesConfig.getVoidDirectory();
    }

    /**
     * Test of getLinksetDirectory method, of class DirectoriesConfig.
     */
    @Test
    public void testGetLinksetDirectory() throws Exception {
        report("getLinksetDirectory");
        File result = DirectoriesConfig.getLinksetDirectory();
    }

    /**
     * Test of getTransativeDirectory method, of class DirectoriesConfig.
     */
    @Test
    public void testGetTransativeDirectory() throws Exception {
        report("getTransativeDirectory");
        File result = DirectoriesConfig.getTransativeDirectory();
    }

    /**
     * Test of getExportDirectory method, of class DirectoriesConfig.
     */
    @Test
    public void testGetExportDirectory() throws Exception {
        report("getExportDirectory");
        File result = DirectoriesConfig.getExportDirectory();
    }
}
