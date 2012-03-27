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
public interface URLByPossition {
    
    public Set<String> getURLByPossition(int possition, int limit) throws IDMapperException;

    public String getURLByPossition(int possition) throws IDMapperException;
    
    public Set<String> getURLByPossition(String nameSpace, int possition, int limit) throws IDMapperException;

    public String getURLByPossition(String namespace, int possition) throws IDMapperException;

}
