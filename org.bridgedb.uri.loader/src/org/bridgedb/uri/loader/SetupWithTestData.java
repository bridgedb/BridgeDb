/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.uri.loader;

import java.io.File;
import org.bridgedb.sql.SQLUriMapper;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.Reporter;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author Christian
 */
public class SetupWithTestData {
    
    static final String MAIN_JUSTIFCATION = "http://semanticscience.org/resource/CHEMINF_000059";
    static final String LENS_JUSTIFCATION = "http://www.bridgedb.org/test#testJustification";
    static final URI linkPredicate = new URIImpl("http://www.w3.org/2004/02/skos/core#exactMatch");
    private SQLUriMapper uriListener;
    private LinksetListener instance;

    public SetupWithTestData() throws BridgeDBException {
        uriListener = SQLUriMapper.createNew();
        instance = new LinksetListener(uriListener);
    }
    
    private void loadFile(String fileName, String justification) throws BridgeDBException{
        Reporter.println("parsing " + fileName);
        File file = new File(fileName);
        int mappingSetId = instance.parse(file, linkPredicate, justification, true);
        Reporter.println("       Loaded as mappingSet " + mappingSetId);
     }
    /**
     * ONLY recommended if using BridgeDB without the OpenPhacts IMS extension.
     * Test of parse method, of class LinksetListener.
     */
   public static void main(String[] args) throws BridgeDBException {
        Reporter.println("LoadTestData");
        SetupWithTestData loader = new SetupWithTestData();
        loader.loadFile("../org.bridgedb.uri.loader/test-data/cw-cs.ttl", MAIN_JUSTIFCATION);
        loader.loadFile("../org.bridgedb.uri.loader/test-data/cs-cm.ttl", MAIN_JUSTIFCATION);
        loader.loadFile("../org.bridgedb.uri.loader/test-data/cw-cm.ttl", MAIN_JUSTIFCATION);
        loader.loadFile("../org.bridgedb.uri.loader/test-data/cw-ct.ttl", MAIN_JUSTIFCATION);
        loader.loadFile("../org.bridgedb.uri.loader/test-data/cw-dd.ttl", MAIN_JUSTIFCATION);
        loader.loadFile("../org.bridgedb.uri.loader/test-data/cw-dt.ttl", MAIN_JUSTIFCATION);
        loader.loadFile("../org.bridgedb.uri.loader/test-data/cw-cs_test_lens.ttl", LENS_JUSTIFCATION);
        loader.loadFile("../org.bridgedb.uri.loader/test-data/cs-cm_test_lens.ttl", LENS_JUSTIFCATION);
        loader.loadFile("../org.bridgedb.uri.loader/test-data/cw-cm_test_lens.ttl", LENS_JUSTIFCATION);
    }

 }
