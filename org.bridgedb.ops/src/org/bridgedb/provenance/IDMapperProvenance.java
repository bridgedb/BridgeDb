/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.provenance;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

/**
 *
 * @author Christian
 */
public interface IDMapperProvenance extends IDMapper{
    
    public Set<XrefProvenance> mapIDProvenance (Xref ref, DataSource... tgtDataSources) throws IDMapperException;
    
    public Map<Xref, Set<XrefProvenance>> mapIDProvenance(Collection<Xref> srcXrefs, DataSource... tgtDataSources) throws IDMapperException;

    public Provenance getProvenance(Xref source, Xref Target);
}
