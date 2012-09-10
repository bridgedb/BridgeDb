/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import org.bridgedb.metadata.utils.Reporter;
import org.junit.Test;

/**
 *
 * @author Christian
 */
public class FactoryTest {
    @Test
    public void testDataSet() throws Exception {
        Reporter.report("DataSet");
        ResourceMetaData resource = MetaDataRegistry.getResourceByType(VoidConstants.DATASET);
        System.out.println(resource);
    }
}
