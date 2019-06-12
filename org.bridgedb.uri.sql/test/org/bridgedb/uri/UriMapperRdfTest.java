/* BridgeDb,
* An abstraction layer for identifier mapping services, both local and online.
*
* Copyright 2006-2009  BridgeDb developers
* Copyright 2012-2013  Christian Y. A. Brenninkmeijer
* Copyright 2012-2013  OpenPhacts
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.bridgedb.uri;

import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.sql.transative.DirectMapping;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.uri.api.Mapping;
import org.bridgedb.uri.tools.StatementMaker;
import org.junit.jupiter.api.*;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.TreeModel;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.Rio;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the UriMapper interface (and by loading the UriListener interface)
 *
 * Should be passable by any implementation of UriMapper that has the test data loaded.
 * 
 * @author Christian
 */
public abstract class UriMapperRdfTest extends UriListenerTest{
           
    public static final String NULL_GRAPH = null;
    public static final Set<String> NULL_PATTERNS = null;
    public static final Set<DataSource> NO_TARGET_DATASOURCE = null;
    public static final Boolean DEFAULT_IGNORE_XREF = null;
    public static final String NULL_LENS = null;
    
    public static StatementMaker statementMaker;

    URI mappingSet1 = new URIImpl("http://example.com/testBasemappingSetRDF/1");
    URI linksetJustification = new URIImpl("http://openphacts.cs.man.ac.uk:9090/ontology/DataSource.owl#linksetJustification");
    URI inChlKey = new URIImpl("http://semanticscience.org/resource/CHEMINF_000059");

    URI importedFrom = new URIImpl("http://purl.org/pav/importedFrom");
    URI derivedFrom = new URIImpl("http://purl.org/pav/derivedFrom");
    URI example1to2 = new URIImpl("http://example.com/1to2");
    URI linkPredicate = new URIImpl("http://rdfs.org/ns/void#linkPredicate");
    URI exactMatch = new URIImpl("http://www.w3.org/2004/02/skos/core#exactMatch");
    
    URI sameAs = new URIImpl("http://www.w3.org/2002/07/owl#sameAs");
    URI concept = new URIImpl("http://www.conceptwiki.org/concept/f25a234e-df03-419f-8504-cde8689a4d1f");

    URI override = new URIImpl("http://example.com/override");
    
    public void checkMapping(Mapping mapping){
        if (!mapping.isMappingToSelf()){
            for (Mapping via:mapping.getViaMappings()){
                checkMapping(via);
            }
            checkDirect((DirectMapping)mapping);
        }
    }
    
    protected void checkDirect(Mapping mapping) {
        assertNotNull(mapping.getMappingSource());
    }
    
    @Test 
    public void testMapSetInfo1() throws Exception {
        report("MapSetInfo1");
        MappingSetInfo info = uriMapper.getMappingSetInfo(1);
        Set<Statement> rdf = statementMaker.asRDF(info, "http://example.com/testBase", "http://example.com/testContext");
        Model m = asModel(rdf);
        assertTrue(m.contains(null, linkPredicate, exactMatch));
        assertTrue(m.contains(null, importedFrom, null));

        // Disabled - not true when testing against
        // https://github.com/bridgedb/BridgeDb/blob/OpenPHACTS/develop/org.bridgedb.uri.loader/test-data/cw-cs.ttl
        // in ImsMapperRdfTest
        //assertTrue(m.contains(null, linksetJustification, null));

    }
    

    private Model asModel(Set<Statement> rdf) throws RDFHandlerException {
        Model m = new TreeModel(rdf);
        Rio.write(m, System.out, RDFFormat.TRIG);
        return m;
	}

	@Test 
    public void testMappingNoLink() throws Exception {
        report("MapSetInfoNoLink");
        Set<Mapping> mappings = uriMapper.mapFull(map1Uri1,  null, false, null, null);

        Set<Statement> rdf = statementMaker.asRDF(mappings, "http://example.com/testContext", false, null);
        Model m = asModel(rdf);

        assertTrue(m.contains(concept, sameAs, new URIImpl("http://www.conceptwiki.org/concept/index/f25a234e-df03-419f-8504-cde8689a4d1f")));
        assertTrue(m.contains(concept, sameAs, new URIImpl("http://www.conceptwiki.org/web-ws/concept/get?uuid=f25a234e-df03-419f-8504-cde8689a4d1f")));
        
        assertTrue(m.contains(concept, exactMatch, new URIImpl("http://identifiers.org/chemspider/28509384")));
        assertTrue(m.contains(concept, exactMatch, new URIImpl("http://info.identifiers.org/chemspider/28509384")));
        
        assertTrue(m.contains(concept, exactMatch, new URIImpl("http://ops.rsc.org/Compounds/Get/8")));
        assertTrue(m.contains(concept, exactMatch, new URIImpl("http://ops.rsc-us.org/OPS8")));
        assertTrue(m.contains(concept, exactMatch, new URIImpl("http://ops.rsc.org/OPS8")));
        assertTrue(m.contains(concept, exactMatch, new URIImpl("http://ops.rsc.org/OPS8/rdf")));
        
        assertTrue(m.contains(concept, exactMatch, new URIImpl("http://rdf.chemspider.com/28509384")));
        assertTrue(m.contains(concept, exactMatch, new URIImpl("http://www.chemspider.com/28509384")));
        assertTrue(m.contains(concept, exactMatch, new URIImpl("http://www.chemspider.com/Chemical-Structure.28509384")));
        assertTrue(m.contains(concept, exactMatch, new URIImpl("http://www.chemspider.com/Chemical-Structure.28509384.html")));
        assertTrue(m.contains(concept, exactMatch, new URIImpl("http://www.chemspider.com/Chemical-Structure.28509384.rdf")));
        
    }

	@Test 
    public void testMappingNoLinkFixedPredicate() throws Exception {
        report("MapSetInfoNoLinkFixedPredicate");
        Set<Mapping> mappings = uriMapper.mapFull(map1Uri1,  null, false, null, null);

        Set<Statement> rdf = statementMaker.asRDF(mappings, "http://example.com/testContext", true, "http://example.com/override");
        Model m = asModel(rdf);

        assertTrue(m.contains(concept, override, new URIImpl("http://www.conceptwiki.org/concept/index/f25a234e-df03-419f-8504-cde8689a4d1f")));
        assertTrue(m.contains(concept, override, new URIImpl("http://www.conceptwiki.org/web-ws/concept/get?uuid=f25a234e-df03-419f-8504-cde8689a4d1f")));
        
        assertTrue(m.contains(concept, override, new URIImpl("http://identifiers.org/chemspider/28509384")));
        assertTrue(m.contains(concept, override, new URIImpl("http://info.identifiers.org/chemspider/28509384")));
        
        assertTrue(m.contains(concept, override, new URIImpl("http://ops.rsc.org/Compounds/Get/8")));
        assertTrue(m.contains(concept, override, new URIImpl("http://ops.rsc-us.org/OPS8")));
        assertTrue(m.contains(concept, override, new URIImpl("http://ops.rsc.org/OPS8")));
        assertTrue(m.contains(concept, override, new URIImpl("http://ops.rsc.org/OPS8/rdf")));
        
        assertTrue(m.contains(concept, override, new URIImpl("http://rdf.chemspider.com/28509384")));
        assertTrue(m.contains(concept, override, new URIImpl("http://www.chemspider.com/28509384")));
        assertTrue(m.contains(concept, override, new URIImpl("http://www.chemspider.com/Chemical-Structure.28509384")));
        assertTrue(m.contains(concept, override, new URIImpl("http://www.chemspider.com/Chemical-Structure.28509384.html")));
        assertTrue(m.contains(concept, override, new URIImpl("http://www.chemspider.com/Chemical-Structure.28509384.rdf")));
        
        URI mapSet1Override = new URIImpl("http://example.com/testContextmappingSetRDF/1?predicate=http%3A%2F%2Fexample.com%2Foverride");
        URI mapSet1_3Override = new URIImpl("http://example.com/testContextmappingSetRDF/1_3?predicate=http%3A%2F%2Fexample.com%2Foverride");
        URI example1to2 = new URIImpl("http://example.com/1to2");
        URI example2to3 = new URIImpl("http://example.com/2to3");
        
        // FIXME: Exactly how should the provenance chains go here?
//        URIImpl mapset1 = new URIImpl("http://example.com/testContextmappingSetRDF/1");
//		assertTrue(m.contains(mapSet1Override, derivedFrom, mapset1));
//        assertTrue(m.contains(mapset1, derivedFrom, example1to2));
//
//        URIImpl mapset1_3 = new URIImpl("http://example.com/testContextmappingSetRDF/1_3");
//		assertTrue(m.contains(mapSet1_3Override, derivedFrom, mapset1_3));        
//        assertTrue(m.contains(mapset1_3, derivedFrom, example1to2));
//        assertTrue(m.contains(mapset1_3, derivedFrom, example2to3));
        
    }
	
    @Test 
    public void testMappingWithLink() throws Exception {
        report("MapSetInfoWithLink");
        Set<Mapping> mappings = uriMapper.mapFull(map1Uri1,  null, true, null, null);
        Set<Statement> rdf = statementMaker.asRDF(mappings, "http://example.com/testContext", false, "http://example.com/override");
        Model m = asModel(rdf);
        assertTrue(m.contains(concept, override, new URIImpl("http://www.conceptwiki.org/concept/index/f25a234e-df03-419f-8504-cde8689a4d1f")));
        assertTrue(m.contains(concept, override, new URIImpl("http://www.conceptwiki.org/web-ws/concept/get?uuid=f25a234e-df03-419f-8504-cde8689a4d1f")));
        
        assertTrue(m.contains(concept, override, new URIImpl("http://identifiers.org/chemspider/28509384")));
        assertTrue(m.contains(concept, override, new URIImpl("http://info.identifiers.org/chemspider/28509384")));
        
        assertTrue(m.contains(concept, override, new URIImpl("http://ops.rsc.org/Compounds/Get/8")));
        assertTrue(m.contains(concept, override, new URIImpl("http://ops.rsc-us.org/OPS8")));
        assertTrue(m.contains(concept, override, new URIImpl("http://ops.rsc.org/OPS8")));
        assertTrue(m.contains(concept, override, new URIImpl("http://ops.rsc.org/OPS8/rdf")));
        
        assertTrue(m.contains(concept, override, new URIImpl("http://rdf.chemspider.com/28509384")));
        assertTrue(m.contains(concept, override, new URIImpl("http://www.chemspider.com/28509384")));
        assertTrue(m.contains(concept, override, new URIImpl("http://www.chemspider.com/Chemical-Structure.28509384")));
        assertTrue(m.contains(concept, override, new URIImpl("http://www.chemspider.com/Chemical-Structure.28509384.html")));
        assertTrue(m.contains(concept, override, new URIImpl("http://www.chemspider.com/Chemical-Structure.28509384.rdf")));
    }
    
    @Test 
    public void testMappingWithLinkOverride() throws Exception {
        report("MapSetInfoWithLink");
        Set<Mapping> mappings = uriMapper.mapFull(map1Uri1,  null, true, null, null);
        Set<Statement> rdf = statementMaker.asRDF(mappings, "http://example.com/testContext", false, null);
        Model m = asModel(rdf);
        assertTrue(m.contains(concept, sameAs, new URIImpl("http://www.conceptwiki.org/concept/index/f25a234e-df03-419f-8504-cde8689a4d1f")));
        assertTrue(m.contains(concept, sameAs, new URIImpl("http://www.conceptwiki.org/web-ws/concept/get?uuid=f25a234e-df03-419f-8504-cde8689a4d1f")));
        
        assertTrue(m.contains(concept, exactMatch, new URIImpl("http://identifiers.org/chemspider/28509384")));
        assertTrue(m.contains(concept, exactMatch, new URIImpl("http://info.identifiers.org/chemspider/28509384")));
        
        assertTrue(m.contains(concept, exactMatch, new URIImpl("http://ops.rsc.org/Compounds/Get/8")));
        assertTrue(m.contains(concept, exactMatch, new URIImpl("http://ops.rsc-us.org/OPS8")));
        assertTrue(m.contains(concept, exactMatch, new URIImpl("http://ops.rsc.org/OPS8")));
        assertTrue(m.contains(concept, exactMatch, new URIImpl("http://ops.rsc.org/OPS8/rdf")));
        
        assertTrue(m.contains(concept, exactMatch, new URIImpl("http://rdf.chemspider.com/28509384")));
        assertTrue(m.contains(concept, exactMatch, new URIImpl("http://www.chemspider.com/28509384")));
        assertTrue(m.contains(concept, exactMatch, new URIImpl("http://www.chemspider.com/Chemical-Structure.28509384")));
        assertTrue(m.contains(concept, exactMatch, new URIImpl("http://www.chemspider.com/Chemical-Structure.28509384.html")));
        assertTrue(m.contains(concept, exactMatch, new URIImpl("http://www.chemspider.com/Chemical-Structure.28509384.rdf")));
    }
}
 
