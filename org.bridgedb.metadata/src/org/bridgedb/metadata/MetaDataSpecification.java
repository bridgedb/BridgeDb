package org.bridgedb.metadata;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bridgedb.IDMapperException;
import org.bridgedb.metadata.validator.ValidationType;
import org.bridgedb.utils.InputStreamFinder;
import org.bridgedb.utils.Reporter;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNaryBooleanClassExpression;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyRange;
import org.semanticweb.owlapi.model.OWLQuantifiedRestriction;
import uk.ac.manchester.cs.owl.owlapi.OWLAnnotationPropertyImpl;

/**
 *
 * @author Christian
 */
public class MetaDataSpecification {
    private static OWLAnnotationProperty REQUIREMENT_LEVEL_PROPERTY = new OWLAnnotationPropertyImpl(
            IRI.create("http://openphacts.cs.man.ac.uk:9090/Void/ontology.owl#RequirementLevel"));
    private OWLOntology ontology;
    private final ValidationType validationType;
    
    Map<URI, ResourceMetaData> resourcesByType = new HashMap<URI, ResourceMetaData>();
   // Map<Resource, ResourceMetaData> resourcesById = new HashMap<Resource, ResourceMetaData>();
    static String documentationRoot = "";
    private static String THING_ID = "http://www.w3.org/2002/07/owl#Thing";
        
    public MetaDataSpecification(ValidationType type) throws IDMapperException{
        InputStream stream = InputStreamFinder.findByName(type.getOwlFileName(), this);
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        this.validationType = type;
        try {
            ontology = m.loadOntologyFromOntologyDocument(stream);
        } catch (OWLOntologyCreationException ex) {
            throw new MetaDataException("Unable to read owl from inputStream", ex);
        }
        loadSpecification();
    }
    
    private void loadSpecification() throws MetaDataException{
        Set<OWLClass> theClasses = ontology.getClassesInSignature();
        for (OWLClass theClass:theClasses){
            String id = theClass.toStringID();
            String requirementLevelSt = getRequirementLevelString(theClass);
            if (id.equals(THING_ID)){
                //ignore thing;
            } else if (requirementLevelSt == null){
                URI type = new URIImpl(id);
                //ystem.out.println(theClass);
                List<MetaDataBase> childMetaData = getChildren(theClass, type.getLocalName(), RequirementLevel.MUST);
                ResourceMetaData resourceMetaData = new ResourceMetaData(type, childMetaData);
                resourcesByType.put(type, resourceMetaData);
            }
        }
        for (OWLClass theClass:theClasses){
            String id = theClass.toStringID();
            String requirementLevelSt = getRequirementLevelString(theClass);
            if (requirementLevelSt != null){
                URI type = getSuperType(theClass);
                RequirementLevel requirementLevel = getRequriementLevel(requirementLevelSt, type);
                List<MetaDataBase> childMetaData = getChildren(theClass, type.getLocalName(), requirementLevel);
                ResourceMetaData resourceMetaData = resourcesByType.get(type);
                resourceMetaData.addChildren(childMetaData);
             }
        }
    }
    
    private String getRequirementLevelString(OWLClass theClass) throws MetaDataException{
        Set<OWLAnnotation> annotations = theClass.getAnnotations(ontology, REQUIREMENT_LEVEL_PROPERTY);
        if (annotations.isEmpty()){
            return null;
        }
        if (annotations.size() != 1){
            throw new MetaDataException("Only expected one annotation with property " + REQUIREMENT_LEVEL_PROPERTY + 
                    "for theClass " + theClass + " but found " + annotations);
        }
        return annotations.iterator().next().getValue().toString();
    }
    
    private RequirementLevel getRequriementLevel(String requirementLevelSt, URI type) throws MetaDataException{
        RequirementLevel requirementLevel = RequirementLevel.parseString(requirementLevelSt);
        switch (requirementLevel){
            case MINIMAL: {
                return RequirementLevel.MUST;
            }
            case MUST:{
                if (validationType.isMinimal()) {
                    return RequirementLevel.SHOULD;
                } else{
                    return requirementLevel;
                }
            }
            case DIRECTMUST:{
                if (type.equals(validationType.getDirectType())){
                    return RequirementLevel.MUST;
                } else {
                    return RequirementLevel.SHOULD;
                }
            }
            default:{
                return requirementLevel;
            }
        }
    }
    
    private URI getSuperType(OWLClass theClass) throws MetaDataException {
        Set<OWLClassExpression> supers = theClass.getSuperClasses(ontology);
        for (OWLClassExpression theSuper:supers){
            if (theSuper instanceof OWLClass){
                OWLClass superClass = (OWLClass)theSuper;
                String id = superClass.toStringID();
                if (id.equals(THING_ID)){
                    throw new MetaDataException ("OWLClass " + theClass + " is a direct child of OWLThing.");
                }
                if (getRequirementLevelString(superClass) == null){
                    return new URIImpl(id);
                } else {
                    return getSuperType(superClass);
                }
            }
        }
        throw new MetaDataException("Unexpected end of loop without finding an OWLClass superclass for " + theClass);
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
            Reporter.report("Unable to find specifications for type: " + type);
            return null;
        } else {
            return resourceMetaData.getSchemaClone();
        }
    }
   
    public static String getDocumentationRoot(){
        return documentationRoot;
    }
    
    private List<MetaDataBase> getChildren(OWLClass theClass, String type, RequirementLevel requirementLevel) throws MetaDataException {
        ArrayList<MetaDataBase> children = new ArrayList<MetaDataBase>();
        Set<OWLClassExpression> exprs = theClass.getSuperClasses(ontology);
        for (OWLClassExpression expr:exprs){
            if (!(expr instanceof OWLClass)){
                MetaDataBase child = parseExpression(expr, type, requirementLevel);
                children.add(child);
            }
        }
        return children;
    }

    private MetaDataBase parseExpression(OWLClassExpression expr, String type, RequirementLevel requirementLevel) throws MetaDataException {
        if (expr instanceof OWLQuantifiedRestriction){
            return parseOWLQuantifiedRestriction ((OWLQuantifiedRestriction) expr, type, requirementLevel);
        }
        if (expr instanceof OWLNaryBooleanClassExpression){
            return parseOWLNaryBooleanClassExpression ((OWLNaryBooleanClassExpression) expr, type, requirementLevel);
        }
        throw new MetaDataException("Unexpected expression." + expr + " " + expr.getClass());
    }
        
    private MetaDataBase parseOWLNaryBooleanClassExpression(OWLNaryBooleanClassExpression expression, String type, 
            RequirementLevel requirementLevel) throws MetaDataException{
        ArrayList<MetaDataBase> children = new ArrayList<MetaDataBase>();
        Set<OWLClassExpression> operands = expression.getOperands();
        for (OWLClassExpression expr:operands){
            MetaDataBase child = parseExpression(expr, type, requirementLevel);
            children.add(child);
        }
        if (expression instanceof OWLObjectIntersectionOf){
            String name = children.get(0).name;
            for (int i = 1; i < children.size(); i++){
                name = name + " and " + children.get(i).name;
            }
            return new MetaDataGroup(name, type, requirementLevel, children);
        } 
        if (expression instanceof OWLObjectUnionOf){
            String name = children.get(0).name;
            for (int i = 1; i < children.size(); i++){
                name = name + " or " + children.get(i).name;
            }
            return new MetaDataAlternatives(name, type, requirementLevel, children);
        } 
        throw new MetaDataException("Unexpected expression." + expression);
    }
    
    private MetaDataBase parseOWLQuantifiedRestriction(OWLQuantifiedRestriction restriction, String type, 
            RequirementLevel requirementLevel) throws MetaDataException{
        URI predicate;
        OWLPropertyRange range = restriction.getFiller();
        OWLPropertyExpression owlPropertyExpression = restriction.getProperty();
        predicate = toURI(owlPropertyExpression);
        if (range instanceof OWLClass){
            OWLClass owlClass = (OWLClass)range;
            if (owlClass.isOWLThing()){
                return new PropertyMetaData(predicate, type, requirementLevel, range.toString());
            }
            IRI iri = owlClass.getIRI();
            ontology.containsClassInSignature(iri);
            return new LinkedResource(predicate, type, requirementLevel, new URIImpl(iri.toString()), this);
        }
        return new PropertyMetaData(predicate, type, requirementLevel, range.toString());
    }

}
