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
package org.bridgedb.tools.metadata;

import java.util.Set;
import org.bridgedb.IDMapperException;
import org.bridgedb.rdf.constants.BridgeDBConstants;
import org.bridgedb.rdf.constants.VoidConstants;
import org.bridgedb.tools.metadata.constants.DulConstants;
import org.bridgedb.tools.metadata.constants.OwlConstants;
import org.bridgedb.tools.metadata.constants.PavConstants;
import org.bridgedb.tools.metadata.constants.SkosConstants;
import org.bridgedb.tools.metadata.rdf.LinksetStatements;
import org.bridgedb.tools.metadata.validator.MetaDataSpecificationRegistry;
import org.bridgedb.tools.metadata.validator.ValidationType;
import org.bridgedb.utils.BridgeDBException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 *
 * @author Christian
 */
public class LinksetVoidInformation implements MetaData {
  
    private MetaDataCollection collection;
    private Resource linksetResource = null;
    private boolean transative; 
    private String predicate = null;
    private boolean isSymmetric;
    private String subjectUriSpace = null;
    private String targetUriSpace = null;
    private String linksetJustification = null;
    private String error = "";
    private int correctLinks = 0;
    private int wrongSubject = 0;
    private String subjectExample;
    private int wrongTarget = 0;
    private String targetExample;
    private boolean INCLUDE_WARNINGS = true;
    private boolean NO_WARNINGS = false;
    
    public LinksetVoidInformation(String source, LinksetStatements reader,  ValidationType type) throws IDMapperException{
        this(source, reader, MetaDataSpecificationRegistry.getMetaDataSpecificationByValidatrionType(type));
    }
    
    public LinksetVoidInformation(String source, LinksetStatements reader, MetaDataSpecification specification) 
            throws BridgeDBException{
        collection = new MetaDataCollection(source, reader.getVoidStatements(), specification);
        ResourceMetaData linkset = findLinkSet();
        predicate = extractSingleStringByPredicate(linkset, VoidConstants.LINK_PREDICATE);  
        linksetJustification = extractSingleStringByPredicate(linkset, DulConstants.EXPRESSES);  
        isSymmetric = checkIsSymmetric();
        transative =  checkIsTransative(linkset);
        ResourceMetaData resourceMetaData = extractSingletonResourceMetaDataBypredicate(linkset, VoidConstants.SUBJECTSTARGET);
        subjectUriSpace = extractSingleStringByPredicate(resourceMetaData, VoidConstants.URI_SPACE_URI);
        ResourceMetaData target = extractSingletonResourceMetaDataBypredicate(linkset, VoidConstants.OBJECTSTARGET);
        targetUriSpace = extractSingleStringByPredicate(target, VoidConstants.URI_SPACE_URI);
        validateLinks(reader.getLinkStatements());
//        validate();
     }
    
    private ResourceMetaData findLinkSet(){
        Set<ResourceMetaData> possibleResults = collection.getResourceMetaDataByType(VoidConstants.LINKSET);
        if (possibleResults == null){
            error = "No Resource found with the type " + VoidConstants.LINKSET + ". ";
            return null;
        }
        if (possibleResults.isEmpty()){
            error = "Found no Resource with the type " + VoidConstants.LINKSET + ". ";
            return null;
        }
        if (possibleResults.size()> 1){
            error = "Found more than one Resource with the type " + VoidConstants.LINKSET + ". \n\t" + possibleResults;
            return null;
        }
        ResourceMetaData linkset = possibleResults.iterator().next();
        linksetResource = linkset.getId();
        return linkset;
    }
    
    private ResourceMetaData extractSingletonResourceMetaDataBypredicate(ResourceMetaData linkset, URI predicate){
        if (linkset == null){
            //Already in error mode.
            return null;
        }
        Set<ResourceMetaData> possibleResults = linkset.getResoucresByPredicate(predicate);
        if (possibleResults == null){
            error = error + linkset.id + " Has no predicate " + predicate + ". ";
            return null;
        }
        if (possibleResults.isEmpty()){
            error = error + linkset.id + " Contains no predicate " + predicate + ". ";
            return null;
        }
        if (possibleResults.size()> 1){
            error = error + linkset.id + " Has has more than one predicate " + predicate + ". \n\t " + possibleResults;
            return null;
        }
        return possibleResults.iterator().next();
    }
    
    private Value extractSingleValueByPredicate(ResourceMetaData metaDataResource, URI predicate) {
        if (metaDataResource == null){
            //Already in error mode.
            return null;
        }
        Set<Value> possible = metaDataResource.getValuesByPredicate(predicate);
        if (possible == null){
            error = error + "Resource " + metaDataResource.getId() + " has no predicate " + predicate + ". ";
            return null;
        }
        if (possible.isEmpty()){
            error = error + "Resource " + metaDataResource.getId() + " does not have a predicate " + predicate + ". ";
            return null;
        }
        if (possible.size() > 1){
            error = error + "Resource " + metaDataResource.getId() + " has " + possible.size() + 
                    " predicates " + predicate + ". When a unique one is required. \n\t" +
                    possible;
        }
        return possible.iterator().next();
    }

    private URI extractSingleURIByPredicate(ResourceMetaData metaDataResource, URI predicate) {
        Value value = extractSingleValueByPredicate(metaDataResource, predicate);
        if (value == null) { return null; }
        if (value instanceof URI){
            return (URI)value;
        }
        error = error + "Resource " + metaDataResource.getId() + " has predicates " + predicate + 
                " with a Object of type " + value.getClass() + ". But a URI was required. ";
        return null;
    }

    private String extractSingleStringByPredicate(ResourceMetaData metaDataResource, URI predicate) throws BridgeDBException{
        Value value = extractSingleValueByPredicate(metaDataResource, predicate);
        if (value == null){
            return null;
        } else {
            return value.stringValue();
        }
    }
    
    private void validateLinks(Set<Statement> linkStatements) {
        if ((subjectUriSpace == null) || (targetUriSpace == null)){
            correctLinks = linkStatements.size();
        } else {
            for (Statement link:linkStatements){
                if (!link.getSubject().stringValue().startsWith(subjectUriSpace)){
                    if (wrongSubject == 0){
                        subjectExample = link.getSubject().stringValue();
                    }
                    wrongSubject++; 
                    if (!link.getObject().stringValue().startsWith(targetUriSpace)){
                        if (wrongTarget == 0){
                            targetExample = link.getSubject().stringValue();
                        }
                        wrongTarget++;   
                    }
                } else if (!link.getObject().stringValue().startsWith(targetUriSpace)){
                    if (wrongTarget == 0){
                        targetExample = link.getSubject().stringValue();
                    }
                    wrongTarget++;               
                } else {
                    correctLinks++;
                }
            }
        }       
    }

    /**
     * When creating a Transative link various values are added.
     * If any of these are missing the file is considered as non transative
     * 
     * @param linkset
     * @return 
     */
    private boolean checkIsTransative(ResourceMetaData linkset) {
        if (linkset == null) {
            return false;
        }
        Set<Value> derivedFrom = linkset.getValuesByPredicate(PavConstants.DERIVED_FROM);
        if (derivedFrom == null || derivedFrom.isEmpty()) return false;
        Set<Value> derivedOn = linkset.getValuesByPredicate(PavConstants.DERIVED_ON);
        if (derivedOn == null || derivedOn.isEmpty()) return false;
        Set<Value> derivedBy = linkset.getValuesByPredicate(PavConstants.DERIVED_BY);
        if (derivedBy == null || derivedBy.isEmpty()) return false;
        return true;
    }
   
    @Override
    public void validate() throws BridgeDBException {
        String report = collection.validityReport(false);
        if (report.contains("ERROR") || !error.isEmpty()){
            if (!hasRequiredValues() ){
                report = "Missing required values. \n" + report;
            }
            if (!hasCorrectTypes()){
                report = "Incorrect Type(s)\n" + report;
            }
            if (!error.isEmpty()){
                throw new BridgeDBException(report + "\n" + error);
            }
        }
    }

    // Methods used to extract specific Linkset info
    
    public String getSubjectUriSpace() throws BridgeDBException {
        if (subjectUriSpace == null) {
            throw new BridgeDBException(error);
        }
        return subjectUriSpace;
    }

    public String getTargetUriSpace() throws BridgeDBException {
        if (targetUriSpace == null) {
            throw new BridgeDBException(error);
        }
        return targetUriSpace;
    }

    public String getPredicate() throws BridgeDBException {
        if (predicate == null) {
            throw new BridgeDBException(error);
        }
        return predicate;
    }

    public Resource getLinksetResource() throws BridgeDBException {
        if (linksetResource == null) {
            throw new BridgeDBException(error);
        }
        return linksetResource;
    }

    public boolean isTransative(){
        return transative;
        
    }
    // **** MetaData methdos  ***
    
    @Override
    public String Schema() {
       return collection.Schema() + "There must aslo be exactly ONE " + VoidConstants.LINKSET;
    }

    @Override
    public boolean hasRequiredValues() {
        return collection.hasRequiredValues() &&  (linksetResource != null) && (predicate != null) && 
                (subjectUriSpace != null) && (targetUriSpace != null);
    }

    @Override
    public boolean hasCorrectTypes() throws BridgeDBException {
        return collection.hasCorrectTypes();
    }

    @Override
    public String validityReport(boolean includeWarnings) throws BridgeDBException {
        if (error.isEmpty()) {
            if (wrongSubject == 0 && wrongTarget == 0){
                 return collection.validityReport(includeWarnings) + "\nFound " + correctLinks + " links";
            } else {
                String report = collection.validityReport(includeWarnings);   
                if (wrongSubject > 0){
                    report += "Found " + wrongSubject + " linkStatements with the wrong Subject" +
                            "\tFor example " + subjectExample + " does not start with " + subjectUriSpace;
                }
                if (wrongTarget > 0){
                    report += "Found " + wrongTarget + " linkStatements with the wrong Target" +
                            "\tFor example " + targetExample + " does not start with " + targetUriSpace;
                }
                return report;
            }
        } else {
            String validityReport = collection.validityReport(includeWarnings);
            if (validityReport.equals(AppendBase.CLEAR_REPORT)){
                return "\nError getting linkset information:\n" + error +
                    "\n Found " + correctLinks + " protentional links but could not check them";
            } else {
                return "Validation Report:\n" + validityReport + 
                        "\nWhich may have Errors getting linkset information:\n" + error +
                    "\nFound " + correctLinks + " protentional links but could not check them";
            }
        }
    }

    @Override
    public boolean allStatementsUsed() {
        return (collection.allStatementsUsed() && (linksetResource != null));
    } 

    @Override
    public String unusedStatements() {
        return collection.unusedStatements();
    }

    @Override
    public Set<Value> getValuesByPredicate(URI predicate) {
        return collection. getValuesByPredicate(predicate);
    }

    @Override
    public Set<ResourceMetaData> getResoucresByPredicate(URI predicate) {
        return collection.getResoucresByPredicate(predicate);
    }

    @Override
    public Set<Statement> getRDF() {
        return collection.getRDF();
    }

    private boolean checkIsSymmetric() {
        if (predicate.equals(OwlConstants.EQUIVALENT_CLASS.toString())){ 
            return true; 
        }
        if (predicate.equals(OwlConstants.SAME_AS.toString())){ 
           return true; 
        }
        if (predicate.equals(SkosConstants.EXACT_MATCH.toString())){ 
           return true; 
        }
        if (predicate.equals(SkosConstants.CLOSE_MATCH.toString())){ 
           return true; 
        }
        if (predicate.equals(SkosConstants.RELATED_MATCH.toString())){ 
           return true; 
        }
        if (predicate.equals(BridgeDBConstants.TEST_PREDICATE.toString())){ 
           return true; 
        }
        return false;
   }

    public boolean isSymmetric() {
	   //current assumption is all linksets are semaantic
	   return isSymmetric;
    }
   
    public String getJustification() {
		return linksetJustification;
    }

    
}
