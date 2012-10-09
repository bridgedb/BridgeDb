/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridgedb.loader;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.bridgedb.IDMapperException;
import org.bridgedb.linkset.IDMapperLinksetException;
import org.bridgedb.linkset.LinksetLoader;
import org.bridgedb.linkset.transative.TransativeCreator;
import org.bridgedb.metadata.MetaDataException;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.utils.Reporter;
import org.openrdf.rio.RDFHandlerException;

/**
 *
 * @author Christian
 */
public class RunLoader {

    private static void loadFile (String fileName) 
            throws IDMapperException, BridgeDbSqlException, IDMapperLinksetException, FileNotFoundException, MetaDataException{
        Reporter.report(fileName);
        String[] args = new String[2];
        args[0] = fileName;
        args[1] = "load";
        LinksetLoader.main (args);
    }

    private static void transtitive(int leftId, int rightId, String fileName)
            throws Exception {
        Reporter.report(fileName);
        String[] args = new String[4];
        args[0] = leftId + "";
        args[1] = rightId + "";
        args[2] = "load";
        args[3] = fileName;
        TransativeCreator.main (args);
        loadFile (fileName);
    }

    private static void transtitive2(int leftId, int rightId, String fileName)
            throws Exception {
        Reporter.report(fileName);
        String[] args = new String[6];
        args[0] = leftId + "";
        args[1] = rightId + "";
        args[2] = "load";
        args[3] = "www";
        args[4] = "purl";
        args[5] = fileName;
        TransativeCreator.main (args);
        loadFile (fileName);
    }

    public static void main(String[] args) 
            throws Exception  {

        String root = "C:/Dropbox/linksets/";
        
        String[] args1 = {root + "originals/ConceptWiki-Chembl2Targets.ttl", "new"};
        LinksetLoader.main (args1);
        loadFile (root + "originals/ConceptWiki-ChemSpider.ttl");
        loadFile (root + "originals/ConceptWiki-DrugbankTargets.ttl");
        loadFile (root + "originals/ConceptWiki-GO.ttl");
        loadFile (root + "originals/ConceptWiki-MSH.ttl");
        loadFile (root + "originals/ConceptWiki-NCIM.ttl");
        loadFile (root + "originals/ConceptWiki-Pdb.ttl");
        loadFile (root + "originals/ConceptWiki-Swissprot.ttl");
        loadFile (root + "originals/Chembl13Id-ChemSpider.ttl");
        loadFile (root + "originals/Chembl13Molecule-Chembl13Id.ttl");
        loadFile (root + "originals/Chembl13Targets-Enzyme.ttl");
        loadFile (root + "originals/Chembl13Targets-Swissprot.ttl");
        loadFile (root + "originals/ChemSpider-Chembl2Compounds.ttl");
        loadFile (root + "originals/ChemSpider-DrugBankDrugs.ttl");
        transtitive(18,20,root + "transitive1/ChemSpider-Chembl13Molecule-via-Chembl13Id.ttl");
        transtitive(3,29,root + "transitive1/ConceptWiki-Chembl13Molecule-via-ChemSpider.ttl");
        transtitive2(15,24,root + "transitive1/ConceptWiki-Chembl13Targets-via-Swissprot.ttl");
        transtitive(3,25,root + "transitive1/ConceptWiki-Chembl2Compounds-via-ChemSpider.ttl");
        transtitive(3,27,root + "transitive1/ConceptWiki-DrugBankDrugs-via-ChemSpider.ttl");
    }

}
