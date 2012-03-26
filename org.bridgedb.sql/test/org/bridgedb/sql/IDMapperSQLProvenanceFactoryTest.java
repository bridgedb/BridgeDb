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
public class IDMapperSQLProvenanceFactoryTest extends ProvenanceFactoryTest{
    
    public IDMapperSQLProvenanceFactoryTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        factory = new SimpleProvenanceFactory();
    }

}
