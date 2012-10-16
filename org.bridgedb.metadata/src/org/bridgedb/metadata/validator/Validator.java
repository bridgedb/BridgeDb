/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata.validator;

import org.bridgedb.IDMapperException;
import org.bridgedb.metadata.*;
import org.bridgedb.rdf.LinksetStatementReader;
import org.bridgedb.rdf.LinksetStatements;
import org.bridgedb.utils.Reporter;

/**
 *
 * @author Christian
 */
public class Validator {
    
    public static String VALIDATION = "validation";
    public static String INCLUDE_WARNINGS = "includeWarnings";
    
    public static String validityReport (String dataFileName, ValidationType type, boolean includeWarnings) throws IDMapperException{
        MetaDataSpecification specification = 
                MetaDataSpecificationRegistry.getMetaDataSpecificationByValidatrionType(type);
        MetaData metaData;
        if (type.isLinkset()){
            LinksetStatements statements = new LinksetStatementReader(dataFileName);
            metaData = new LinksetVoidInformation(statements, specification);
        } else {
            metaData = new MetaDataCollection(dataFileName, specification);
        }
        return metaData.validityReport(includeWarnings);
    }
    
    static public void main(String[] args) throws IDMapperException {
        if (args.length != 1){
            usage("Please specify a file/directory and use -D format for all other arguements.");
        }
        String fileName = args[0];
        Reporter.report("Checking " + fileName);
    
        String validationString = System.getProperty(VALIDATION);
        ValidationType validationType = null;
        if (validationString == null || validationString.isEmpty()){
            validationType = ValidationType.LINKS;
        } else {
            try {
                validationType = ValidationType.parseString(validationString);
            } catch (MetaDataException ex) {
                usage(ex.getMessage());
            }
        }

        String includeWarningsString = System.getProperty(INCLUDE_WARNINGS, "true");
        boolean includeWarnings = Boolean.valueOf(includeWarningsString);
        
        Reporter.report(validityReport(fileName, validationType, includeWarnings));
    }

    private static void usage(String cause) {
        Reporter.report("This method uses a normal paramter and several named (-D) style parameters");
        Reporter.report("Required Parameter (following the jar) is:");
        Reporter.report("File to validate");
        Reporter.report("   Name (ideally with path) of the file to be validated.");
        Reporter.report("   Type of file will be dettermined based on the exstension.");
        Reporter.report("Optional -D format (before the jar) Parameters are:");
        Reporter.report(Validator.VALIDATION);
        Reporter.report("       See: http://www.openphacts.org/specs/datadesc/");
        Reporter.report("   " + ValidationType.DATASETVOID + ": Checks that all MUST and SHOULD values are present");
        Reporter.report("       Multiple datasets can be declared but linksets and links are not expected");
        Reporter.report("   " + ValidationType.LINKSETVOID + ": Checks that all MUST and SHOULD values are present");
        Reporter.report("       Multiple Linksets can be declared but links are not expected");
        Reporter.report("       Included Datasets are validated to Linkset \"Minimal Dataset Description\".");
        Reporter.report("   " + ValidationType.LINKS + ": Checks that all MUST and SHOULD values are present");
        Reporter.report("       Only a single Linkset can be declared. ");
        Reporter.report("       Included Links are also validated.");
        Reporter.report("       Included Datasets are validated to Linkset \"Minimal Dataset Description\".");
        Reporter.report("   " + ValidationType.LINKSMINIMAL + ": requires only the absolute mininal void to load the data");
        Reporter.report("       Please attempt to complete the missing information and run." + ValidationType.LINKS);
        Reporter.report("   Default is " + ValidationType.LINKS);
        Reporter.report(INCLUDE_WARNINGS);
        Reporter.report("   This determines if the validator will include warnings.");
        Reporter.report("   Any non null value other than \"true\" ignoring case will remove warnings.");
        Reporter.report("   Default is to include warnings.");
        Reporter.report(cause);
        System.exit(1);
    }

}
