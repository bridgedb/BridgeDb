/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata.validator;

import java.io.File;
import java.util.Set;
import org.bridgedb.metadata.MetaDataCollection;
import org.bridgedb.metadata.MetaDataException;
import org.bridgedb.metadata.utils.Reporter;
import org.bridgedb.rdf.StatementReader;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;

/**
 *
 * @author Christian
 */
public class Validator {
    
/*    static public void main(String[] arg) throws MetaDataException {
        if (arg.length < 2 || arg.length > 3){
            usage();
        }
        String fileName = arg[0];
        Reporter.report("Checking " + fileName);
        Reporter.report("    Up to Level " + fileName);
        boolean includeWarnings = true;
        
        if (arg.length > 2){
            includeWarnings = Boolean.parseBoolean(arg[1]);
        }
        if (includeWarnings){
            Reporter.report("    Including warnings ");
        } else {
            Reporter.report("    Excluding warnings ");
        }
        File input = new File(fileName);
        Set<Statement> statements = StatementReader.extractStatements(input);
        MetaDataCollection metaData = new MetaDataCollection(statements);
        Reporter.report(metaData.validityReport(true));
    }
*/
    private static void usage() {
        Reporter.report("Welecome to the Void Validator.");
        Reporter.report("Please run it again with three parameters");
        Reporter.report("FileName: ");
        Reporter.report("   This must be an absolute file path or relative to the current location.");
        Set<RDFFormat> formats = StatementReader.getSupportedFormats();
        Reporter.report("   File exstention will be used to determine the parser to be used!");
        Reporter.report("   Supported formats are:");
        for (RDFFormat format: formats){
            Reporter.report("        " + format.toString());
        }
        Reporter.report("Include Warnings: ");
        Reporter.report("   This determines if the validator will include warnings.");
        Reporter.report("   Any non null value other than \"true\" ignoring case will remove warnings.");
        Reporter.report("   Default is to include warnings.");
        System.exit(1);
    }


}
