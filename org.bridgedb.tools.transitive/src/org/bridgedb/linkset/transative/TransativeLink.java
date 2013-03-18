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
public class TransativeLink implements Link{
    
    String source;
    String target;
    List<SimpleLink> chain;
    TransativeLink inverse;
    boolean primary = true;
    
    public TransativeLink (Link left, Link right){
        source = left.getSource();
        target = right.getTarget();
        chain = new ArrayList<SimpleLink>(left.getChain());
        chain.addAll(right.getChain());
    }

    public TransativeLink (Link left, Link middle, Link right){
        source = left.getSource();
        target = right.getTarget();
        chain = new ArrayList<SimpleLink>(left.getChain());
        chain.addAll(middle.getChain());
        chain.addAll(right.getChain());
    }
    
    @Override
    public void findInverse(Set<Link> others) {
        for (Link link:others){
            if (link instanceof TransativeLink){
                TransativeLink inverse = (TransativeLink)link;
                if (isInverse(inverse)){
                    setInverse(inverse);
                    inverse.setInverse(this);
                }
            }
        }
    }

    private void setInverse(TransativeLink other){
        inverse = other;       
        primary = (this.compareTo(other) < 0);
    }
    
    public boolean isInverse (TransativeLink inverse){
        if (!this.source.equals(inverse.target)){
            return false;
        }
        if (!this.target.equals(inverse.source)){
            return false;
        }
        if (this.getChain().size() != inverse.chain.size()){
            return false;
        }
        for (int i = 0; i < chain.size(); i++){
            int iInverse = chain.size() - i ;
            if (!chain.get(i).isInverse(inverse.chain.get(iInverse))){
                return false;
            }
        }
        return true;
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
        return this.chain;
    }
    
    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder(source);
        for (SimpleLink link:chain){
            builder.append(" -> ");
            builder.append(link.getTarget());
        }
        builder.append (" (");
        builder.append (chain.get(0).linkset);
        for (int i = 1; i< chain.size(); i++){
            builder.append (", ");
            builder.append (chain.get(i).linkset);
        }
        builder.append (")");
        if (!primary){
            builder.append(" inverse of ");
            builder.append(inverse); 
        }
        return builder.toString();
    }

    private int compareChain(int index, TransativeLink other){
        if (this.chain.size() <= index){
            if (other.chain.size() <= index){
                return 0;
            } else {
                return -1;
            }
        }
        if (other.chain.size() <= index){
            return 1;
        }
        SimpleLink linkThis = this.chain.get(index);
        SimpleLink linkOther = other.chain.get(index);
        int result = linkThis.getTarget().compareTo(linkOther.getTarget());
        if (result != 0){
            return result;
        }
        return compareChain(index + 1, other);
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
            return -1;
        }
        //if (other instanceof TransativeLink){
            TransativeLink transativeLink = (TransativeLink)other;
            return compareChain(0, transativeLink);
        //}
    }
}
