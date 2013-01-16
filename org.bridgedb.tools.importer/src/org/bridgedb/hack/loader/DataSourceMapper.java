// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009  BridgeDb developers
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  OpenPhacts
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.bridgedb.hack.loader;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.impl.InternalUtils;

/**
 *
 * @author Christian
 */
public class DataSourceMapper implements IDMapper, IDMapperCapabilities {

    Map<DataSource,Set<DataSource>> mappings = new HashMap<DataSource,Set<DataSource>>();
    Set<DataSource> targetDataSources = null;
    boolean closed = false;
    
    DataSourceMapper(){
        DataSource src = DataSource.register("foo", "foo").urlPattern("http://foo.com#$id").asDataSource();
        DataSource tgt = DataSource.register("id.org_foo", "Identifier.org foo").urlPattern("http://identifiers.org/foo/$id").asDataSource();
        HashSet<DataSource> oneSet = new HashSet<DataSource>();
        oneSet.add(tgt);
        mappings.put(src, oneSet);
    }
            
    @Override
    public Map<Xref, Set<Xref>> mapID(Collection<Xref> srcXrefs, DataSource... tgtDataSources) throws IDMapperException {
        return InternalUtils.mapMultiFromSingle(this, srcXrefs, tgtDataSources);
    }

    @Override
    public Set<Xref> mapID(Xref ref, DataSource... tgtDataSources) throws IDMapperException {
        Set<Xref> results = new HashSet<Xref>();
        Set<DataSource> possibleMappings = mappings.get(ref.getDataSource());
        Iterator<DataSource> possibleIterator = possibleMappings.iterator();
        if (tgtDataSources.length > 0){
            List<DataSource> targets = Arrays.asList(tgtDataSources);
            while (possibleIterator.hasNext()){
                DataSource next = possibleIterator.next();
                if (!targets.contains(next)){
                    possibleIterator.remove();
                }
            }
        }
        for (DataSource possibleMapping:possibleMappings){
            results.add(new Xref(ref.getId(), possibleMapping));
        }
        return results;
    }

    @Override
    public boolean xrefExists(Xref xref) throws IDMapperException {
        //check that xref.getDataSource() is in the mappig keys or one of the sets
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Xref> freeSearch(String text, int limit) throws IDMapperException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IDMapperCapabilities getCapabilities() {
        return this;
    }

    @Override
    public void close() throws IDMapperException {
        closed = true;
    }

    @Override
    public boolean isConnected() {
        return closed;
    }

    @Override
    public boolean isFreeSearchSupported() {
        return false; //Doable but lazy for now
    }

    @Override
    public Set<DataSource> getSupportedSrcDataSources() throws IDMapperException {
        return mappings.keySet();
    }

    @Override
    public Set<DataSource> getSupportedTgtDataSources() throws IDMapperException {
        if (targetDataSources == null){
            targetDataSources = new HashSet<DataSource>();
            Collection<Set<DataSource>> values = mappings.values();
            for (Set<DataSource> oneSet:values){
                targetDataSources.addAll(oneSet);
            }
        }
        return targetDataSources;
    }

    @Override
    public boolean isMappingSupported(DataSource src, DataSource tgt) throws IDMapperException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getProperty(String key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<String> getKeys() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
