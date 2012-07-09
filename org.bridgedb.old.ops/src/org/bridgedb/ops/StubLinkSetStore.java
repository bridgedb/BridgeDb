/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ops;

import java.util.ArrayList;
import java.util.List;
import org.bridgedb.IDMapperException;

/**
 *
 * @author Christian
 */
public class StubLinkSetStore implements LinkSetStore{

    @Override
    public List<String> getLinksetNames() throws IDMapperException {
        ArrayList result = new ArrayList<String>();
        result.add("http://localhost:8080/OPS-IMS/linkset/1");
        result.add("http://localhost:8080/OPS-IMS/linkset/2");
        result.add("http://localhost:8080/OPS-IMS/linkset/3");
        return result;
    }

    @Override
    public String getRDF(int id) throws IDMapperException {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?> "
        + "<rdf:RDF	xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">"
        + "     <rdf:Description rdf:about=\"http://localhost:8080/OPS-IMS/linkset/2/#TestDS1\">"
        + "     <rdf:type rdf:resource=\"http://rdfs.org/ns/void#Dataset\"/>"
        + "     <uriSpace xmlns=\"http://rdfs.org/ns/void#\" rdf:resource=\"http://www.foo.com/\"/>"
        + "</rdf:Description> "
        + "<rdf:Description rdf:about=\"http://localhost:8080/OPS-IMS/linkset/2/#TestDS3\">"
        + "     <rdf:type rdf:resource=\"http://rdfs.org/ns/void#Dataset\"/>"
        + "     <uriSpace xmlns=\"http://rdfs.org/ns/void#\" rdf:resource=\"http://www.example.org#\"/>"
        + "</rdf:Description>"
        + "     <rdf:Description rdf:about=\"http://localhost:8080/OPS-IMS/linkset/2/#Test1_3\">"
        + "     <rdf:type rdf:resource=\"http://rdfs.org/ns/void#Linkset\"/>"
        + " 	<subjectsTarget xmlns=\"http://rdfs.org/ns/void#\" rdf:resource=\"http://localhost:8080/OPS-IMS/linkset/2/#TestDS1\"/>"
        + "     <objectsTarget xmlns=\"http://rdfs.org/ns/void#\" rdf:resource=\"http://localhost:8080/OPS-IMS/linkset/2/#TestDS3\"/>"
        + "     <linkPredicate xmlns=\"http://rdfs.org/ns/void#\" rdf:resource=\"http://www.bridgedb.org/test#testPredicate\"/>"
        + " 	<created xmlns=\"http://purl.org/dc/terms/\" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#date\">2012-02-22</created>"
        + " 	<creator xmlns=\"http://purl.org/dc/terms/\" rdf:resource=\"http://www.cs.man.ac.uk/~brenninc\"/>"
        + "</rdf:Description>";
    }

    //@Override
    public List<Triple> getTriples(String graphId) throws IDMapperException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
