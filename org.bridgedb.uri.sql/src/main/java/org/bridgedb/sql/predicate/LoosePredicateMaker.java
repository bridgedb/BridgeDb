// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009  BridgeDb developers
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  OpenPhacts
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.bridgedb.sql.predicate;

import org.bridgedb.rdf.constants.OWLConstants;
import org.bridgedb.rdf.constants.OboConstants;
import org.bridgedb.rdf.constants.RdfSchemaConstants;
import org.bridgedb.rdf.constants.SkosConstants;
import org.bridgedb.utils.BridgeDBException;

/**
 *
 * @author Christian
 */
public class LoosePredicateMaker implements PredicateMaker{
//Broaawed + narrow = error
    
    private static final LoosePredicateMaker instance = new LoosePredicateMaker();
    
    private LoosePredicateMaker(){
    }
    
    public static LoosePredicateMaker getInstance(){
        return instance;
    }
    
    public static void init() throws BridgeDBException{
        //Currently does nothing but this is where you would read a proerties file.
    }
    
    @Override
    public String combine(String left, String right) throws BridgeDBException {
        String result = possibleCombine(left, right);
        if (result == null){
            throw new BridgeDBException("unable to combine " + left + " with " + right);
        } else {
            return result;
        }

    }
    
    @Override
    public String possibleCombine(String left, String right) {
        if (left.equals(right)){
            return left;
        }
        left = cleanup(left);
        right = cleanup(right);
        if (left.equals(right)){
            return left;
        }
        if (left.equals(RdfSchemaConstants.SEE_ALSO)) {
            return RdfSchemaConstants.SEE_ALSO;
        }
        if (right.equals(RdfSchemaConstants.SEE_ALSO)) {
            return RdfSchemaConstants.SEE_ALSO;
        }
        if (left.equals(OWLConstants.SAME_AS)) {
            if (right.equals(OWLConstants.EQUIVALENT_CLASS)) return OWLConstants.EQUIVALENT_CLASS;
            if (right.equals(SkosConstants.EXACT_MATCH)) return SkosConstants.EXACT_MATCH;
            if (right.equals(SkosConstants.CLOSE_MATCH)) return SkosConstants.CLOSE_MATCH;
            if (right.equals(SkosConstants.MAPPING_RELATION)) return SkosConstants.MAPPING_RELATION;
            if (right.equals(SkosConstants.RELATED_MATCH)) return SkosConstants.RELATED_MATCH;
            if (right.equals(SkosConstants.NARROW_MATCH)) return SkosConstants.NARROW_MATCH;
            if (right.equals(SkosConstants.BROAD_MATCH)) return SkosConstants.BROAD_MATCH;  
            if (right.equals(OboConstants.HAS_PART)) return OboConstants.HAS_PART;
        }
        if (left.equals(OWLConstants.EQUIVALENT_CLASS)) {
            if (right.equals(OWLConstants.SAME_AS)) return OWLConstants.EQUIVALENT_CLASS;
            if (right.equals(SkosConstants.EXACT_MATCH)) return SkosConstants.EXACT_MATCH;
            if (right.equals(SkosConstants.CLOSE_MATCH)) return SkosConstants.CLOSE_MATCH;
            if (right.equals(SkosConstants.MAPPING_RELATION)) return SkosConstants.MAPPING_RELATION;
            if (right.equals(SkosConstants.RELATED_MATCH)) return SkosConstants.RELATED_MATCH;
            if (right.equals(SkosConstants.NARROW_MATCH)) return SkosConstants.NARROW_MATCH;
            if (right.equals(SkosConstants.BROAD_MATCH)) return SkosConstants.BROAD_MATCH; 
            if (right.equals(OboConstants.HAS_PART)) return OboConstants.HAS_PART;
        }
        if (left.equals(SkosConstants.EXACT_MATCH)) { //TODO keep other
            if (right.equals(OWLConstants.EQUIVALENT_CLASS)) return SkosConstants.EXACT_MATCH;
            if (right.equals(OWLConstants.SAME_AS)) return SkosConstants.EXACT_MATCH;
            if (right.equals(SkosConstants.CLOSE_MATCH)) return SkosConstants.CLOSE_MATCH;
            if (right.equals(SkosConstants.MAPPING_RELATION)) return SkosConstants.MAPPING_RELATION;
            if (right.equals(SkosConstants.RELATED_MATCH)) return SkosConstants.MAPPING_RELATION;
            if (right.equals(SkosConstants.NARROW_MATCH)) return SkosConstants.MAPPING_RELATION;
            if (right.equals(SkosConstants.BROAD_MATCH)) return SkosConstants.MAPPING_RELATION;    
            if (right.equals(OboConstants.HAS_PART)) return OboConstants.HAS_PART;
        }
        if (left.equals(SkosConstants.CLOSE_MATCH)) {
            if (right.equals(OWLConstants.EQUIVALENT_CLASS)) return SkosConstants.CLOSE_MATCH;
            if (right.equals(OWLConstants.SAME_AS)) return SkosConstants.CLOSE_MATCH;
            if (right.equals(SkosConstants.EXACT_MATCH)) return SkosConstants.CLOSE_MATCH;
            if (right.equals(SkosConstants.MAPPING_RELATION)) return SkosConstants.MAPPING_RELATION;
            if (right.equals(SkosConstants.RELATED_MATCH)) return SkosConstants.MAPPING_RELATION;
            if (right.equals(SkosConstants.NARROW_MATCH)) return SkosConstants.MAPPING_RELATION;
            if (right.equals(SkosConstants.BROAD_MATCH)) return SkosConstants.MAPPING_RELATION;    
            if (right.equals(OboConstants.HAS_PART)) return OboConstants.HAS_PART;
        }
        if (left.equals(SkosConstants.MAPPING_RELATION)) {
            if (right.equals(OWLConstants.EQUIVALENT_CLASS)) return SkosConstants.MAPPING_RELATION;
            if (right.equals(OWLConstants.SAME_AS)) return SkosConstants.MAPPING_RELATION;
            if (right.equals(SkosConstants.EXACT_MATCH)) return SkosConstants.MAPPING_RELATION;
            if (right.equals(SkosConstants.CLOSE_MATCH)) return SkosConstants.MAPPING_RELATION;
            if (right.equals(SkosConstants.RELATED_MATCH)) return SkosConstants.MAPPING_RELATION;
            if (right.equals(SkosConstants.NARROW_MATCH)) return SkosConstants.MAPPING_RELATION;
            if (right.equals(SkosConstants.BROAD_MATCH)) return SkosConstants.MAPPING_RELATION;    
        }
        //Left != Right as that has been handled above
        //So Borad, Narrow and mapping relation all go up to mapping Relation
        if (left.equals(SkosConstants.RELATED_MATCH) || left.equals(SkosConstants.BROAD_MATCH) || 
                left.equals(SkosConstants.NARROW_MATCH)) {
            if (right.equals(OWLConstants.EQUIVALENT_CLASS)) return left;
            if (right.equals(OWLConstants.SAME_AS)) return left;
            if (right.equals(SkosConstants.EXACT_MATCH)) return SkosConstants.MAPPING_RELATION;
            if (right.equals(SkosConstants.CLOSE_MATCH)) return SkosConstants.MAPPING_RELATION;
            if (right.equals(SkosConstants.MAPPING_RELATION)) return SkosConstants.MAPPING_RELATION;
            if (right.equals(SkosConstants.RELATED_MATCH)) return SkosConstants.MAPPING_RELATION;
            if (right.equals(SkosConstants.NARROW_MATCH)) return SkosConstants.MAPPING_RELATION;
            if (right.equals(SkosConstants.BROAD_MATCH)) return SkosConstants.MAPPING_RELATION;    
        }
        if (left.equals(OboConstants.HAS_PART)) {
        	if (right.equals(OWLConstants.EQUIVALENT_CLASS)) return OboConstants.HAS_PART;
        	if (right.equals(OWLConstants.SAME_AS)) return OboConstants.HAS_PART;
        	if (right.equals(SkosConstants.EXACT_MATCH)) return OboConstants.HAS_PART;
        	if (right.equals(SkosConstants.CLOSE_MATCH)) return OboConstants.HAS_PART;
        }
        return null;
    }
    
    private static String cleanup(String predicate){
        if (predicate.equals(SkosConstants.BROADER)){
            return SkosConstants.BROAD_MATCH;
        }
        if (predicate.equals(SkosConstants.NARROWER)){
            return SkosConstants.NARROW_MATCH;
        }
        if (predicate.equals(SkosConstants.RELATED)){
            return SkosConstants.RELATED_MATCH;
        }
        return predicate;
     }
}
