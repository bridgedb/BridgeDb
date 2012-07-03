/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset.transative;

import org.bridgedb.linkset.LinksetLoader;

/**
 *
 * @author Christian
 */
public class TransativeManual {
    
    public static void main(String[] args) throws Exception {
        String[] args1 = new String[6];
        args1[0] = "33";
        args1[1] = "14";
        args1[2] = "load";
        args1[3] = "www";
        args1[4] = "purl";
        String fileName = "D:/OpenPhacts/ondex2linksets/JulyTransitive/cw-drugbankDrugs-transitive.ttl";
//        String fileName = "test-data/linkset2To3.ttl";
        args1[5] = fileName;
        TransativeCreator.main(args1);
        String[] args2 = new String[2];
        args2[0] = fileName;
        args2[1] = "validate";
        LinksetLoader.main (args2);
    }
}
