package org.bridgedb.utils;

/**
 * Util functions that allows messages to be output.
 * <p>
 * Allows the output format to be changed in one place so changing everywhere.
 * <p>
 * All other System.out calls can then be considered debug commands that should not have stayed in.
 * 
 * @author Christian
 */
public class Reporter {
    
    //Should be logger but using System out for now
    public static void report(String message){
        System.out.println(message);
    }
}
