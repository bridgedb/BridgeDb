/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.tools.metadata.validator;

import org.bridgedb.tools.metadata.LinksetVoidInformation;
import org.bridgedb.tools.metadata.MetaDataSpecification;
import org.bridgedb.tools.metadata.MetaData;
import org.bridgedb.tools.metadata.MetaDataCollection;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.bridgedb.IDMapperException;
import org.bridgedb.tools.metadata.rdf.LinksetStatementReader;
import org.bridgedb.tools.metadata.rdf.LinksetStatements;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
import org.bridgedb.utils.Reporter;

/**
 *
 * @author Christian
 */
public class Validator {
    
    public static String VALIDATION = "validation";
    public static String INCLUDE_WARNINGS = "includeWarnings";
    
    static final Logger logger = Logger.getLogger(Validator.class);

    public static String validityReport (String dataFileName, ValidationType type, boolean includeWarnings) throws IDMapperException{
        MetaDataSpecification specification = 
                MetaDataSpecificationRegistry.getMetaDataSpecificationByValidatrionType(type);
        MetaData metaData;
        if (type.isLinkset()){
            LinksetStatements statements = new LinksetStatementReader(dataFileName);
            metaData = new LinksetVoidInformation(dataFileName, statements, specification);
        } else {
            metaData = new MetaDataCollection(dataFileName, specification);
        }
        return metaData.validityReport(includeWarnings);
    }
    
    static public void main(String[] args) throws IDMapperException {
        ConfigReader.logToConsole();
        if (args.length != 1){
            usage("Please specify a file/directory and use -D format for all other arguements.");
        }
        String fileName = args[0];
        Reporter.println("Checking " + fileName);
    
        String validationString = System.getProperty(VALIDATION);
        ValidationType validationType = null;
        if (validationString == null || validationString.isEmpty()){
            validationType = ValidationType.LINKS;
        } else {
            try {
                validationType = ValidationType.parseString(validationString);
            } catch (BridgeDBException ex) {
                usage(ex.getMessage());
            }
        }

        String includeWarningsString = System.getProperty(INCLUDE_WARNINGS, "true");
        boolean includeWarnings = Boolean.valueOf(includeWarningsString);
        
        Reporter.println(validityReport(fileName, validationType, includeWarnings));
    }

    private static void usage(String cause) {
        Reporter.println("This method uses a normal paramter and several named (-D) style parameters");
        Reporter.println("Required Parameter (following the jar) is:");
        Reporter.println("File to validate");
        Reporter.println("   Name (ideally with path) of the file to be validated.");
        Reporter.println("   Type of file will be dettermined based on the exstension.");
        Reporter.println("Optional -D format (before the jar) Parameters are:");
        Reporter.println(Validator.VALIDATION);
        Reporter.println("       See: http://www.openphacts.org/specs/datadesc/");
        Reporter.println("   " + ValidationType.VOID + ": Checks that all MUST and SHOULD values are present");
        Reporter.println("       Multiple datasets and linksets can be declared but links are not expected");
        Reporter.println("   " + ValidationType.LINKS + ": Checks that all MUST and SHOULD values are present");
        Reporter.println("       Only a single Linkset can be declared. ");
        Reporter.println("       Included Links are also validated.");
        Reporter.println("       Included Datasets are validated to Linkset \"Minimal Dataset Description\".");
        Reporter.println("   " + ValidationType.ANY_RDF + ": Checks that the data is valid RDF");
        Reporter.println("       Only really intended for checking a Parent data file.");
        Reporter.println("   " + ValidationType.LINKSMINIMAL + ": requires only the absolute mininal void to load the data");
        Reporter.println("       Please attempt to complete the missing information and run." + ValidationType.LINKS);
        Reporter.println("   Default is " + ValidationType.LINKS);
        Reporter.println(INCLUDE_WARNINGS);
        Reporter.println("   This determines if the validator will include warnings.");
        Reporter.println("   Any non null value other than \"true\" ignoring case will remove warnings.");
        Reporter.println("   Default is to include warnings.");
        Reporter.println(cause);
        System.exit(1);
    }

}
