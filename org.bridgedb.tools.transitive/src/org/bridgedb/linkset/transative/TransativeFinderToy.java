/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset.transative;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author Christian
 */
public class TransativeFinderToy {
    
    TreeSet<Link> links;
    int nextLink = 1;
    
    TransativeFinderToy (){
        links = new TreeSet<Link>();
        addLinks("A","B");
        addLinks("C","B");
        addLinks("C","D");
        addLinks("D","E");
        addLinks("A","E");
    }
    
    private void addLinks(String source, String target){
        addLink(source, target);
        addLink(target, source);
    }
    
    private void addLink(String source, String target){
        SimpleLink newLink = new SimpleLink(source, target, nextLink);
        nextLink++;
        newLink.findInverse(links);
        Set<Link> newLinks = new HashSet<Link>();
        for (Link link:links){
            Link transative = createIfLinks(link, newLink);
            if (transative != null){
                newLinks.add(transative);
            }
            transative = createIfLinks(newLink, link);
            if (transative != null){
                newLinks.add(transative);
            }
            for (Link right:links){
                transative = createIfLinks(link, newLink, right);
                if (transative != null){
                    newLinks.add(transative);
                }
            }
        }
        System.out.println();
        links.add(newLink);
        links.addAll(newLinks);
    }
    
    public static TransativeLink createIfLinks(Link left, Link right){
        System.out.println("left: " + left);
        System.out.println("right: " + right);
        if (!left.getTarget().equals(right.getSource())){
            System.out.println("no conenction");
            return null;//no connection
        }
        if (left.getSource().equals(right.getTarget())){
            System.out.println("circle");
            return null;  //circle
        } 
        ArrayList<String> chain = new ArrayList<String>();
        chain.add(left.getSource());
        for (SimpleLink simple:left.getChain()){
            chain.add(simple.getTarget());
        }
        for (SimpleLink simple:right.getChain()){
            if (chain.contains(simple.getTarget())){
                System.out.println ("repeated " + simple.getTarget());
                return null;
            }
            chain.add(simple.getTarget());
        }
        TransativeLink result =  new TransativeLink(left, right);
        System.out.println (result);
        return result;
    }
    
    public static TransativeLink createIfLinks(Link left, SimpleLink middle, Link right){
        System.out.println("left: " + left);
        System.out.println("middle: " + middle);
        System.out.println("right: " + right);
        if (!left.getTarget().equals(middle.getSource())){
            System.out.println("no conenction");
            return null;//no connection
        }
        if (!middle.getTarget().equals(right.getSource())){
            System.out.println("no conenction");
            return null;//no connection
        }
        if (left.getSource().equals(right.getTarget())){
            System.out.println("circle");
            return null;  //circle
        } 
        ArrayList<String> chain = new ArrayList<String>();
        chain.add(left.getSource());
        for (SimpleLink simple:left.getChain()){
            chain.add(simple.getTarget());
        }
        if (chain.contains(middle.getTarget())){
            return null;
        }
        chain.add(middle.getTarget());
        for (SimpleLink simple:right.getChain()){
            if (chain.contains(simple.getTarget())){
                System.out.println ("repeated " + simple.getTarget());
                return null;
            }
            chain.add(simple.getTarget());
        }
        TransativeLink result =  new TransativeLink(left, middle, right);
        System.out.println (result);
        return result;
    }

    private void showLinks(){
        for (Link link:links){
            System.out.println(link);
        }
        System.out.println(links.size());
    }
    
    public static void main(String[] args){
        TransativeFinderToy test = new TransativeFinderToy();
        test.showLinks();
    }
}
