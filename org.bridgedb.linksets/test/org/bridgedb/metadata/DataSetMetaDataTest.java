/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import org.bridgedb.linkset.constants.DctermsConstants;
import org.bridgedb.linkset.constants.RdfConstants;
import org.bridgedb.linkset.constants.VoidConstants;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author Christian
 */
public class DataSetMetaDataTest {
    
    static final Resource ID = new URIImpl ("http://www.example.com/test/id");
    static final Value TITLE = new LiteralImpl("The title");
    
    Statement idStatement; 
    Statement titleStatement;

    public DataSetMetaDataTest() {
        idStatement = new StatementImpl(ID, RdfConstants.TYPE_URI, VoidConstants.DATASET); 
        titleStatement = new StatementImpl(ID, DctermsConstants.TITLE, TITLE);
    }

    private RDFData loadRDFData(){
        RDFData data = new RDFData();
        data.addStatement(idStatement);
        data.addStatement(titleStatement);
        return data;
    }
    
    @Test
    public void testShowAll(){
        DataSetMetaData metaData = new DataSetMetaData(ID, loadRDFData());
        String showAll = metaData.showAll(RequirementLevel.MAY);
        System.out.println(showAll);
    } 


}
