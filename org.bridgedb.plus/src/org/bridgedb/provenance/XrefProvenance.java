package org.bridgedb.provenance;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;

public class XrefProvenance extends Xref
{	

    private final String provenanceId;
    private final String predicate;
    
	public XrefProvenance(String id, DataSource ds, String provenanceId, String predicate) {
        super(id, ds);
        this.provenanceId = provenanceId;
        this.predicate = predicate;
	}

    public XrefProvenance(Xref plainXref, String provenanceId, String predicate) {
       super (plainXref.getId(), plainXref.getDataSource());
        this.provenanceId = provenanceId;
        this.predicate = predicate;
    }
	
    @Override
    public String toString(){
        return super.toString()+ "@" + getProvenanceId();
    }

    /**
     * @return the provenanceId
     */
    public String getProvenanceId() {
        return provenanceId;
    }

    /**
     * @return the predicate
     */
    public String getPredicate() {
        return predicate;
    }
}