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
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

/**
 *
 * @author Christian
 */
public class WrapperIDMapperProvenance extends AbstractIDMapperProvenance{
    
    IDMapper coreMapper;
    Provenance provenace;
    
    public WrapperIDMapperProvenance (IDMapper coreMapper, Provenance provenace){
        this.coreMapper = coreMapper;
        this.provenace = provenace;
    }

    @Override
    public Provenance getProvenance(Xref source, Xref Target) {
        return provenace;
    }

    @Override
    public Map<Xref, Set<Xref>> mapID(Collection<Xref> srcXrefs, DataSource... tgtDataSources) throws IDMapperException {
        return coreMapper.mapID(srcXrefs, tgtDataSources);
    }

    @Override
    public Set<Xref> mapID(Xref ref, DataSource... tgtDataSources) throws IDMapperException {
        return coreMapper.mapID(ref, tgtDataSources);
    }

    @Override
    public boolean xrefExists(Xref xref) throws IDMapperException {
        return coreMapper.xrefExists(xref);
    }

    @Override
    public Set<Xref> freeSearch(String text, int limit) throws IDMapperException {
        return coreMapper.freeSearch(text, limit);
    }

    @Override
    public IDMapperCapabilities getCapabilities() {
        return coreMapper.getCapabilities();
    }

    @Override
    public void close() throws IDMapperException {
        coreMapper.close();
    }

    @Override
    public boolean isConnected() {
        return coreMapper.isConnected();
    }
    
}
