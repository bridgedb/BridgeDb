/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.io.FileNotFoundException;
import org.bridgedb.metadata.constants.SchemaConstants;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.bridgedb.metadata.utils.Reporter;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNaryBooleanClassExpression;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyRange;
import org.semanticweb.owlapi.model.OWLQuantifiedRestriction;
import org.semanticweb.owlapi.model.OWLRestriction;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 *
 * @author Christian
 */
public class MetaDataRegistry {
    private OWLOntology ontology;
    
    Map<URI, ResourceMetaData> resourcesByType;
   // Map<Resource, ResourceMetaData> resourcesById = new HashMap<Resource, ResourceMetaData>();
    static String documentationRoot = "";
    private static String THING_ID = "http://www.w3.org/2002/07/owl#Thing";
        
    public MetaDataRegistry(String location) throws MetaDataException{
        resourcesByType = new HashMap<URI, ResourceMetaData>();
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        IRI pav = IRI.create(location);
        try {
            ontology = m.loadOntologyFromOntologyDocument(pav);
        } catch (OWLOntologyCreationException ex) {
            Logger.getLogger(MetaDataRegistry.class.getName()).log(Level.SEVERE, null, ex);
        }
        Set<OWLClass> theClasses = ontology.getClassesInSignature();
        for (OWLClass theClass:theClasses){
            String id = theClass.toStringID();
             if (id.equals(THING_ID)){
                //ignore thing;
            } else {
                URI type = new URIImpl(id);
                List<MetaDataBase> childMetaData = getChildren(theClass);
                ResourceMetaData resourceMetaData = new ResourceMetaData(type, childMetaData);
                resourcesByType.put(type, resourceMetaData);
             }
        }
    }
    
    private URI toURI(OWLObject object) throws MetaDataException{
        OWLEntity entity;
        if (object instanceof OWLEntity){
            entity = (OWLEntity)object;
        } else {
            Set<OWLEntity> signature = object.getSignature();
            if (signature.size() != 1){
                throw new MetaDataException ("Object " + object + " has unexpected signature " + signature);
            }
            entity = signature.iterator().next();
        }
        String id = entity.toStringID();
        return new URIImpl(id);
    }
    
    public ResourceMetaData getResourceByType(Value type) throws MetaDataException{
        ResourceMetaData resourceMetaData = resourcesByType.get(type);
        if (resourceMetaData == null){
            return null;
        } else {
            return resourceMetaData.getSchemaClone();
        }
    }
   
    public static String getDocumentationRoot(){
        return documentationRoot;
    }
    
    private List<MetaDataBase> getChildren(OWLClass theClass) throws MetaDataException {
        ArrayList<MetaDataBase> children = new ArrayList<MetaDataBase>();
        Set<OWLClassExpression> exprs = theClass.getSuperClasses(ontology);
        for (OWLClassExpression expr:exprs){
            MetaDataBase child = parseExpression(expr);
            children.add(child);
        }
        return children;
    }

    private MetaDataBase parseExpression(OWLClassExpression expr) throws MetaDataException {
        if (expr instanceof OWLQuantifiedRestriction){
            return parseOWLQuantifiedRestriction ((OWLQuantifiedRestriction) expr);
        }
        if (expr instanceof OWLNaryBooleanClassExpression){
            return parseOWLNaryBooleanClassExpression ((OWLNaryBooleanClassExpression) expr);
        }
        throw new MetaDataException("Unexpected expression." + expr);
    }
        
    private MetaDataBase parseOWLNaryBooleanClassExpression(OWLNaryBooleanClassExpression expression) throws MetaDataException{
        ArrayList<MetaDataBase> children = new ArrayList<MetaDataBase>();
        Set<OWLClassExpression> operands = expression.getOperands();
        for (OWLClassExpression expr:operands){
            MetaDataBase child = parseExpression(expr);
            children.add(child);
        }
        if (expression instanceof OWLObjectIntersectionOf){
            String name = children.get(0).name;
            for (int i = 1; i < children.size(); i++){
                name = name + " and " + children.get(i).name;
            }
            return new MetaDataGroup(name, children);
        } 
        if (expression instanceof OWLObjectUnionOf){
            String name = children.get(0).name;
            for (int i = 1; i < children.size(); i++){
                name = name + " or " + children.get(i).name;
            }
            return new MetaDataAlternatives(name, children);
        } 
        throw new MetaDataException("Unexpected expression." + expression);
    }
    
    private PropertyMetaData parseOWLQuantifiedRestriction(OWLQuantifiedRestriction restriction) throws MetaDataException{
        URI predicate;
        OWLPropertyRange range = restriction.getFiller();
        OWLPropertyExpression owlPropertyExpression = restriction.getProperty();
        predicate = toURI(owlPropertyExpression);
        return new PropertyMetaData(predicate, range.toString());
    }


    public static void main( String[] args ) throws MetaDataException 
    {
        MetaDataRegistry test = new MetaDataRegistry("file:resources/shouldOwl.owl");
        for (ResourceMetaData resource:test.resourcesByType.values()){
            System.out.println(resource.Schema());
        }
    }
}
