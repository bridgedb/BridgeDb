/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.io.IOException;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Reporter;
import org.openrdf.OpenRDFException;

/**
 * Validation class 
 * WARNING hard coded for Christian Machine.
 * Edit as needed.
 * @author Christian
 */
public class LinksetValidator {
        
    public static void main(String[] args) throws IDMapperException, IOException, OpenRDFException  {        
        String[] args1 = {"D:/OpenPhacts/ondex2linksets/original", "validate"};
        LinksetLoader.main (args1);
	}

}
