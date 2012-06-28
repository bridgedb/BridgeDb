/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.url;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import org.bridgedb.IDMapperException;
import org.bridgedb.Reporter;
import org.bridgedb.url.URISpace;

/**
 *
 * @author Christian
 */
public class ByURISpaceIterable implements Iterable<String> {
    
    private Set<String> URISpaces;
    private URISpace inner;
    private ByURISpaceIterator first;
    
    public ByURISpaceIterable(Set<String> URISpaces, URISpace inner) throws IDMapperException{
        this.URISpaces =  URISpaces;
        this.inner = inner;
        //Lets get the first Iterator so we can return any exceptions
        first = new ByURISpaceIterator(URISpaces, inner);
    }

    @Override
    public Iterator<String> iterator() {
        if (first != null){
           ByURISpaceIterator temp = first;
           first = null;
           return temp;
        }
        try {
            return new ByURISpaceIterator(URISpaces, inner);
        } catch (IDMapperException ex) {
            Reporter.report(ex.getMessage());
            //Can't throw an exception so best we can do is return an empty Iterator.
            return new HashSet<String>().iterator();
        }
    }

    private static class ByURISpaceIterator implements Iterator<String> {

        Iterator<String> URISpaces;
        URISpace inner;
        Iterator<String> innerData;
        
        public ByURISpaceIterator(Set<String> URISpaces, URISpace inner) throws IDMapperException {
            //Make a copy so we can remove URISpaces
            this.URISpaces =  URISpaces.iterator();
            this.inner = inner;
            nextURISpace();
        }

        private boolean nextURISpace() throws IDMapperException{
            if (URISpaces.hasNext()){
                this.innerData = inner.getURLIterator(URISpaces.next()).iterator();
                return true;
            }
            return false;
        }
        
        @Override
        public boolean hasNext() {
            if (innerData.hasNext()) return true;
            try {
                return nextURISpace();
            } catch (IDMapperException ex) {
                //Don't expect this as query ran successfuly once but best we can do is return false;
                Reporter.report(ex.getMessage());
                return false;
            }
        }

        @Override
        public String next() {
            if (innerData.hasNext()){
                return innerData.next();
            }
            try {
                if (nextURISpace()){
                    return innerData.next();
                } else {
                    throw new NoSuchElementException("No more URISpaces to iterate over");
                }
            } catch (IDMapperException ex) {
                Reporter.report(ex.getMessage());
                throw new NoSuchElementException("Error getting next URISpace. " + ex.getMessage());
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}