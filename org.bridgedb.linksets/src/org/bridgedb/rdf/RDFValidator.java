package org.bridgedb.rdf;

import java.util.ArrayList;
import org.bridgedb.IDMapperException;
import org.bridgedb.linkset.constants.DctermsConstants;
import org.bridgedb.linkset.constants.PavConstants;
import org.bridgedb.linkset.constants.RdfConstants;
import org.bridgedb.linkset.constants.VoidConstants;
import org.bridgedb.utils.Reporter;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.rio.RDFHandlerException;

/**
 *
 * @author Christian
 */
public class RDFValidator implements RdfLoader{

    ArrayList<Statement> statements = new ArrayList<Statement>();
    private String subjectURISpace;
    private String targetURISpace;
    private Resource linksetResource;
    Value linksetPredicate;
    private boolean isTransative;
    private boolean strict;
    private final Value UNSPECIFIED = new LiteralImpl("Unspecified");
   
    public RDFValidator(boolean strict) {
        this.strict = strict;
    }
        
    @Override
    public void addHeaderStatement(Statement st) throws RDFHandlerException {
        if (statements == null){
            throw new RDFHandlerException ("Illegal call to addStatement after validateAndSaveVoid() called.");
        }
        statements.add(st);
    }
    
    @Override
    public void processFirstNoneHeader(Statement firstMap) throws RDFHandlerException{
        Reporter.report("Validation started");
        linksetResource = findTheSingletonSubject(RdfConstants.TYPE_URI, VoidConstants.LINKSET);
        linksetPredicate = findTheSingletonObject (linksetResource, VoidConstants.LINK_PREDICATE);
        //Provide details of the licence under which the dataset is published using the dcterms:license property.
        checkObject(linksetResource, DctermsConstants.LICENSE);
        //The linkset authorship, i.e. the agent that generated the intellectual knowledge
        validateLinksetProvenance();
        subjectURISpace = validateDataSetAndExtractUriSpace(firstMap.getSubject(), VoidConstants.SUBJECTSTARGET);
        targetURISpace = validateDataSetAndExtractUriSpace(firstMap.getObject(), VoidConstants.OBJECTSTARGET);
        isTransative = checkIsTransative();
     }
    
    private void validateLinksetProvenance() throws RDFHandlerException {
        Value by = findPossibleObject(linksetResource, PavConstants.AUTHORED_BY);
        if (by != null){
            findTheSingletonObject(linksetResource, PavConstants.AUTHORED_ON);
            return;
        }
        by = findPossibleObject(linksetResource, PavConstants.CREATED_BY);
        if (by != null){
            findTheSingletonObject(linksetResource, PavConstants.CREATED_ON);
            return;
        }
        by = findPossibleObject(linksetResource, PavConstants.DERIVED_BY);
        if (by != null){
            findTheSingletonObject(linksetResource, PavConstants.DERIVED_ON);
            return;
        }
        by = findPossibleObject(linksetResource, DctermsConstants.CREATOR);
        if (by != null){
            findTheSingletonObject(linksetResource, DctermsConstants.CREATED);
            return;
        }
        if (strict){
            throw new RDFHandlerException(linksetResource + " must have " + PavConstants.AUTHORED_BY + ", " + 
                    PavConstants.DERIVED_BY + ", " + PavConstants.CREATED_BY + " or " + DctermsConstants.CREATOR);
        }
        Statement licenseStatement = new StatementImpl(linksetResource, PavConstants.CREATED_BY, UNSPECIFIED);
        statements.add(licenseStatement);
    }


    /**
     * Based on http://www.cs.man.ac.uk/~graya/ops/mappingspec/
     * 
     * @param fullURI
     * @param targetPredicate
     * @return
     * @throws RDFHandlerException 
     */
    private String validateDataSetAndExtractUriSpace(Value fullURI, URI targetPredicate) throws RDFHandlerException{
        Value dataSetId = findPossibleSingletonObject(targetPredicate);
        if (dataSetId == null){
            dataSetId = findAndRegisterDataSetIdBasedOnUriSpace(fullURI, targetPredicate);
        }
        //Declare that we have a dataset using void:dataset.
        checkStatementExists(dataSetId, RdfConstants.TYPE_URI, VoidConstants.DATASET);
        //Provide details of the licence under which the dataset is published using the dcterms:license property.
        checkObject(dataSetId, DctermsConstants.LICENSE);
        //There must be a version or a Date
        checkHasVersionOrDate(dataSetId);
        //The type of the resource being linked is declared with the dcterm:subject predicate.
        checkObject(dataSetId, DctermsConstants.SUBJECT);      
        //The URI namespace for the resources being linked is declared using the void:uriSpace property
        Value uriValue = findTheSingletonObject(dataSetId, VoidConstants.URI_SPACE);
        String uriSpace = uriValue.stringValue();
        if (fullURI.stringValue().startsWith(uriSpace)){
            return uriSpace;
        }
        throw new RDFHandlerException("Declared URISpace " + uriSpace + " and uri in first link " + fullURI + " do not match");
    }

    private Value findAndRegisterDataSetIdBasedOnUriSpace(Value fullURI, URI targetPredicate) throws RDFHandlerException {
        for (Statement st:statements){
            if (st.getPredicate().equals(VoidConstants.URI_SPACE)){
                Value uriValue = st.getObject();
                String uriSpace = uriValue.stringValue();
                if (fullURI.stringValue().startsWith(uriSpace)){
                    Resource dataSetId =  st.getSubject();
                    checkStatementExists(linksetResource, VoidConstants.TARGET, dataSetId);
                    Statement newStatement = new StatementImpl(dataSetId, targetPredicate, dataSetId);
                    statements.add(newStatement);
                    return dataSetId;
                }
            }
        }
        throw new RDFHandlerException ("Unable to find a " + VoidConstants.TARGET + " with " 
                + VoidConstants.URI_SPACE + " that covers " + fullURI);
    }

   private void checkHasVersionOrDate(Value dataSetId) throws RDFHandlerException {
        Value license = findPossibleSingletonObject (dataSetId, PavConstants.VERSION);
        if (license != null) return;
        Value date = findPossibleSingletonObject (dataSetId, PavConstants.CREATED_ON);
        if (date != null) return;
        date = findPossibleSingletonObject (dataSetId, PavConstants.DERIVED_ON);
        if (date != null) return;
        date = findPossibleSingletonObject (dataSetId, PavConstants.IMPORTED_ON);
        if (date != null) return;
        date = findPossibleSingletonObject (dataSetId, PavConstants.MODIFIED_ON);
        if (date != null) return;
        date = findPossibleSingletonObject (dataSetId, PavConstants.RETRIEVED_ON);
        if (date != null) return;
        if (strict || !(dataSetId instanceof Resource)) {
            throw new RDFHandlerException ("Could not find a Version for DataSet " + dataSetId + 
                    " Please include at least one of " + PavConstants.VERSION + ", " + PavConstants.CREATED_ON + ", " 
                    + PavConstants.DERIVED_ON + ", " + PavConstants.IMPORTED_ON + ", " + 
                    PavConstants.MODIFIED_ON + ", " + PavConstants.RETRIEVED_ON);
        }
        Resource subjectR = (Resource)dataSetId;
        Statement versionStatement = new StatementImpl(subjectR, PavConstants.VERSION, UNSPECIFIED);
        statements.add(versionStatement);
   }
    
    public boolean isTransative() throws RDFHandlerException {
        return isTransative;
    }

    private boolean checkIsTransative() throws RDFHandlerException {
        Value derivedFrom = findPossibleObject(linksetResource, PavConstants.DERIVED_FROM);
        if (derivedFrom == null) return false;
        Value derivedOn = findPossibleObject(linksetResource, PavConstants.DERIVED_ON);
        Value derivedBy = findPossibleObject(linksetResource, PavConstants.DERIVED_BY);
        return true;
    }
   
    private Resource findPossibleSingletonSubject (URI predicate) throws RDFHandlerException{
        Resource subject = null;
        for (Statement st:statements){
            if (st.getPredicate().equals(predicate)){
                if (subject != null){
                    throw new RDFHandlerException ("Found more than one statement with predicate " + predicate);
                }
                subject = st.getSubject();
            }
        }
        return subject;
    }
    
   private Resource findPossibleSingletonSubject (URI predicate, Value object) throws RDFHandlerException{
        Resource subject = null;
        for (Statement st:statements){
            if (st.getPredicate().equals(predicate) && st.getObject().equals(object)){
                if (subject != null){
                    throw new RDFHandlerException ("Found more than one statement with predicate " + predicate + 
                            " and object " + object);
                }
                subject = st.getSubject();
            }
        }
        return subject;
    }

    private Value findPossibleSingletonObject (URI predicate) throws RDFHandlerException{
        Value object = null;
        for (Statement st:statements){
            if (st.getPredicate().equals(predicate)){
                if (object != null){
                    throw new RDFHandlerException ("Found more than one statement with predicate " + predicate);
                }
                object = st.getObject();
            }
        }
        return object;
    }
    
    private Value findPossibleSingletonObject (Value subject, URI predicate) throws RDFHandlerException{
        Value object = null;
        for (Statement st:statements){
            if (st.getSubject().equals(subject) && st.getPredicate().equals(predicate)){
                if (object != null){
                    throw new RDFHandlerException ("Found more than one statement with subject " + subject + 
                            " and predicate " + predicate);
                }
                object = st.getObject();
            }
        }
        return object;
    }

    private Value findPossibleObject (Value subject, URI predicate) throws RDFHandlerException{
        Value object = null;
        for (Statement st:statements){
            if (st.getSubject().equals(subject) && st.getPredicate().equals(predicate)){
                return object = st.getObject();
            }
        }
        return object;
    }

    private Value findTheSingletonObject (Value subject, URI predicate) throws RDFHandlerException{
        Value object = findPossibleSingletonObject(subject, predicate);
        if (object == null){
            throw new RDFHandlerException ("Found no statement with subject " + subject +  " and predicate " + predicate);
        }
        return object;
    }

    private void checkObject (Value subject, URI predicate) throws RDFHandlerException{
        for (Statement st:statements){
            if (st.getSubject().equals(subject) && st.getPredicate().equals(predicate)){
                return ;
            }
        }
        if (strict || (!(subject instanceof Resource))){
            throw new RDFHandlerException (subject + " does not have a " + predicate);
        } else {
            Resource subjectR = (Resource)subject;
            Statement newStatement = new StatementImpl(subjectR, predicate, UNSPECIFIED);
            statements.add(newStatement);
        }
   }

   private void checkStatementExists (Value subject, URI predicate, Value object) throws RDFHandlerException{
         for (Statement st:statements){
            if (st.getSubject().equals(subject) && st.getPredicate().equals(predicate) && st.getObject().equals(object)){
                return;
            }
        }
        throw new RDFHandlerException ("Found no statement with subject " + subject +  " predicate " + predicate + 
                " and object " + object);
    }

    private Resource findTheSingletonSubject (URI predicate, Value object) throws RDFHandlerException{
        Resource subject = null;
        for (Statement st:statements){
            if (st.getObject().equals(object) && st.getPredicate().equals(predicate)){
                if (subject != null){
                    throw new RDFHandlerException ("Found more than one statement with predicate " + predicate + 
                            " and Object " + object);
                }
                subject = st.getSubject();
            }
        }
        if (subject == null){
            throw new RDFHandlerException ("Found no statement with predicate " + predicate + 
                            " and Object " + object);
        }
        return subject;
    }

    public String getSubjectUriSpace() throws RDFHandlerException {
        if (subjectURISpace != null){
            return this.subjectURISpace;
        }
        throw new RDFHandlerException("run validateAndSaveVoid before calling getSubjectUriSpace()");
     }

    public String getTargetUriSpace() throws RDFHandlerException {
        if (targetURISpace != null){
            return this.targetURISpace;
        }
        throw new RDFHandlerException("run validateAndSaveVoid before calling getTargetUriSpace()");
    }

    String getPredicate() {
        return linksetPredicate.stringValue();
    }

    boolean isSymmetric() {
        //current assumption is all linksets are semaantic
        return true;
    }

    @Override
    public void insertURLMapping(Statement st) throws RDFHandlerException{
        String sourceURL = st.getSubject().stringValue();
        if (!sourceURL.startsWith(subjectURISpace)){
            throw new RDFHandlerException("Subject " + sourceURL + " does not begin with the declared uriSpace " 
                    + subjectURISpace);
        }
        Value predicate = st.getPredicate();
        if (!predicate.equals(linksetPredicate)){
            throw new RDFHandlerException("Predicate " + predicate + " does not begin with the declared predicate " 
                    + linksetPredicate);            
        }
        String targetURL = st.getObject().stringValue();
        if (!targetURL.startsWith(targetURISpace)){
            throw new RDFHandlerException("Target " + targetURL + " does not begin with the declared uriSpace " 
                    + targetURISpace);
        }
    }

    @Override
    public void closeInput() throws IDMapperException {
        //do nothing
    }
    

 }
