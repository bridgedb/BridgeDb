package org.bridgedb.sql;

import java.util.Collection;
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
public abstract class newSQLBase extends SQLListener implements IDMapper {

    private static final int FREESEARCH_CUTOFF = 100000;      
    //Internal parameters
    protected static final int DEFAULT_LIMIT = 1000;
    protected static final int BLOCK_SIZE = 1000;
    protected int blockCount = 0;
    protected int insertCount = 0;
    protected int doubleCount = 0;    
    
    public newSQLBase(boolean dropTables, SQLAccess sqlAccess) throws BridgeDbSqlException{
        super(dropTables, sqlAccess);
     }   

    @Override
    public Map<Xref, Set<Xref>> mapID(Collection<Xref> srcXrefs, DataSource... tgtDataSources) throws IDMapperException {
        return InternalUtils.mapMultiFromSingle(this, srcXrefs, tgtDataSources);
    }

    @Override
    public Set<Xref> mapID(Xref ref, DataSource... tgtDataSources) throws IDMapperException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean xrefExists(Xref xref) throws IDMapperException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Xref> freeSearch(String text, int limit) throws IDMapperException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IDMapperCapabilities getCapabilities() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void close() throws IDMapperException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isConnected() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected abstract void appendMySQLLimitConditions(StringBuilder query, Integer position, Integer limit);

    protected abstract void appendVirtuosoTopConditions(StringBuilder query, Integer position, Integer limit);


}
