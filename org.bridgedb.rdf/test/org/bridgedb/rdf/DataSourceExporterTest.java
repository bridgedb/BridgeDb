/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.io.File;
import org.bridgedb.bio.BioDataSource;
import org.bridgedb.utils.TestUtils;
import org.junit.Test;

/**
 *
 * @author Christian
 */
public class DataSourceExporterTest extends TestUtils{
    
    /**
     * Test of main method, of class DataSourceExporter.
     */
    @Test
    public void testExport() throws Exception {
        report("Export");
        BioDataSource.init();
        File file = new File("test-data/CreatedByTest.rdf");
        DataSourceExporter.export(file);
    }
}
