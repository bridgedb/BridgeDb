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
package org.bridgedb.uri;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import org.bridgedb.rdf.RdfBase;
import org.bridgedb.rdf.constants.DulConstants;
import org.bridgedb.rdf.constants.OWLConstants;
import org.bridgedb.rdf.constants.VoidConstants;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.Reporter;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.RDFWriterFactory;
import org.openrdf.rio.RDFWriterRegistry;
import org.openrdf.rio.n3.N3Writer;
import org.openrdf.rio.ntriples.NTriplesWriter;
import org.openrdf.rio.rdfxml.RDFXMLWriter;
import org.openrdf.rio.rdfxml.util.RDFXMLPrettyWriter;
import org.openrdf.rio.trig.TriGWriter;
import org.openrdf.rio.trix.TriXWriter;
import org.openrdf.rio.turtle.TurtleWriter;

/**
 * Holder class for the main Meta Data of MappingSet.
 *
 * Does not include everything in the void header but only what is captured in the SQL.
 * @author Christian
 */
public class MappingsBySet {
    private final String lens;
    private final Set<SetMappings> setMappings;
    
    static final Logger logger = Logger.getLogger(MappingsBySet.class);
    
    /*
     * These are the direct mappings based on namespace substitution
     */
    private final Set<UriMapping> mappings;
    
    public MappingsBySet(String lens){
        this.lens = lens;
        this.setMappings = new HashSet<SetMappings>();
        this.mappings = new HashSet<UriMapping>();
    }
    
    public void addMapping (int mappingSetId, String predicate, String justification, String mappingSource, 
            String mappingResource, String sourceUri, Set<String> targetUris){
        SetMappings setMapping = setMappingById(mappingSetId);
        if (setMapping == null){
            setMapping = new SetMappings(mappingSetId, predicate, justification, mappingSource, mappingResource);
            setMappings.add(setMapping);
        }
        for (String targetUri: targetUris){
            setMapping.addMapping(new UriMapping(sourceUri, targetUri));
        }
    }

    public void addSetMapping(SetMappings setMapping){
        setMappings.add(setMapping);
    }
    
    public void addMapping (int mappingSetId, String predicate, String justification, String mappingSource, 
            String mappingResource, String sourceUri, String targetUri){
        SetMappings setMapping = setMappingById(mappingSetId);
        if (setMapping == null){
            setMapping = new SetMappings(mappingSetId, predicate, justification, mappingSource, mappingResource);
            getSetMappings().add(setMapping);
        }
        setMapping.addMapping(new UriMapping(sourceUri, targetUri));
    }
    
    public final void addMapping (String sourceUri, String targetUri){
        mappings.add(new UriMapping(sourceUri, targetUri));
    }
    
    public final void addMapping (UriMapping uriMapping){
        mappings.add(uriMapping);
    }
     public void addMapping (String sourceUri, Set<String> targetUris){
       for (String targetUri:targetUris){
           addMapping(sourceUri, targetUri);
       }
    }

    private SetMappings setMappingById(int id) {
        for (SetMappings setMapping: getSetMappings()){
            if (setMapping.getId() == id){
                return setMapping;
            }
        }
        return null;
    }
    
    public Set<String> getTargetUris(){
        HashSet<String> targetUris = new HashSet<String>();
        for (SetMappings setMapping: getSetMappings()){
            targetUris.addAll(setMapping.getTargetUris());           
        }
        for (UriMapping mapping:getMappings()){
            targetUris.add(mapping.getTargetUri());
        }

        return targetUris;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder("Lens: ");
        sb.append(getLens());
        for (SetMappings setMapping: getSetMappings()){
            setMapping.append(sb);           
        }
        sb.append("\n\tUriSpace based mappings");
        for (UriMapping mapping:getMappings()){
            mapping.append(sb);
        }
        return sb.toString();
    }

    /**
     * @return the lens
     */
    public String getLens() {
        return lens;
    }

    /**
     * @return the setMappings
     */
    public Set<SetMappings> getSetMappings() {
        return setMappings;
    }

    /**
     * @return the mappings
     */
    public Set<UriMapping> getMappings() {
        return mappings;
    }
    
    public Set<Statement> asRDF() throws BridgeDBException{
        HashSet<Statement> statements = new HashSet<Statement>();
        for (SetMappings setMapping: getSetMappings()){
            Set<Statement> more = setMapping.asRDF(lens);
            statements.addAll(more);          
        }
        for (UriMapping mapping:mappings){
            if (!mapping.getSourceUri().equals(mapping.getTargetUri())){
                URI sourceURI = SetMappings.toURI(mapping.getSourceUri());
                URI targetURI = SetMappings.toURI(mapping.getTargetUri());
                Statement statement =  new StatementImpl(sourceURI, OWLConstants.SAMEAS_URI, targetURI);
                statements.add(statement);
            }
        }

       return statements;
    }
    
    /**
     * This method is required as at last check the BinaryRDFWriterFactory was not fully implemeneted.
     * @param format
     * @param writer
     * @return 
     */
    private static RDFWriter getWriterIfPossible(RDFFormat format, Writer writer){
        RDFWriterRegistry register =  RDFWriterRegistry.getInstance();
        RDFWriterFactory factory = register.get(format);
        if (factory == null){
            return null;
        }
        try {
            return factory.getWriter(writer);
        } catch (Exception ex){
            logger.error(ex);
            return null;
        }
    }
    
    private void writeRDF(Set<Statement> statements,  RDFFormat format, Writer writer) throws BridgeDBException{        
        RDFWriter rdfWriter = getWriterIfPossible(format, writer); 
        try {
            if (rdfWriter != null){
                rdfWriter.startRDF();
                rdfWriter.handleNamespace("ops", RdfBase.DEFAULT_BASE_URI);
                rdfWriter.handleNamespace("void", VoidConstants.voidns);
                rdfWriter.handleNamespace("dul", DulConstants.dulns);
                for(Statement statement:statements){
                    rdfWriter.handleStatement(statement);
                }
                rdfWriter.endRDF();
            } else {
                writer.flush();
                writer.write("No Writer available for ");
                writer.write(format.toString());
                writer.write("\n");
            }
       } catch (RDFHandlerException ex) {
            throw new BridgeDBException("Error writing RDF. ", ex);
        } catch (IOException ex) {
            throw new BridgeDBException("Error writing RDF. ", ex);
        }
    }
    
    public String toRDF(String formatName) throws BridgeDBException{
            Set<Statement> statements = asRDF();
            StringWriter writer = new StringWriter();
            if (formatName == null){
                formatName = "TriX";
            }
            RDFFormat rdfFormat = RDFFormat.valueOf(formatName);
            writeRDF(statements,  rdfFormat, writer);
            return writer.toString();
    }
    
    public static Set<String> getAvaiableWriters(){
        N3Writer n = null;
        NTriplesWriter nt = null;
        RDFXMLPrettyWriter x2 = null;
        RDFXMLWriter x = null;
        TriGWriter tr = null;
        TriXWriter tw = null;
        TurtleWriter t = null;
        HashSet<String> results = new HashSet<String>();
        StringWriter writer = new StringWriter();
        for (RDFFormat rdfFormat:RDFFormat.values()){
            RDFWriter rdfWriter = getWriterIfPossible(rdfFormat, writer); 
            if (rdfWriter != null){
                results.add(rdfFormat.getName());
            }
        }
        return results;
    }
    
    public static void main(String[] args) {
        Reporter.println(getAvaiableWriters().toString());
    }

    public boolean isEmpty(){
        return mappings.isEmpty() && setMappings.isEmpty();
    }
}
