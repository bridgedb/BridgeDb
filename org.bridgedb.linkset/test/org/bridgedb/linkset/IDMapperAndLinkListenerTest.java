package org.bridgedb.linkset;

import org.bridgedb.IDMapperException;
import org.bridgedb.provenance.Provenance;
import org.bridgedb.provenance.ProvenanceFactory;
import org.bridgedb.IDMapperTest;

/**
 *
 * @author Christian
 */
public abstract class IDMapperAndLinkListenerTest extends IDMapperTest{
   
    //The Listener may be but needs not be the same as the IDMapper
    protected static LinkListener listener;
    
    //The ProvenanceFactory may but needs not be the same as either the IdMapper or the LinkListener
    protected static ProvenanceFactory provenanceFactory;
    
    /**
     * Suggested method to load the data.
     * 
     * Must be called or replaced.
     * @throws IDMapperException 
     */
    protected static void defaultLoadData() throws IDMapperException{
        Provenance provenance = provenanceFactory.createProvenance("testProvenance", "mapsTo", 1000);
        listener.init(provenance);
        //First set of matches
        listener.insertLink(map1xref1, map1xref2);
        listener.insertLink(map1xref2, map1xref3);
        //As the Listener is not required to support transativity
        listener.insertLink(map1xref1, map1xref3);
        //As the Listner is not required to be Semetric
        listener.insertLink(map1xref2, map1xref1);
        listener.insertLink(map1xref3, map1xref1);
        listener.insertLink(map1xref3, map1xref3);
        
        //Second set of matches
        listener.insertLink(map2xref1, map2xref2);
        listener.insertLink(map2xref2, map2xref3);
        //As the Listener is not required to support transativity
        listener.insertLink(map2xref1, map2xref3);
        //As the Listner is not required to be Semetric
        listener.insertLink(map2xref2, map2xref1);
        listener.insertLink(map2xref3, map2xref1);
        listener.insertLink(map2xref3, map2xref3);

        //Third set of matches
        listener.insertLink(map3xref1, map3xref2);
        listener.insertLink(map3xref2, map3xref3);
        //As the Listener is not required to support transativity
        listener.insertLink(map3xref1, map3xref3);
        //As the Listner is not required to be Semetric
        listener.insertLink(map3xref2, map3xref1);
        listener.insertLink(map3xref3, map3xref1);
        listener.insertLink(map3xref3, map3xref3);
    }
    
}
