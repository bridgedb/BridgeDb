package org.bridgedb.linkset;

import org.bridgedb.IDMapperException;
import org.bridgedb.linkset.IDMapperLinkset;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 *
 * @author Christian
 */
@Ignore
public class IDMapperLinksetTest extends IDMapperAndLinkListenerTest{
  
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException{
        IDMapperLinkset iDMapperLinkset = new IDMapperLinkset();
        idMapper = iDMapperLinkset;
        listener = iDMapperLinkset;       
        defaultLoadData();
    }
    
}
