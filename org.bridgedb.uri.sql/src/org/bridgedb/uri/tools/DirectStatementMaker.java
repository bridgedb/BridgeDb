/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridgedb.uri.tools;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bridgedb.rdf.constants.BridgeDBConstants;
import org.bridgedb.rdf.constants.DulConstants;
import org.bridgedb.rdf.constants.OWLConstants;
import org.bridgedb.rdf.constants.PavConstants;
import org.bridgedb.rdf.constants.VoidConstants;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.uri.api.Mapping;
import org.bridgedb.uri.api.MappingsBySet;
import org.bridgedb.uri.api.SetMappings;
import org.bridgedb.uri.api.UriConstants;
import org.bridgedb.uri.api.UriMapping;
import org.bridgedb.uri.lens.Lens;
import org.bridgedb.uri.lens.LensTools;
import org.bridgedb.utils.BridgeDBException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

/**
 *
 * @author christian
 */
public class DirectStatementMaker implements StatementMaker{
   
    protected final IRI toURI(String text){
        try {
            return SimpleValueFactory.getInstance().createIRI(text);
        } catch (IllegalArgumentException ex){
            return SimpleValueFactory.getInstance().createIRI("<" + text + ">");
        }
    }
    
    private Set<Statement> asRDF(SetMappings setMappings, String lens, String baseUri, String methodName) throws BridgeDBException {
        HashSet<Statement> statements = new HashSet<Statement>();
        IRI setUri;
        setUri = SimpleValueFactory.getInstance().createIRI(baseUri + methodName + "/" + setMappings.getId());
        IRI predicateURI = toURI(setMappings.getPredicate());
        Statement statement = SimpleValueFactory.getInstance().createStatement(setUri, VoidConstants.LINK_PREDICATE, predicateURI);
        statements.add(statement);
        IRI justifcationURI = toURI(setMappings.getJustification());
        statement = SimpleValueFactory.getInstance().createStatement(setUri, DulConstants.EXPRESSES, justifcationURI);
        statements.add(statement);
        String source = setMappings.getMappingSource();
        if (source != null && !source.isEmpty()){
        	IRI mappingSourceURI = toURI(setMappings.getMappingSource());
            statement = SimpleValueFactory.getInstance().createStatement(setUri, VoidConstants.DATA_DUMP, mappingSourceURI);
            statements.add(statement);
        }
        if (lens != null){
            Lens theLens = LensTools.byId(lens);
            IRI lensUri = SimpleValueFactory.getInstance().createIRI(theLens.toUri(baseUri));
            IRI hasLensUri = BridgeDBConstants.FULFILLS_LENS;
            statement = SimpleValueFactory.getInstance().createStatement(setUri, hasLensUri, lensUri);
            statements.add(statement);
        }
        for (UriMapping mapping:setMappings.getMappings()){
        	IRI sourceURI = toURI(mapping.getSourceUri());
        	IRI targetURI = toURI(mapping.getTargetUri());
            statement =  SimpleValueFactory.getInstance().createStatement(sourceURI, predicateURI, targetURI, setUri);
            statements.add(statement);
        }
        return statements;
    }
    
    /***
     * @deprecated 
     * @param mappingsBySet - mappings by set
     * @param baseUri - base uri
     * @return RDF set
     * @throws BridgeDBException if something goes wrong
     */
    @Override
    public Set<Statement> asRDF(MappingsBySet mappingsBySet, String baseUri, String methodName) throws BridgeDBException{
        HashSet<Statement> statements = new HashSet<Statement>();
        for (SetMappings setMapping: mappingsBySet.getSetMappings()){
            Set<Statement> more = asRDF(setMapping, mappingsBySet.getLens(), baseUri, methodName);
            statements.addAll(more);          
        }
        for (UriMapping mapping:mappingsBySet.getMappings()){
            //Inclusion of mapping to self at Antonis request April 2014
            //if (!mapping.getSourceUri().equals(mapping.getTargetUri())){
        	IRI sourceURI = toURI(mapping.getSourceUri());
        	IRI targetURI = toURI(mapping.getTargetUri());
                Statement statement =  SimpleValueFactory.getInstance().createStatement(sourceURI, OWLConstants.SAMEAS_URI, targetURI);
                statements.add(statement);
            //}
        }

       return statements;
    }

    protected IRI mappingSetURI(String id, String baseUri, String predicateURI){
        String uriStr = baseUri + UriConstants.MAPPING_SET + UriConstants.RDF + "/" + id;
        if (predicateURI != null) {
        	String p;
			p = encodeWithinQuery(predicateURI);
        	uriStr += "?" + UriConstants.QUERY_PREDICATE + "=" + p;
        }
		return toURI(uriStr);
    }
    
    private String encodeWithinQuery(String str) {
    	String space = UUID.randomUUID().toString();
    	str = str.replace(" ", space);
    	try {
			str = URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("UTF-8 was not a supported encoding");
		}
    	return str.replace(space, "%20");
	}

	@Override
    public Set<Statement> asRDF(MappingSetInfo info, String baseUri, String contextString) throws BridgeDBException{
        HashSet<Statement> results = new HashSet<Statement>();
        IRI linksetId = mappingSetURI(info.getStringId(), baseUri, null);
        IRI source = toURI(info.getMappingSource());
        IRI context = SimpleValueFactory.getInstance().createIRI(contextString);
        results.add(SimpleValueFactory.getInstance().createStatement(linksetId, PavConstants.IMPORTED_FROM, source, context));
        IRI predicate = toURI(info.getPredicate());
        results.add(SimpleValueFactory.getInstance().createStatement(linksetId, VoidConstants.LINK_PREDICATE, predicate, context));
        IRI justification = toURI(info.getJustification());
        results.add(SimpleValueFactory.getInstance().createStatement(linksetId, BridgeDBConstants.LINKSET_JUSTIFICATION, justification, context));
        return results;
    }

    private void addMappingsRDF(Set<Statement> statements, Mapping mapping, IRI predicateUri, IRI mappingSet) throws BridgeDBException{
        for (String source:mapping.getSourceUri()){
            IRI sourceUri = SimpleValueFactory.getInstance().createIRI(source);
            for (String target: mapping.getTargetUri()){
                IRI targetUri = SimpleValueFactory.getInstance().createIRI(target);
                statements.add(SimpleValueFactory.getInstance().createStatement(sourceUri, predicateUri, targetUri, mappingSet));
            }
        }
    }
    
    protected void addLinksetInfo(Set<Statement> statements, Mapping mapping, IRI mappingSet) throws BridgeDBException{
        if (mapping.isMappingToSelf()){
            //No void for mapping to self at the moment.
        } else if (mapping.isTransitive()){
            for (Mapping via:mapping.getViaMappings()){
                addMappingVoid(statements, via, mappingSet);            
            }
        } else {
            addMappingVoid(statements, mapping, mappingSet);
        } 
    }
    
    protected void addMappingVoid(Set<Statement> statements, Mapping mapping, IRI mappingSet)
            throws BridgeDBException {
        IRI sourceUri = toURI(mapping.getMappingSource());
        statements.add(SimpleValueFactory.getInstance().createStatement(mappingSet, PavConstants.DERIVED_FROM, sourceUri, mappingSet));
    }
     
    private void addSelfMappingsRDF(Set<Statement> statements, Mapping mapping, String selfMappingPredicateURI) throws BridgeDBException{
    	IRI predicate;
		if (selfMappingPredicateURI == null || selfMappingPredicateURI.isEmpty()) {
        	predicate = OWLConstants.SAMEAS_URI;
        } else {
        	predicate = SimpleValueFactory.getInstance().createIRI(selfMappingPredicateURI);
        }
    	
    	for (String source:mapping.getSourceUri()){
            IRI sourceUri = SimpleValueFactory.getInstance().createIRI(source);
            for (String target: mapping.getTargetUri()){
                if (!source.equals(target)){
                    IRI targetUri = SimpleValueFactory.getInstance().createIRI(target);
                    statements.add(SimpleValueFactory.getInstance().createStatement(sourceUri, predicate, targetUri));
                }
            }
        }
    }

    @Override
    public Set<Statement> asRDF(Set<Mapping> mappings, String baseUri, boolean linksetInfo, String overridePredicateURI) throws BridgeDBException{
        Set<Statement> statements = new HashSet<Statement>();
        for (Mapping mapping:mappings){
        	String predicate;	
        	if (overridePredicateURI != null) {
        		// If given, always override
        		predicate = overridePredicateURI;
        	} else {
        		predicate = mapping.getPredicate();
        	}
            if (predicate != null){
                String id = mapping.getMappingSetId();
                IRI mappingSet = mappingSetURI(id, baseUri, overridePredicateURI);
                IRI predicateUri = SimpleValueFactory.getInstance().createIRI(predicate);
                addMappingsRDF(statements, mapping, predicateUri, mappingSet);
                if (linksetInfo){
                    addLinksetInfo(statements, mapping, mappingSet);   
                }
            } else {
                addSelfMappingsRDF(statements, mapping, overridePredicateURI);
            }
        }
        return statements;
    }
    
}
