/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import org.openrdf.rio.ParseErrorListener;

/**
 *
 * @author Christian
 */
public class LinksetParserErrorListener implements ParseErrorListener{

    @Override
    public void warning(String message, int lineNo, int colNo) {
        System.out.println("WARNING *** " + message);
        System.out.println ("Line number: " + lineNo + " columns number: " + colNo);
    }

    @Override
    public void error(String message, int lineNo, int colNo) {
        System.out.println("***ERROR*** " + message);
        System.out.println ("Line number: " + lineNo + " columns number: " + colNo);
    }

    @Override
    public void fatalError(String message, int lineNo, int colNo) {
        System.out.println("******* FETAL ERROR  *** ");
        System.out.println(message);
        System.out.println ("Line number: " + lineNo + " columns number: " + colNo);
        System.out.println("*************************************************************************");
        System.out.println();
    }
    
}
