// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright      2012  Christian Y. A. Brenninkmeijer
// Copyright      2012  OpenPhacts
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
package org.bridgedb.rdf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bridgedb.IDMapperException;
import org.bridgedb.linkset.constants.BridgeDBConstants;
import org.bridgedb.linkset.constants.DctermsConstants;
import org.bridgedb.linkset.constants.DulConstants;
import org.bridgedb.linkset.constants.FoafConstants;
import org.bridgedb.linkset.constants.OwlConstants;
import org.bridgedb.linkset.constants.PavConstants;
import org.bridgedb.linkset.constants.RdfConstants;
import org.bridgedb.linkset.constants.SkosConstants;
import org.bridgedb.linkset.constants.VoidConstants;
import org.bridgedb.utils.Reporter;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFHandlerException;

/**
 *
 * @author Christian
 */
public class RDFValidator implements RdfLoader{

    List<Statement> statements = new ArrayList<Statement>();
    private String subjectURISpace;
    private String targetURISpace;
    private Resource linksetResource;
    Value linksetPredicate;
    Value linksetJustification;
    private boolean isSymmetric;
	private boolean isTransative;
    private boolean strict;
    private final Value UNSPECIFIED = new LiteralImpl("Unspecified");
    private final Resource UNDEFINED = new URIImpl("http://www.example.org/UNDEFINED");
    private boolean headerError = false;
    private boolean linkError = false;
	private boolean processingLinks = false;
	public RDFValidator(boolean strict) {
        this.strict = strict;
    }

	private Set<URI> symmetricPredicates = new HashSet<URI>(Arrays.asList( new URI[] {
			OwlConstants.EQUIVALENT_CLASS,
			OwlConstants.SAME_AS,
			SkosConstants.EXACT_MATCH,
			SkosConstants.CLOSE_MATCH,
			SkosConstants.RELATED_MATCH,
			BridgeDBConstants.TEST_PREDICATE
		}));
	
    @Override
    public void addHeaderStatement(Statement st) throws RDFHandlerException {
        if (statements == null) {
            throw new RDFHandlerException ("Illegal call to addStatement after validateAndSaveVoid() called.");
        }
        statements.add(st);
    }
    
    @Override
    public void processFirstNoneHeader(Statement firstMap) {//throws RDFHandlerException{
    	//TODO: Rename method to validateHeader?
        Reporter.report("Validating linkset VoID header");
        validateVoidDescription();
        linksetResource = findTheSingletonSubject(RdfConstants.TYPE_URI, VoidConstants.LINKSET);
        linksetPredicate = findTheSingletonObject (linksetResource, VoidConstants.LINK_PREDICATE);
        linksetJustification = findTheSingletonObject(linksetResource, DulConstants.EXPRESSES);
        //Provide details of the license under which the dataset is published using the dcterms:license property.
        checkObject(linksetResource, DctermsConstants.LICENSE);
        //The linkset authorship, i.e. the agent that generated the intellectual knowledge
        validateLinksetProvenance();
        subjectURISpace = validateDataSetAndExtractUriSpace(firstMap.getSubject(), VoidConstants.SUBJECTSTARGET);
        targetURISpace = validateDataSetAndExtractUriSpace(firstMap.getObject(), VoidConstants.OBJECTSTARGET);
        isSymmetric = checkIsSymmetric();
        isTransative = checkIsTransative();
        if (headerError) {
        	
        } else {
        	Reporter.report("\tLinkset VoID header is valid!");
        }
        processingLinks  = true;
        Reporter.report("Validating linkset links");
     }

	private void validateVoidDescription() {
		linksetResource = findTheSingletonSubject(RdfConstants.TYPE_URI, 
				VoidConstants.DATASET_DESCRIPTION);
        checkObject(linksetResource, DctermsConstants.TITLE);
        checkObject(linksetResource, DctermsConstants.DESCRIPTION);
        checkObject(linksetResource, PavConstants.CREATED_BY);
        checkObject(linksetResource, PavConstants.CREATED_ON);
        checkObject(linksetResource, FoafConstants.PRIMARY_TOPIC);
	}
    
    private void validateLinksetProvenance() {
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
        if (strict) {
        	headerError = true;
            Reporter.report(linksetResource + " must have one of:\n\t" + 
            		PavConstants.AUTHORED_BY + "\n\t" + 
                    PavConstants.DERIVED_BY + "\n\t" + 
            		PavConstants.CREATED_BY + "\n\t" + 
                    DctermsConstants.CREATOR);
        }
        Statement licenseStatement = new StatementImpl(linksetResource, 
        		PavConstants.CREATED_BY, UNSPECIFIED);
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
    private String validateDataSetAndExtractUriSpace(Value fullURI, URI targetPredicate) {
        Value dataSetId = findPossibleSingletonObject(targetPredicate);
        if (dataSetId == null){
            dataSetId = findAndRegisterDataSetIdBasedOnUriSpace(fullURI, targetPredicate);
        }
        //Declare that we have a dataset using void:dataset.
        checkStatementExists(dataSetId, RdfConstants.TYPE_URI, VoidConstants.DATASET);
        //Dataset title
        checkObject(dataSetId, DctermsConstants.TITLE);
        //Provide details of the licence under which the dataset is published using the dcterms:license property.
        checkObject(dataSetId, DctermsConstants.LICENSE);
        //There must be a version or a Date
        checkHasVersionOrDate(dataSetId);
        //The type of the resource being linked is declared with the dcterm:subject predicate.
//        checkObject(dataSetId, DctermsConstants.SUBJECT);      
        //The URI namespace for the resources being linked is declared using the void:uriSpace property
        Value uriValue = findTheSingletonObject(dataSetId, VoidConstants.URI_SPACE);
        String uriSpace = null;
        if (uriValue != null) {
        	uriSpace = uriValue.stringValue();
        	if (fullURI.stringValue().startsWith(uriSpace)){
        		return uriSpace;
        	}
        }
        headerError = true;
        Reporter.report("Declared URISpace " + uriSpace + 
        		" and uri in first link " + fullURI + " do not match");
        return UNSPECIFIED.stringValue();
    }

    private Value findAndRegisterDataSetIdBasedOnUriSpace(Value fullURI, URI targetPredicate) {
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
        headerError = true;
        Reporter.report("Unable to find a " + VoidConstants.TARGET + " with " 
                + VoidConstants.URI_SPACE + " that covers " + fullURI);
        return UNSPECIFIED;
    }

   private void checkHasVersionOrDate(Value dataSetId) {
        Value license = findPossibleSingletonObject (dataSetId, PavConstants.VERSION);
        if (license != null) return;
        Value date = findPossibleSingletonObject (dataSetId, PavConstants.CREATED_ON);
        if (date != null) return;
        date = findPossibleSingletonObject (dataSetId, PavConstants.DERIVED_ON);
        if (date != null) return;
        date = findPossibleSingletonObject (dataSetId, PavConstants.IMPORTED_ON);
        if (date != null) return;
        date = findPossibleSingletonObject (dataSetId, DctermsConstants.MODIFIED);
        if (date != null) return;
        date = findPossibleSingletonObject (dataSetId, PavConstants.RETRIEVED_ON);
        if (date != null) return;
        if (strict || !(dataSetId instanceof Resource)) {
        	headerError = true;
            Reporter.report("Could not find a Version for DataSet " + dataSetId + 
                    " Please include at least one of:\n\t" + 
            		PavConstants.VERSION + "\n\t" + 
                    PavConstants.CREATED_ON + "\n\t" + 
                    PavConstants.DERIVED_ON + "\n\t" + 
                    PavConstants.IMPORTED_ON + "\n\t" + 
                    DctermsConstants.MODIFIED + "\n\t" + 
                    PavConstants.RETRIEVED_ON);
        }
        Resource subjectR = (Resource)dataSetId;
        Statement versionStatement = 
        		new StatementImpl(subjectR, PavConstants.VERSION, UNSPECIFIED);
        statements.add(versionStatement);
   }
    
   boolean isSymmetric() {
	   //current assumption is all linksets are semaantic
	   return isSymmetric;
   }
   
   private boolean checkIsSymmetric() {
	   if (symmetricPredicates.contains(linksetPredicate)) {
		   return true;
	   } else {
		return false;
	   }
   }

	public boolean isTransative() throws RDFHandlerException {
        return isTransative;
    }

    private boolean checkIsTransative() {
        Value derivedFrom = findPossibleObject(linksetResource, PavConstants.DERIVED_FROM);
        if (derivedFrom == null) return false;
        Value derivedOn = findPossibleObject(linksetResource, PavConstants.DERIVED_ON);
        Value derivedBy = findPossibleObject(linksetResource, PavConstants.DERIVED_BY);
        return true;
    }
   
    private Resource findPossibleSingletonSubject (URI predicate) {
        Resource subject = null;
        for (Statement st:statements){
            if (st.getPredicate().equals(predicate)){
                if (subject != null){
                	headerError = true;
                    Reporter.report("Found more than one statement with predicate " + predicate);
                }
                subject = st.getSubject();
            }
        }
        return subject;
    }
    
   private Resource findPossibleSingletonSubject (URI predicate, Value object) {
        Resource subject = null;
        for (Statement st:statements){
            if (st.getPredicate().equals(predicate) && st.getObject().equals(object)){
                if (subject != null){
                	headerError = true;
                    Reporter.report("Found more than one statement with predicate " + predicate + 
                            " and object " + object);
                }
                subject = st.getSubject();
            }
        }
        return subject;
    }

    private Value findPossibleSingletonObject (URI predicate) {
        Value object = null;
        for (Statement st:statements){
            if (st.getPredicate().equals(predicate)){
                if (object != null){
                	headerError = true;
                    Reporter.report("Found more than one statement with predicate " + predicate);
                }
                object = st.getObject();
            }
        }
        return object;
    }
    
    private Value findPossibleSingletonObject (Value subject, URI predicate) {
        Value object = null;
        for (Statement st:statements){
            if (st.getSubject().equals(subject) && st.getPredicate().equals(predicate)){
                if (object != null){
                	headerError = true;
                    Reporter.report("Found more than one statement with subject " + subject + 
                            " and predicate " + predicate);
                }
                object = st.getObject();
            }
        }
        return object;
    }

    private Value findPossibleObject (Value subject, URI predicate) {
        Value object = null;
        for (Statement st:statements){
            if (st.getSubject().equals(subject) && st.getPredicate().equals(predicate)){
                return object = st.getObject();
            }
        }
        return object;
    }

    private Value findTheSingletonObject (Value subject, URI predicate) {
        Value object = findPossibleSingletonObject(subject, predicate);
        if (object == null) {
        	headerError = true;
            Reporter.report("Found no statement with subject " + subject +  
            		" and predicate " + predicate);
            object = UNSPECIFIED;
        }
        return object;
    }

    private void checkObject (Value subject, URI predicate) {
        for (Statement st:statements){
            if (st.getSubject().equals(subject) && st.getPredicate().equals(predicate)){
                return ;
            }
        }
        if (strict || (!(subject instanceof Resource))){
        	headerError = true;
            Reporter.report(subject + " does not have a " + predicate);
        } else {
            Resource subjectR = (Resource)subject;
            Statement newStatement = new StatementImpl(subjectR, predicate, UNSPECIFIED);
            statements.add(newStatement);
        }
   }

   private void checkStatementExists (Value subject, URI predicate, Value object) {
         for (Statement st:statements){
            if (st.getSubject().equals(subject) && 
            		st.getPredicate().equals(predicate) && 
            		st.getObject().equals(object)){
                return;
            }
        }
        headerError = true;
        Reporter.report("Found no statement with subject " + subject +  
        		" predicate " + predicate + 
                " and object " + object);
    }

    private Resource findTheSingletonSubject (URI predicate, Value object) {
        Resource subject = null;
        for (Statement st:statements){
            if (st.getObject().equals(object) && st.getPredicate().equals(predicate)){
                if (subject != null){
                	headerError = true;
                    Reporter.report("Found more than one statement with the " +
                    		"predicate " + predicate + 
                            " and Object " + object);
                }
                subject = st.getSubject();
            }
        }
        if (subject == null){
            headerError = true;
        	Reporter.report("Found no statement with predicate " + predicate + 
                            " and Object " + object);
        	subject = UNDEFINED; 
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

    String getJustification() {
		return linksetJustification.stringValue();
	}

	Resource getLinksetResource() {
        return linksetResource;
    }
    
    @Override
    public void insertURLMapping(Statement st) {
        String sourceURL = st.getSubject().stringValue();
        if (!sourceURL.startsWith(subjectURISpace)){
            linkError = true;
        	Reporter.report("Subject " + sourceURL + 
            		" does not begin with the declared uriSpace " 
                    + subjectURISpace);
        }
        Value predicate = st.getPredicate();
        if (!predicate.equals(linksetPredicate)){
        	linkError = true;
            Reporter.report("Predicate " + predicate + 
            		" does not begin with the declared predicate " 
                    + linksetPredicate);            
        }
        String targetURL = st.getObject().stringValue();
        if (!targetURL.startsWith(targetURISpace)){
        	linkError = true;
            Reporter.report("Target " + targetURL + 
            		" does not begin with the declared uriSpace " 
                    + targetURISpace);
        }
    }

    @Override
    public void closeInput() throws IDMapperException {
    	if (headerError || linkError) {
    		Reporter.report("Linkset not valid");
    		throw new IDMapperException("Linkset not valid!");
    	}
    	if (processingLinks && !linkError) {
    		Reporter.report("\tLinkset links are valid");
    	}
        //do nothing
    }

    @Override
    public void setSourceFile(String absolutePath) {
        //Do nothing does not need validating.
    }

 }
