/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.sql;

import org.bridgedb.provenance.ProvenanceFactoryTest;
import org.bridgedb.provenance.SimpleProvenanceFactory;
import org.junit.BeforeClass;

/**
 *
 * @author Christian
 */
public class TOFIXProvenanceFactoryTest extends ProvenanceFactoryTest{
    
    public TOFIXProvenanceFactoryTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        factory = new SimpleProvenanceFactory();
    }

}
