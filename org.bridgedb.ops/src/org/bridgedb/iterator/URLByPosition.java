/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.iterator;

import java.util.Set;
import org.bridgedb.IDMapperException;

/**
 *
 * @author Christian
 */
public interface URLByPosition {
    
    public Set<String> getURLByPosition(int position, int limit) throws IDMapperException;

    public String getURLByPosition(int position) throws IDMapperException;
    
    public Set<String> getURLByPosition(String nameSpace, int position, int limit) throws IDMapperException;

    public String getURLByPosition(String namespace, int position) throws IDMapperException;

}
