/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.bridgedb.IDMapperException;

/**
 *
 * @author Christian
 */
public class ByPossitionURLIterator implements Iterator<String>,Iterable<String>{

    private String bufferedNext;
    private URLByPossition urlByPossition;  
    private String nameSpace;
    int count;
        
    public ByPossitionURLIterator (URLByPossition urlByPossition, String nameSpace){
        this(urlByPossition);
        this.nameSpace = nameSpace;
    }
    
    public ByPossitionURLIterator (URLByPossition urlByPossition){
        this.urlByPossition = urlByPossition;
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
            return urlByPossition.getURLByPossition(count);
        } else {
            return urlByPossition.getURLByPossition(nameSpace, count);
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
