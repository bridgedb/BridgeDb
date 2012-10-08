/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata.validator;

import java.io.File;
import java.util.Set;
import org.bridgedb.metadata.*;
import org.bridgedb.metadata.utils.Reporter;
import org.bridgedb.rdf.StatementReader;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;

/**
 *
 * @author Christian
 */
public class Validator {
    
    public static String validityReport (String dataFileName, ValidationType type, boolean includeWarnings) throws MetaDataException{
        MetaDataSpecification specification = 
                MetaDataSpecificationRegistry.getMetaDataSpecificationByValidatrionType(type);
        MetaData metaData;
        if (type.isLinkset()){
            metaData = new LinksetVoidInformation(dataFileName, specification);
        } else {
            metaData = new MetaDataCollection(dataFileName, specification);
        }
        return metaData.validityReport(includeWarnings);
    }
    
    static public void main(String[] arg) throws MetaDataException {
        if (arg.length < 2 || arg.length > 3){
            Reporter.report(validityReport("test-data/chemspider-void.ttl", ValidationType.DATASETVOID, true));
            return;
            //usage();
        }
        String fileName = arg[0];
        Reporter.report("Checking " + fileName);
        Reporter.report("    Up to Level " + fileName);
        boolean includeWarnings = true;
    
        ValidationType type = ValidationType.valueOf(arg[1]);
        Reporter.report("    Validating to Specifications " + type);
        
        if (arg.length > 2){
            includeWarnings = Boolean.parseBoolean(arg[1]);
        }
        if (includeWarnings){
            Reporter.report("    Including warnings ");
        } else {
            Reporter.report("    Excluding warnings ");
        }
        Reporter.report(validityReport(fileName, type, includeWarnings));
    }

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
        Reporter.report("Validation Type: ");
        Reporter.report("   This determines the specification agaoinst which the file will be validated.");
        Reporter.report("   Legal values are:");
        ValidationType[] types = ValidationType.values();
        Reporter.report("       " + ValidationType.valuesString());
        
        Reporter.report("Include Warnings: ");
        Reporter.report("   This determines if the validator will include warnings.");
        Reporter.report("   Any non null value other than \"true\" ignoring case will remove warnings.");
        Reporter.report("   Default is to include warnings.");
        System.exit(1);
    }

}
