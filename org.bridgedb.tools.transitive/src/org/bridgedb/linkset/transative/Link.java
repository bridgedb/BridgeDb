/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset.transative;

import java.util.List;
import java.util.Set;

/**
 *
 * @author Christian
 */
public interface Link extends Comparable<Link>{
    public void findInverse(Set<Link> others);

    public String getSource();
    
    public String getTarget();
    
    List<SimpleLink> getChain();
}
