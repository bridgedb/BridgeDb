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
import org.bridgedb.url.URLIterator;

/**
 *
 * @author Christian
 */
public class ByNameSpaceIterable implements Iterable<String> {
    
    private Set<String> nameSpaces;
    private URLIterator inner;
    private ByNameSpaceIterator first;
    
    public ByNameSpaceIterable(Set<String> nameSpaces, URLIterator inner) throws IDMapperException{
        this.nameSpaces =  nameSpaces;
        this.inner = inner;
        //Lets get the first Iterator so we can return any exceptions
        first = new ByNameSpaceIterator(nameSpaces, inner);
    }

    @Override
    public Iterator<String> iterator() {
        if (first != null){
           ByNameSpaceIterator temp = first;
           first = null;
           return temp;
        }
        try {
            return new ByNameSpaceIterator(nameSpaces, inner);
        } catch (IDMapperException ex) {
            Reporter.report(ex.getMessage());
            //Can't throw an exception so best we can do is return an empty Iterator.
            return new HashSet<String>().iterator();
        }
    }

    private static class ByNameSpaceIterator implements Iterator<String> {

        Iterator<String> nameSpaces;
        URLIterator inner;
        Iterator<String> innerData;
        
        public ByNameSpaceIterator(Set<String> nameSpaces, URLIterator inner) throws IDMapperException {
            //Make a copy so we can remove namespaces
            this.nameSpaces =  nameSpaces.iterator();
            this.inner = inner;
            nextNameSpace();
        }

        private boolean nextNameSpace() throws IDMapperException{
            if (nameSpaces.hasNext()){
                this.innerData = inner.getURLIterator(nameSpaces.next()).iterator();
                return true;
            }
            return false;
        }
        
        @Override
        public boolean hasNext() {
            if (innerData.hasNext()) return true;
            try {
                return nextNameSpace();
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
                if (nextNameSpace()){
                    return innerData.next();
                } else {
                    throw new NoSuchElementException("No more nameSpaces to iterate over");
                }
            } catch (IDMapperException ex) {
                Reporter.report(ex.getMessage());
                throw new NoSuchElementException("Error getting next nameSpace. " + ex.getMessage());
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}