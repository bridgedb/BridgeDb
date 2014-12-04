/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridgedb.uri.tools;

import org.bridgedb.uri.lens.Lens;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bridgedb.rdf.constants.BridgeDBConstants;
import org.bridgedb.rdf.constants.DulConstants;
import org.bridgedb.rdf.constants.OWLConstants;
import org.bridgedb.rdf.constants.PavConstants;
import org.bridgedb.rdf.constants.VoidConstants;
import org.bridgedb.sql.transative.DirectMapping;
import org.bridgedb.sql.transative.SelfMapping;
import org.bridgedb.sql.transative.TransitiveMapping;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.uri.api.Mapping;
import org.bridgedb.uri.api.MappingsBySet;
import org.bridgedb.uri.api.SetMappings;
import org.bridgedb.uri.api.UriConstants;
import org.bridgedb.uri.api.UriMapping;
import org.bridgedb.uri.lens.LensTools;
import org.bridgedb.utils.BridgeDBException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author christian
 */
public class DirectStatementMaker implements StatementMaker{
   
    protected final URI toURI(String text){
        try {
            return new URIImpl(text);
        } catch (IllegalArgumentException ex){
            return new URIImpl("<" + text + ">");
        }
    }
    
    private Set<Statement> asRDF(SetMappings setMappings, String lens, String lensBaseUri) throws BridgeDBException {
        HashSet<Statement> statements = new HashSet<Statement>();
        URI setUri = new URIImpl(setMappings.getMappingSource());
        URI predicateURI = toURI(setMappings.getPredicate());
        Statement statement = new StatementImpl(setUri, VoidConstants.LINK_PREDICATE, predicateURI);
        statements.add(statement);
        URI justifcationURI = toURI(setMappings.getJustification());
        statement = new StatementImpl(setUri, DulConstants.EXPRESSES, justifcationURI);
        statements.add(statement);
        URI mappingSourceURI = toURI(setMappings.getMappingSource());
        statement = new StatementImpl(setUri, VoidConstants.DATA_DUMP, mappingSourceURI);
        statements.add(statement);
        if (lens != null){
            Lens theLens = LensTools.byId(lens);
            URI lensUri = new URIImpl(theLens.toUri(lensBaseUri));
            URI hasLensUri = BridgeDBConstants.FULFILLS_LENS;
            statement = new StatementImpl(setUri, hasLensUri, lensUri);
            statements.add(statement);
        }
        for (UriMapping mapping:setMappings.getMappings()){
            URI sourceURI = toURI(mapping.getSourceUri());
            URI targetURI = toURI(mapping.getTargetUri());
            statement =  new ContextStatementImpl(sourceURI, predicateURI, targetURI, setUri);
            statements.add(statement);
        }
        return statements;
    }
    
    /***
     * @deprecated 
     * @param mappingsBySet
     * @param lensBaseUri
     * @return
     * @throws BridgeDBException 
     */
    @Override
    public Set<Statement> asRDF(MappingsBySet mappingsBySet, String lensBaseUri) throws BridgeDBException{
        HashSet<Statement> statements = new HashSet<Statement>();
        for (SetMappings setMapping: mappingsBySet.getSetMappings()){
            Set<Statement> more = asRDF(setMapping, mappingsBySet.getLens(), lensBaseUri);
            statements.addAll(more);          
        }
        for (UriMapping mapping:mappingsBySet.getMappings()){
            //Inclusion of mapping to self at Antonis request April 2014
            //if (!mapping.getSourceUri().equals(mapping.getTargetUri())){
                URI sourceURI = toURI(mapping.getSourceUri());
                URI targetURI = toURI(mapping.getTargetUri());
                Statement statement =  new StatementImpl(sourceURI, OWLConstants.SAMEAS_URI, targetURI);
                statements.add(statement);
            //}
        }

       return statements;
    }

    protected URI mappingSetURI(String id, String baseUri){
        return toURI(baseUri + UriConstants.MAPPING_SET + UriConstants.RDF + "/" + id);
    }
    
    @Override
    public Set<Statement> asRDF(MappingSetInfo info, String baseUri, String contextString) throws BridgeDBException{
        HashSet<Statement> results = new HashSet<Statement>();
        URI linksetId = mappingSetURI(info.getStringId(), baseUri);
        URI source = toURI(info.getMappingSource());
        URI context = new URIImpl(contextString);
        results.add(new ContextStatementImpl(linksetId, PavConstants.IMPORTED_FROM, source, context));
        URI predicate = toURI(info.getPredicate());
        results.add(new ContextStatementImpl(linksetId, VoidConstants.LINK_PREDICATE, predicate, context));
        URI justification = toURI(info.getJustification());
        results.add(new ContextStatementImpl(linksetId, BridgeDBConstants.LINKSET_JUSTIFICATION, justification, context));
        return results;
    }

    private void addMappingsRDF(Set<Statement> statements, Mapping mapping, URI predicateUri, URI mappingSet) throws BridgeDBException{
        for (String source:mapping.getSourceUri()){
            URI sourceUri = new URIImpl(source);
            for (String target: mapping.getTargetUri()){
                URI targetUri = new URIImpl(target);
                statements.add(new ContextStatementImpl(sourceUri, predicateUri, targetUri, mappingSet));
            }
        }
    }
    
    protected void addLinksetInfo(Set<Statement> statements, Mapping mapping, URI mappingSet) throws BridgeDBException{
        if (mapping instanceof DirectMapping){
            DirectMapping directMapping = (DirectMapping)mapping;
            addMappingVoid(statements, directMapping, mappingSet);
        } else if (mapping instanceof TransitiveMapping){
            TransitiveMapping transitiveMapping = (TransitiveMapping)mapping;
            List<DirectMapping> vias = transitiveMapping.getVia();
            for (DirectMapping via:vias){
                addMappingVoid(statements, via, mappingSet);            
            }
        } else if (mapping instanceof SelfMapping){
            //void to add
        } else {
            throw new BridgeDBException ("Unexpected mapping Type " + mapping.getClass());
        }
        
        mapping.getMappingSource();
    }
    
    protected void addMappingVoid(Set<Statement> statements, DirectMapping directMapping, URI mappingSet)
            throws BridgeDBException {
        URI sourceUri = toURI(directMapping.getMappingSource());
        statements.add(new ContextStatementImpl(mappingSet, PavConstants.DERIVED_FROM, sourceUri, mappingSet));
    }
     
    private void addSelfMappingsRDF(Set<Statement> statements, Mapping mapping) throws BridgeDBException{
        for (String source:mapping.getSourceUri()){
            URI sourceUri = new URIImpl(source);
            for (String target: mapping.getTargetUri()){
                if (!source.equals(target)){
                    URI targetUri = new URIImpl(target);
                    statements.add(new StatementImpl(sourceUri, OWLConstants.SAMEAS_URI, targetUri));
                }
            }
        }
    }

    @Override
    public Set<Statement> asRDF(Set<Mapping> mappings, String baseUri, boolean linksetInfo) throws BridgeDBException{
        Set<Statement> statements = new HashSet<Statement>();
        for (Mapping mapping:mappings){
            String predicate = mapping.getPredicate();
            if (predicate != null){
                URI mappingSet = mappingSetURI(mapping.combinedId(), baseUri);
                URI predicateUri = new URIImpl(mapping.getPredicate());
                addMappingsRDF(statements, mapping, predicateUri, mappingSet);
                if (linksetInfo){
                    addLinksetInfo(statements, mapping, mappingSet);   
                }
            } else {
                addSelfMappingsRDF(statements, mapping);
            }
        }
        return statements;
    }
    
    public Set<Statement> asRDFX(Set<Mapping> mappings, String baseUri) throws BridgeDBException{
         Set<Statement> statements = new HashSet<Statement>();
         for (Mapping mapping:mappings){
             String predicate = mapping.getPredicate();
             if (predicate != null){
                URI predicateUri = new URIImpl(mapping.getPredicate());
                URI mappingSet = mappingSetURI(mapping.combinedId(), baseUri);
                for (String source:mapping.getSourceUri()){
                    URI sourceUri = new URIImpl(source);
                    for (String target: mapping.getTargetUri()){
                        URI targetUri = new URIImpl(target);
                        statements.add(new ContextStatementImpl(sourceUri, predicateUri, targetUri, mappingSet));
                    }
                }
             }
         }
         return statements;
     }

}
