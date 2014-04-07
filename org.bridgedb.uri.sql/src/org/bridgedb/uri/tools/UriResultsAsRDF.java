/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridgedb.uri.tools;

import org.bridgedb.uri.lens.Lens;
import java.util.HashSet;
import java.util.Set;
import org.bridgedb.rdf.BridgeDbRdfTools;
import org.bridgedb.rdf.constants.DulConstants;
import org.bridgedb.rdf.constants.OWLConstants;
import org.bridgedb.rdf.constants.VoidConstants;
import org.bridgedb.uri.api.MappingsBySet;
import org.bridgedb.uri.api.SetMappings;
import static org.bridgedb.uri.api.SetMappings.HAS_LENS;
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
public class UriResultsAsRDF {
    private static URI toURI(String text){
        try {
            return new URIImpl(text);
        } catch (IllegalArgumentException ex){
            return new URIImpl("<" + text + ">");
        }
    }
    
    private static Set<Statement> asRDF(SetMappings setMappings, String lens, String lensBaseUri) throws BridgeDBException {
        HashSet<Statement> statements = new HashSet<Statement>();
        URI setUri = new URIImpl(setMappings.getMappingResource());
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
            URI hasLensUri = new URIImpl(HAS_LENS);
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
    
    public static Set<Statement> asRDF(MappingsBySet mappingsBySet, String lensBaseUri) throws BridgeDBException{
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

    public static String toRDF(MappingsBySet mappingsBySet, String formatName, String lensBaseUri) throws BridgeDBException{
        Set<Statement> statements = asRDF(mappingsBySet, lensBaseUri);
        return BridgeDbRdfTools.writeRDF(statements, formatName);
    }
    

}
