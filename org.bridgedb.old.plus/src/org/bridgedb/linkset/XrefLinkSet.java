package org.bridgedb.linkset;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;

public class XrefLinkSet extends Xref
{	

    private final String linkSetId;
    private final String predicate;
    
	public XrefLinkSet(String id, DataSource ds, String linkSetId, String predicate) {
        super(id, ds);
        this.linkSetId = linkSetId;
        this.predicate = predicate;
	}

    public XrefLinkSet(Xref plainXref, String linkSetId, String predicate) {
       super (plainXref.getId(), plainXref.getDataSource());
        this.linkSetId = linkSetId;
        this.predicate = predicate;
    }
	
    @Override
    public String toString(){
        return super.toString()+ "@" + getLinkSetId();
    }

    /**
     * @return the linkSetId
     */
    public String getLinkSetId() {
        return linkSetId;
    }

    /**
     * @return the predicate
     */
    public String getPredicate() {
        return predicate;
    }
}