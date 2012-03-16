/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.sql;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

/**
 *
 * @author Christian
 */
public class SqlIterator implements Iterator<Xref>,Iterable<Xref>{

    private Xref bufferedNext;
    private IDMapperSQL idMapperSQL;  
    private DataSource dataSource;
    int count;
        
    public SqlIterator (IDMapperSQL idMapperSQL, DataSource dataSource){
        this(idMapperSQL);
        this.dataSource = dataSource;
    }
    
    public SqlIterator (IDMapperSQL idMapperSQL){
        this.idMapperSQL = idMapperSQL;
        count = -1;
    }
   
    @Override
    public boolean hasNext() {
        try {
            bufferedNext = getNext();
            return (bufferedNext != null);
        } catch (IDMapperException ex) {
            return false;
        }
    }

    private Xref getNext() throws IDMapperException{
        count ++;
        if (dataSource == null){
            return idMapperSQL.getXrefByPossition(count);
        } else {
            return idMapperSQL.getXrefByPossitionAndDataSource(dataSource, count);
        }
    }
    
    @Override
    public Xref next() {
        Xref result;
        if (bufferedNext == null){
            try {
                result = getNext();
                if (result == null){
                    throw new NoSuchElementException("End of database reached");
                }
            } catch (IDMapperException ex) {
                ex.printStackTrace();
                throw new NoSuchElementException("Able to generate next due to exception " + ex.getMessage());
            }
        } else {
            result = bufferedNext;
            bufferedNext = null;
        }
        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator<Xref> iterator() {
        return this;
    }

    
}
