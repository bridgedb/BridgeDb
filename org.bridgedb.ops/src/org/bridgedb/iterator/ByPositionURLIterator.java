package org.bridgedb.iterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.bridgedb.IDMapperException;
import org.bridgedb.url.OpsMapper;

/**
 *
 * @author Christian
 */
public class ByPositionURLIterator implements Iterator<String>,Iterable<String>{

    private String bufferedNext;
    private OpsMapper opsMapper;  
    private ArrayList<String> nameSpaces;
    int position;
         
    //Statics for easy readability of method calls
    private static final ArrayList<String> ALL_PROVENANCE_IDS = new ArrayList<String>();

    public ByPositionURLIterator (OpsMapper opsMapper, String nameSpace){
        this(opsMapper);
        nameSpaces.add(nameSpace);
    }
    
    public ByPositionURLIterator (OpsMapper opsMapper){
        this.opsMapper = opsMapper;
        nameSpaces = new ArrayList<String>();
        position = -1;
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

    private String getNext() throws IDMapperException{
        position ++;
        List<String> list = opsMapper.getURLs(nameSpaces, ALL_PROVENANCE_IDS, position, 1);
        if (list.isEmpty()){
            return null;
        }
        return list.get(0);
    }
    
    @Override
    public String next() {
        String result;
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
    public Iterator<String> iterator() {
        return this;
    }

    
}
