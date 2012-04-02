package org.bridgedb.provenance;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;

public class XrefProvenance extends Xref
{	

    private final Provenance provenance;
    
	public XrefProvenance(String id, DataSource ds, Provenance provenance) {
        super(id, ds);
        this.provenance = provenance;
	}

    public XrefProvenance(Xref plainXref, Provenance provenance) {
       super (plainXref.getId(), plainXref.getDataSource());
       this.provenance = provenance;
    }
	
    public Provenance getProvenance(){
        return provenance;
    }

    @Override
    public String toString(){
        return super.toString()+ "@" + provenance.getId();
    }
}