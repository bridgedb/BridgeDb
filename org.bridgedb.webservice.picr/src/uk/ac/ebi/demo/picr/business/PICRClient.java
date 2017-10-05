package uk.ac.ebi.demo.picr.business;

import uk.ac.ebi.demo.picr.soap.AccessionMapperService;
import uk.ac.ebi.demo.picr.soap.AccessionMapperInterface;
import uk.ac.ebi.demo.picr.soap.BlastParameter;
import uk.ac.ebi.demo.picr.soap.UPEntry;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 *    Copyright 2007 - European Bioinformatics Institute
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 * User: rcote
 * User: Sam Wein
 * Date: 17-May-2007
 * Time: 10:58:03
 * $Id: $
 */
public class PICRClient {

    //boolean flag used to communicate with PICR webservice query methods
    private boolean onlyActive = false;

    public boolean isOnlyActive() {
        return onlyActive;
    }

    public void setOnlyActive(boolean onlyActive) {
        this.onlyActive = onlyActive;
    }

    /**
     * Calls PICR webservice and returns a list of UPEntries based on accession
     * @param accession - the accession to map (may include version)
     * @param databases - a list of databases to map to
     * @return a list of UPEntry objects. List can be empty but not null.
     */
    public List<UPEntry> performAccessionMapping(String accession, Object[] databases) {

        //get service
        AccessionMapperService service = new AccessionMapperService();
        AccessionMapperInterface port;
        port = service.getAccessionMapperPort();

        String ac_version = null;
        int ndx = accession.indexOf('.');
        if (ndx > 0) {
            ac_version = accession.substring(ndx + 1);
            accession = accession.substring(0, ndx);
        }

        //a list of all databases to map to
        List<String> searchDB = null;
        if (databases != null) {
            searchDB = new ArrayList<String>();
            for (int i = 0; i < databases.length; i++) {
                searchDB.add(databases[i].toString());
            }
        }

        return port.getUPIForAccession(accession, ac_version, searchDB, null, onlyActive);

    }

    /**
     * Calls PICR webservice and returns a list of UPEntries based on sequence criteria
     *
     * @param sequence  - the FASTA sequence to map
     * @param databases - a list of databases to map to
     * @return a list of UPEntry objects. List can be empty but not null.
     */
    public List<UPEntry> performSequenceMapping(String sequence, Object[] databases) {

        //get service
        AccessionMapperService service = new AccessionMapperService();
        AccessionMapperInterface port;
        port = service.getAccessionMapperPort();

        //a list of all databases to map to
        List<String> searchDB = null;
        if (databases != null) {
            searchDB = new ArrayList<String>();
            for (int i = 0; i < databases.length; i++) {
                searchDB.add(databases[i].toString());
            }
        }

        UPEntry entry = port.getUPIForSequence(sequence, searchDB, null, onlyActive);
        if (entry != null) {
            return Collections.singletonList(entry);
        } else {
            return Collections.emptyList();
        }

    }

    /**
     * calls PICR webservice and returns a list<String> of available databases
     * @return a list of strings. List can be empty but not null.
     */
    public List<String> loadDatabases() {

        //get service
        AccessionMapperService service = new AccessionMapperService();
        AccessionMapperInterface port;
        port = service.getAccessionMapperPort();

        return port.getMappedDatabaseNames();

    }

    public List<UPEntry> performBlastMapping(String sequence, Object[] databases, String identityValue, String identityTaxon, String filterType, String blastDB, String taxonId, boolean onlyActive, BlastParameter blastParameter){
         //get service
        AccessionMapperService service = new AccessionMapperService();
        AccessionMapperInterface port;
        port = service.getAccessionMapperPort();

        //a list of all databases to map to
        List<String> searchDB = null;
        if (databases != null) {
            searchDB = new ArrayList<String>();
            for (int i = 0; i < databases.length; i++) {
                searchDB.add(databases[i].toString());
            }
        }

        List<UPEntry> entry = port.getUPIForBlastSequence(sequence, searchDB, identityValue, identityTaxon,filterType, blastDB, taxonId, onlyActive,blastParameter);
        if (entry != null) {
            return entry;
        } else {
            return Collections.emptyList();
        }

    }
}
