// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009  BridgeDb developers
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  OpenPhacts
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.bridgedb.tools.metadata.owlapi;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import org.apache.log4j.Logger;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentTarget;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.model.SWRLClassAtom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.SWRLVariable;
import org.semanticweb.owlapi.util.AutoIRIMapper;
import org.semanticweb.owlapi.util.OWLEntityRemover;
import org.semanticweb.owlapi.util.OWLOntologyWalker;
import org.semanticweb.owlapi.util.OWLOntologyWalkerVisitor;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

/**
 * Hello world!
 *
 */
public class OwlFileTester 
{
    public static final IRI pizza_iri = IRI
            .create("http://www.co-ode.org/ontologies/pizza/pizza.owl");
    public static final IRI example_iri = IRI
            .create("http://www.semanticweb.org/ontologies/ont.owl");
    public static final IRI example_save_iri = IRI
            .create("file:materializedOntologies/ont1290535967123.owl");
    private static OWLDataFactory df = OWLManager.getOWLDataFactory();

    static final Logger logger = Logger.getLogger(OwlFileTester.class);

    public static OWLOntologyManager create() {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        m.addIRIMapper(new AutoIRIMapper(new File("materializedOntologies"), true));
        return m;
    }

    public OWLOntology loadOntology(OWLOntologyManager m ) throws OWLOntologyCreationException{
        OWLOntology o = m.loadOntologyFromOntologyDocument(pizza_iri);
        return o;
    }
    
    private static void slide14() throws OWLOntologyCreationException, OWLOntologyStorageException, IOException{
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        // map the ontology IRI to a physical IRI (files for example)
        File output = File.createTempFile("saved_pizza", ".owl");
        logger.info(output.getAbsoluteFile());
        IRI documentIRI = IRI.create(output);
        // Set up a mapping, which maps the ontology to the document IRI
        SimpleIRIMapper mapper = new SimpleIRIMapper(example_save_iri, documentIRI);
        m.addIRIMapper(mapper);
        // set up a mapper to read local copies of ontologies
        File localFolder = new File("materializedOntologies");
        // the manager will look up an ontology IRI by checking
        // localFolder first for a local copy
        m.addIRIMapper(new AutoIRIMapper(localFolder, true));
        // Now create the ontology using the ontology IRI (not the physical URI)
        OWLOntology o = m.createOntology(example_save_iri);
        // save the ontology to its physical location - documentIRI
        m.saveOntology(o);
    }      
    
    private static void slide15() throws OWLOntologyCreationException, OWLOntologyStorageException, IOException{
        OWLOntologyManager m = create();
        OWLOntology o = m.createOntology(pizza_iri);
        // class A and class B
        OWLClass clsA = df.getOWLClass(IRI.create(pizza_iri + "#A"));
        OWLClass clsB = df.getOWLClass(IRI.create(pizza_iri + "#B"));
        // Now create the axiom
        OWLAxiom axiom = df.getOWLSubClassOfAxiom(clsA, clsB);
        // add the axiom to the ontology.
        AddAxiom addAxiom = new AddAxiom(o, axiom);
        // We now use the manager to apply the change
        m.applyChange(addAxiom);
        print(m, o);
        // remove the axiom from the ontology
        RemoveAxiom removeAxiom = new RemoveAxiom(o,axiom);
        m.applyChange(removeAxiom);
        print(m, o);
    }    
    
    private static void slide16() throws OWLOntologyCreationException, OWLOntologyStorageException, IOException{
        OWLOntologyManager m = create();
        OWLOntology o = m.createOntology(example_iri);
        // Get hold of references to class A and class B.
        OWLClass clsA = df.getOWLClass(
        IRI.create(example_iri + "#A"));
        OWLClass clsB = df.getOWLClass(
        IRI.create(example_iri + "#B"));
        SWRLVariable var = df.getSWRLVariable(
        IRI.create(example_iri + "#x"));
        SWRLClassAtom body = df.getSWRLClassAtom(clsA, var);
        SWRLClassAtom head = df.getSWRLClassAtom(clsB, var);
        SWRLRule rule = df.getSWRLRule(Collections.singleton(body),
        Collections.singleton(head));
        m.applyChange(new AddAxiom(o, rule));  
        print(m,o);
    }
    
    private static void slide18() throws OWLOntologyCreationException, OWLOntologyStorageException, IOException{
        // Delete individuals representing countries
        OWLOntologyManager m = create();
        OWLOntology o = m.loadOntologyFromOntologyDocument(pizza_iri);
        // Ontologies do not directly contain entities but axioms
        // OWLEntityRemover will remove an entity
        // from a set of ontologies by removing all referencing axioms
        OWLEntityRemover remover = new OWLEntityRemover(m, Collections.singleton(o));
        logger.info(o.getIndividualsInSignature());
        // Visit all individuals with the remover
        // Changes needed for removal will be prepared
        for (OWLNamedIndividual ind : o.getIndividualsInSignature()){
            ind.accept(remover);
        }
        m.applyChanges(remover.getChanges());
        logger.info(o.getIndividualsInSignature().size());        
    }
    
    private static void slide25() throws OWLOntologyCreationException, OWLOntologyStorageException, IOException{
        OWLOntologyManager m = create();
        OWLOntology o = m.loadOntologyFromOntologyDocument(pizza_iri);
        // Named classes referenced by axioms in the ontology.
        for (OWLClass cls : o.getClassesInSignature()){
            logger.info(cls);
        }
    }
    
    private static void slide26() throws OWLOntologyCreationException, OWLOntologyStorageException, IOException{
        // How to walk the asserted structure of an ontology
        OWLOntologyManager m = create();
        OWLOntology o = m.loadOntologyFromOntologyDocument(pizza_iri);
         // Create the walker
        OWLOntologyWalker walker = new OWLOntologyWalker(Collections.singleton(o));
        // Now ask our walker to walk over the ontology
        OWLOntologyWalkerVisitor<Object> visitor = new OWLOntologyWalkerVisitor<Object>(walker) {
            @Override
            public Object visit(OWLObjectSomeValuesFrom desc) {
                logger.info(desc);
                logger.info(" " + getCurrentAxiom());
                return null;
            }
        };
        // Have the walker walk...
        walker.walkStructure(visitor);
    }
    
    private static void print(OWLOntologyManager m, OWLOntology o) throws OWLOntologyStorageException{
       StringDocumentTarget target = new StringDocumentTarget();
       m.saveOntology(o, target);
       logger.info(o);
       logger.info(target); 
       logger.info("*******************************************************");
    }
    
    private static void testOwl(String location) throws OWLOntologyCreationException{
        OWLOntologyManager m = create();
        IRI pav = IRI.create(location);
        OWLOntology o = m.loadOntologyFromOntologyDocument(pav);
        logger.info(o);
        Set<OWLAxiom> all = o.getAxioms();
        //for (OWLAxiom axiom:all){
        //    logger.info.println(axiom);
        //}
        Set<OWLObjectProperty> properties = o.getObjectPropertiesInSignature();
        for (OWLObjectProperty property:properties){
            logger.info(property);
            Set<OWLAxiom>  axioms = o.getReferencingAxioms(property);
            boolean rangeFound = false;
            for (OWLAxiom axiom:axioms){
                if (axiom instanceof OWLObjectPropertyDomainAxiom){
                    OWLObjectPropertyDomainAxiom domain = (OWLObjectPropertyDomainAxiom)axiom;
                    logger.info("    domain " + domain.getDomain());
                }
                if (axiom instanceof OWLObjectPropertyRangeAxiom){
                    OWLObjectPropertyRangeAxiom range = (OWLObjectPropertyRangeAxiom)axiom;
                    logger.info("    range " + range.getRange());
                    rangeFound = true;
                }
            }
            if (!rangeFound){
                logger.info("    range URI");
            }
        }
        Set<OWLDataProperty> dataProps = o.getDataPropertiesInSignature();
        for (OWLDataProperty dataProp: dataProps){
            logger.info(dataProp);
            Set<OWLAxiom>  axioms = o.getReferencingAxioms(dataProp);
            boolean rangeFound = false;
            for (OWLAxiom axiom:axioms){
                logger.info("    " + axiom);
            }
        }
    }
        
    private static URI getType(OWLOntology o, OWLClass theClass){
        Set<OWLClassExpression> exprs = theClass.getSuperClasses(o);
        for (OWLClassExpression expr:exprs){
            if (!expr.isAnonymous()){
                OWLClass parent = expr.asOWLClass();
                return getType(o, parent);
            }
        }
        return new URIImpl(theClass.toStringID());
    }
    
//    private static Set
            
    private static void readOwl(String location) throws OWLOntologyCreationException{
        OWLOntologyManager m = create();
        IRI pav = IRI.create(location);
        OWLOntology o = m.loadOntologyFromOntologyDocument(pav);
        logger.info(o);
        Set<OWLAxiom> all = o.getAxioms();
        for (OWLAxiom axiom:all){
            logger.info(axiom);
            logger.info("   " + axiom.getClass());
            
        }
        
        Set<OWLClass> theClasses = o.getClassesInSignature();
        for (OWLClass theClass:theClasses){
            logger.info(theClass);
            Set<OWLClassExpression> exprs = theClass.getSuperClasses(o);
            for (OWLClassExpression expr:exprs){
                logger.info("  " + expr);
                logger.info("     " + expr.getClass());
            }
            logger.info(getType(o, theClass));
        }        
    }

    public static void main( String[] args ) 
            throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, BridgeDBException
    {
        ConfigReader.logToConsole();
        //@prefix dcterms: <http://purl.org/dc/terms/> .
        //testOwl("http://purl.org/dc/terms/");
        //testOwl("http://bloody-byte.net/rdf/dc_owl2dl/dcterms_od.rdf");
//        testOwl("file:resources/dcterms_od.rdf");

        //@prefix dul: <http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#> .
        //testOwl("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#");
        //testOwl("file:resources/DUL.owl");
        
        //@prefix foaf: <http://xmlns.com/foaf/0.1/> .
        //testOwl("http://xmlns.com/foaf/0.1/");
        //testOwl("http://xmlns.com/foaf/spec/index.rdf");
//        testOwl("file:resources/foaf.rdf");
        
        //@prefix freq: <http://purl.org/cld/freq/> .
//        testOwl("http://purl.org/cld/freq/");
 //       testOwl("http://dublincore.org/groups/collections/frequency/2007-03-09/freq.rdf");
//        testOwl("file:resources/freq.rdf");
        
        //@prefix owl: <http://www.w3.org/2002/07/owl#> .
        //testOwl("http://www.w3.org/2002/07/owl#");
//        testOwl("file:resources/owl.rdf");
        
        //@prefix pav: <http://purl.org/pav/2.0/> .
 //       testOwl("http://purl.org/pav/2.0/");
        //testOwl("http://pav-ontology.googlecode.com/svn/trunk/pav.owl");
//        testOwl("file:resources/pav.owl");

        //@prefix prov: <http://www.w3.org/ns/prov-o/> . 
        //FAILS testOwl("http://www.w3.org/ns/prov-o/");
        //Unused and not working
        
        //@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
        //testOwl("http://www.w3.org/1999/02/22-rdf-syntax-ns#");
//        testOwl("file:resources/22-rdf-syntax-ns.rdf");

        //@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
        //testOwl("http://www.w3.org/2000/01/rdf-schema#");
//        testOwl("file:resources/rdf-schema.rdf");

        //@prefix skos: <http://www.w3.org/2004/02/skos/core#> .
        //testOwl("http://www.w3.org/2004/02/skos/core#");
        //testOwl("http://www.w3.org/2009/08/skos-reference/skos.rdf#");
//        testOwl("file:resources/skos.rdf");
        
        //@prefix voag: <http://voag.linkedmodel.org/schema/voag#> .
        //testOwl("http://voag.linkedmodel.org/schema/voag#");
//        testOwl("file:resources/voag.rdf");
        
        // .
        //testOwl("http://rdfs.org/ns/void#");
//        testOwl("file:resources/void.rdf");
        
        //@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
       // testOwl("http://www.w3.org/2001/XMLSchema#"); //works but empty
        //testOwl("http://www.w3.org/TR/xmlschema-1/"); //works but empty
        
//        slide26();
        
        readOwl("file:resources/shouldOwl.owl");
    }
 /*
     * 
     */
}
