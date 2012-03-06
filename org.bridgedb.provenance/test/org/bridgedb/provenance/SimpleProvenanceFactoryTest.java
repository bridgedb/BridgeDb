/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.provenance;

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
public class SimpleProvenanceFactoryTest extends ProvenanceFactoryTest{
    
    public SimpleProvenanceFactoryTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        factory = new SimpleProvenanceFactory();
    }

}
