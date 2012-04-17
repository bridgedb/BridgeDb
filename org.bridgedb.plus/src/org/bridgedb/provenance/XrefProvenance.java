package org.bridgedb.provenance;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;

public class XrefProvenance extends Xref
{	

    private final ProvenanceLink provenanceLink;
    
	public XrefProvenance(String id, DataSource ds, ProvenanceLink provenanceLink) {
        super(id, ds);
        this.provenanceLink = provenanceLink;
	}

    public XrefProvenance(Xref plainXref, ProvenanceLink provenanceLink) {
       super (plainXref.getId(), plainXref.getDataSource());
       this.provenanceLink = provenanceLink;
    }
	
    public ProvenanceLink getProvenanceLink(){
        return provenanceLink;
    }

    @Override
    public String toString(){
        return super.toString()+ "@" + provenanceLink.getId();
    }
}