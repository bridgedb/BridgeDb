/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset.constants;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author Christian
 */
public enum FrequencyOfChange implements HasURI {
    TRIENIAL ("triennial"),
    BIENNIAL ("biennial"),
    ANNUAL ("annual"),
    SEMI_ANNUAL ("semiannual"),
    THREE_TIMES_A_YEAR ("threeTimesAYear"),
    QUARTERLY ("quarterly"),
    BIMONTHLY ("bimonthly"),
    MONTHLY ("monthly"),
    SEMIMONTHLY ("semimonthly"),
    BIWEEKLY ("biweekly"),
    THREE_TIMES_A_MONTH ("threeTimesAMonth"),
    WEEKLY ("weekly"),
    SEMI_WEEKLY ("semiweekly"),
    THREE_TIMES_A_WEEK ("threeTimesAWeek"),
    DAILY ("daily"),
    CONTINUOUS ("continuous"),
    IRREGULAR ("irregular"); 
    
    private URI uri;
    
    public static final String NAME_SPACE = "http://purl.org/cld/freq/";
    
    private FrequencyOfChange(String localName){
        uri = new URIImpl (NAME_SPACE + localName);
    }
    
    @Override
    public URI getURI(){
        this.values();
        return uri;
    }
    
}
