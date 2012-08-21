/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import org.bridgedb.linkset.constants.RdfConstants;
import org.bridgedb.linkset.constants.VoidConstants;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author Christian
 */
public class tester {
    
    public static void main(String[] args)  {
        URI subject = new URIImpl ("http://www.conceptwiki.org/wiki/#dd758846-1dac-4f0d-a329-06af9a7fa413");
        URI predicate = RdfConstants.TYPE_URI;
        Value object = VoidConstants.DATASET;
        Statement statement = new StatementImpl(subject, predicate, object); 
        Object NULL = null;
  //      DataSetMetaData metaData = new DataSetMetaData();
   //     metaData.addStatement(statement);
   //     metaData.initialise();
  //      System.out.println(metaData.showAll());
    }
}
