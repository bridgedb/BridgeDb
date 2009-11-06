-- Update one of the transcript xref records to be the gene xref
update IGNORE xref x, +++CORE+++.transcript_stable_id tsid, +++CORE+++.transcript ts, +++CORE+++.gene_stable_id gsid 
set x.dbprimary_acc=gsid.stable_id 
where x.dbprimary_acc=tsid.stable_id  
and tsid.transcript_id=ts.transcript_id 
and ts.gene_id=gsid.gene_id;

-- Now update all of the object_xrefs which are still linked to transcript xref records to point at the new gene xref.
update IGNORE xref xt, xref xg, object_xref ox, +++CORE+++.transcript_stable_id tsid, +++CORE+++.transcript ts, +++CORE+++.gene_stable_id gsid 
set ox.xref_id=xg.xref_id 
where ox.xref_id=xt.xref_id 
and xt.dbprimary_acc=tsid.stable_id 
and tsid.transcript_id=ts.transcript_id 
and ts.gene_id=gsid.gene_id 
and gsid.stable_id=xg.dbprimary_acc;
