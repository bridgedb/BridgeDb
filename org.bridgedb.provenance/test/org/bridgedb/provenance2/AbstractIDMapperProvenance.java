/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.provenance;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

/**
 *
 * @author Christian
 */
public abstract class AbstractIDMapperProvenance implements IDMapperProvenance {

    @Override
    public Set<XrefProvenance> mapIDProvenance(Xref ref, DataSource... tgtDataSources) throws IDMapperException {
        Set<Xref> plainXrefs = mapID(ref, tgtDataSources);
        return addProvenance(ref, plainXrefs);
    }

    private Set<XrefProvenance> addProvenance(Xref ref, Set<Xref> plainXrefs){
        HashSet<XrefProvenance> xrefProvenances = new HashSet<XrefProvenance>();
        for (Xref plainXref: plainXrefs){
            if (plainXref instanceof XrefProvenance) {
                xrefProvenances.add((XrefProvenance)plainXref);
            } else {
                Provenance provenance = getProvenance(ref, plainXref);
                XrefProvenance xrefProvenance = new XrefProvenance(plainXref, provenance);
                xrefProvenances.add(xrefProvenance);
            }
        }
        return xrefProvenances;        
    }

    @Override
    public Map<Xref, Set<XrefProvenance>> mapIDProvenance(Collection<Xref> srcXrefs, DataSource... tgtDataSources) throws IDMapperException {
       Map<Xref, Set<Xref>> plainRefs = mapID(srcXrefs, tgtDataSources);
       HashMap<Xref, Set<XrefProvenance>> result = new HashMap<Xref, Set<XrefProvenance>>();
       Set<Xref> keySet = plainRefs.keySet();
       for (Xref key:keySet){
           result.put(key, addProvenance(key, plainRefs.get(key)));
       }
       return result;
    }

}
