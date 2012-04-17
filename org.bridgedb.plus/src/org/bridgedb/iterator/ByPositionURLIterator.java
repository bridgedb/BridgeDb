package org.bridgedb.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.bridgedb.IDMapperException;

/**
 *
 * @author Christian
 */
public class ByPositionURLIterator implements Iterator<String>,Iterable<String>{

    private String bufferedNext;
    private URLByPosition urlByPosition;  
    private String nameSpace;
    int count;
        
    public ByPositionURLIterator (URLByPosition urlByPosition, String nameSpace){
        this(urlByPosition);
        this.nameSpace = nameSpace;
    }
    
    public ByPositionURLIterator (URLByPosition urlByPosition){
        this.urlByPosition = urlByPosition;
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

    private String getNext() throws IDMapperException{
        count ++;
        if (nameSpace == null){
            return urlByPosition.getURLByPosition(count);
        } else {
            return urlByPosition.getURLByPosition(nameSpace, count);
        }
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
