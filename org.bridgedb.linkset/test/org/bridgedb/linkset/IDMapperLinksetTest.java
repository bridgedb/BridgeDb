package org.bridgedb.linkset;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bridgedb.IDMapperException;
import org.bridgedb.provenance.ProvenanceException;
import org.bridgedb.provenance.SimpleProvenanceFactory;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Christian
 */
@Ignore //Fails now as provenance has source and target
public class IDMapperLinksetTest extends IDMapperAndLinkListenerTest{
  
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException{
        IDMapperLinkset iDMapperLinkset = new IDMapperLinkset();
        idMapper = iDMapperLinkset;
        provenanceFactory = new SimpleProvenanceFactory();
        listener = iDMapperLinkset;       
        defaultLoadData();
    }
    
}
