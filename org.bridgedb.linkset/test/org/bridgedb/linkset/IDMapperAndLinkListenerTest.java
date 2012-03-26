package org.bridgedb.linkset;

import java.util.Date;
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
        Provenance provenance;
        provenance = provenanceFactory.createProvenance(DataSource1, "mapsTo", DataSource2, 
                "testProvenance", new Date().getTime());
        listener.openInput(provenance);
        listener.insertLink(map1xref1, map1xref2);
        listener.insertLink(map2xref1, map2xref2);
        listener.insertLink(map3xref1, map3xref2);
        listener.closeInput();
        
        provenance = provenanceFactory.createProvenance(DataSource1, "mapsTo", DataSource3, 
                "testProvenance", new Date().getTime());
        listener.openInput(provenance);
        listener.insertLink(map1xref1, map1xref3);
        listener.insertLink(map2xref1, map2xref3);
        listener.insertLink(map3xref1, map3xref3);
        listener.closeInput();

        provenance = provenanceFactory.createProvenance(DataSource2, "mapsTo", DataSource1, 
                "testProvenance", new Date().getTime());
        listener.openInput(provenance);
        listener.insertLink(map1xref2, map1xref1);
        listener.insertLink(map2xref2, map2xref1);
        listener.insertLink(map3xref2, map3xref1);
        listener.closeInput();

        provenance = provenanceFactory.createProvenance(DataSource2, "mapsTo", DataSource3, 
                "testProvenance", new Date().getTime());
        listener.openInput(provenance);
        listener.insertLink(map1xref2, map1xref3);
        listener.insertLink(map2xref2, map2xref3);
        listener.insertLink(map3xref2, map3xref3);
        listener.closeInput();

        provenance = provenanceFactory.createProvenance(DataSource3, "mapsTo", DataSource1, 
                "testProvenance", new Date().getTime());
        listener.openInput(provenance);
        listener.insertLink(map1xref3, map1xref1);
        listener.insertLink(map2xref3, map2xref1);
        listener.insertLink(map3xref3, map3xref1);
        listener.closeInput();

        provenance = provenanceFactory.createProvenance(DataSource3, "mapsTo", DataSource2, 
                "testProvenance", new Date().getTime());
        listener.openInput(provenance);
        listener.insertLink(map1xref3, map1xref2);
        listener.insertLink(map2xref3, map2xref2);
        listener.insertLink(map3xref3, map3xref2);
        listener.closeInput();
     }
    
}
