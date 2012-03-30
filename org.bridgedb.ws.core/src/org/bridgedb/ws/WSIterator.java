/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.ws.bean.XrefBean;

/**
 *
 * @author Christian
 */
public class WSIterator implements Iterator<Xref>,Iterable<Xref>{

    private ArrayList<Xref> buffered;
    private WSInterface webService;  
    private DataSource dataSource;
    int count;
    private static final int XREFS_TO_BUFFER = 10;
        
    public WSIterator (WSInterface webService, DataSource dataSource){
        this(webService);
        this.dataSource = dataSource;
    }
    
    public WSIterator (WSInterface webService){
        this.webService = webService;
        count = 0;
        buffered = new ArrayList<Xref>(XREFS_TO_BUFFER);
    }
   
    @Override
    public boolean hasNext() {
        if (buffered.isEmpty()){
            try {
                fillBuffer();
                return (!buffered.isEmpty());
            } catch (IDMapperException ex) {
                return false;
            }
        } else {
            return true;
        }
    }

    private void fillBuffer() throws IDMapperException{
        List<XrefBean> beans;
        if (dataSource == null){
            beans = webService.getXrefByPosition(null, count, XREFS_TO_BUFFER);
        } else {
            beans = webService.getXrefByPosition(dataSource.getSystemCode(), count, XREFS_TO_BUFFER);
        }
        count += XREFS_TO_BUFFER;
        buffered.clear();
        for (XrefBean bean:beans){
            buffered.add(bean.asXref());
        }
    }
    
    @Override
    public Xref next() {
        Xref result;
        if (buffered.isEmpty()){
            try {
                fillBuffer();
                if (buffered.isEmpty()){
                    throw new NoSuchElementException("End of Iterator reached");
                }
            } catch (IDMapperException ex) {
                throw new NoSuchElementException("Unable to generate next due to exception " + ex.getMessage());
            }
        }
        result = buffered.get(0);
        buffered.remove(0);
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
