/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset.transative;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Christian
 */
public class SimpleLink implements Link{
    String source;
    String target;
    int linkset;
    SimpleLink inverse;
    boolean primary = true;
            
    public SimpleLink(String source, String target, int linkset){
        this.source = source;
        this.target = target;
        this.linkset = linkset;
    }

    @Override
    public void findInverse(Set<Link> others){
        for (Link link:others){
            if (link instanceof SimpleLink){
                SimpleLink simpleLink = (SimpleLink)link;
                if (isInverse(simpleLink)){
                    setInverse(simpleLink);
                    simpleLink.setInverse(this);
                }
            }
        }
    }
        
    public boolean isInverse(SimpleLink inverse){
        if (this.source.equals(inverse.target)){
            if (this.target.equals(inverse.source)){
                return true;
            }
        }
        return false;
    }
    
    private void setInverse(SimpleLink other){
        inverse = other;       
        primary = (this.compareTo(other) < 0);
    }
    
    public boolean equals(Object other){
        if (other instanceof SimpleLink){
            SimpleLink otherLink = (SimpleLink)other;
            if (this.source.equals(otherLink.source)){
                if (this.target.equals(otherLink.target)){
                    return this.linkset == otherLink.linkset;
                }
            }
        }
        return false;
    }
    
    @Override
    public int compareTo(Link other) {
        int result = this.source.compareTo(other.getSource());
        if (result != 0){ 
            return result; 
        }
        result = this.target.compareTo(other.getTarget());
        if (result != 0){ 
            return result; 
        }
        if (other instanceof SimpleLink){
            SimpleLink simpleOther = (SimpleLink) other;
            if (linkset < simpleOther.linkset){
                return -1;
            }
            if (linkset > simpleOther.linkset){
                return 1;
            }
            return 0;
        }
        return 1;
    }
    
    public String toString(){
        String result = source + " -> " + target + " (" + linkset + ")";
        if (!primary){
            result+= " inverse of " + inverse; 
        }
        return result;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public String getTarget() {
        return target;
    }

    @Override
    public List<SimpleLink> getChain() {
        ArrayList<SimpleLink> result = new ArrayList<SimpleLink>();
        result.add(this);
        return result;
    }

}
