// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright      2012  Christian Y. A. Brenninkmeijer
// Copyright      2012  OpenPhacts
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
package org.bridgedb.linkset.transative;

import org.bridgedb.linkset.constants.OwlConstants;
import org.bridgedb.linkset.constants.SkosConstants;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFHandlerException;

/**
 *
 * @author Christian
 */
public class PredicateMaker {

    public static Value combine(Value left, Value right) throws RDFHandlerException{
        if (left.equals(right)){
            return left;
        }
        if (left.equals(OwlConstants.SAME_AS)) {
            if (right.equals(OwlConstants.EQUIVALENT_CLASS)) return OwlConstants.EQUIVALENT_CLASS;
            if (right.equals(SkosConstants.EXACT_MATCH)) return SkosConstants.EXACT_MATCH;
            if (right.equals(SkosConstants.CLOSE_MATCH)) return SkosConstants.CLOSE_MATCH;
            if (right.equals(SkosConstants.MAPPING_RELATION)) return SkosConstants.MAPPING_RELATION;
            if (right.equals(SkosConstants.NARROW_MATCH)) return SkosConstants.NARROW_MATCH;
            if (right.equals(SkosConstants.BROAD_MATCH)) return SkosConstants.BROAD_MATCH;    
        }
        if (left.equals(OwlConstants.EQUIVALENT_CLASS)) {
            if (right.equals(OwlConstants.SAME_AS)) return OwlConstants.EQUIVALENT_CLASS;
            if (right.equals(SkosConstants.EXACT_MATCH)) return SkosConstants.EXACT_MATCH;
            if (right.equals(SkosConstants.CLOSE_MATCH)) return SkosConstants.CLOSE_MATCH;
            if (right.equals(SkosConstants.MAPPING_RELATION)) return SkosConstants.MAPPING_RELATION;
            if (right.equals(SkosConstants.NARROW_MATCH)) return SkosConstants.NARROW_MATCH;
            if (right.equals(SkosConstants.BROAD_MATCH)) return SkosConstants.BROAD_MATCH;    
        }
        if (left.equals(SkosConstants.EXACT_MATCH)) {
            if (right.equals(OwlConstants.EQUIVALENT_CLASS)) return SkosConstants.EXACT_MATCH;
            if (right.equals(OwlConstants.SAME_AS)) return SkosConstants.EXACT_MATCH;
            if (right.equals(SkosConstants.CLOSE_MATCH)) return SkosConstants.CLOSE_MATCH;
            if (right.equals(SkosConstants.MAPPING_RELATION)) return SkosConstants.MAPPING_RELATION;
            if (right.equals(SkosConstants.NARROW_MATCH)) return SkosConstants.NARROW_MATCH;
            if (right.equals(SkosConstants.BROAD_MATCH)) return SkosConstants.BROAD_MATCH;    
        }
        if (left.equals(SkosConstants.CLOSE_MATCH)) {
            if (right.equals(OwlConstants.EQUIVALENT_CLASS)) return SkosConstants.CLOSE_MATCH;
            if (right.equals(OwlConstants.SAME_AS)) return SkosConstants.CLOSE_MATCH;
            if (right.equals(SkosConstants.EXACT_MATCH)) return SkosConstants.CLOSE_MATCH;
            if (right.equals(SkosConstants.MAPPING_RELATION)) return SkosConstants.MAPPING_RELATION;
            if (right.equals(SkosConstants.NARROW_MATCH)) return SkosConstants.NARROW_MATCH;
            if (right.equals(SkosConstants.BROAD_MATCH)) return SkosConstants.BROAD_MATCH;    
        }
        //Left != Right as that has been handled above
        //So Borad, Narrow and mapping relation all go up to mapping Relation
        if (left.equals(SkosConstants.MAPPING_RELATION) || left.equals(SkosConstants.BROAD_MATCH) || 
                left.equals(SkosConstants.NARROW_MATCH)) {
            if (right.equals(OwlConstants.EQUIVALENT_CLASS)) return SkosConstants.MAPPING_RELATION;
            if (right.equals(OwlConstants.SAME_AS)) return SkosConstants.MAPPING_RELATION;
            if (right.equals(SkosConstants.EXACT_MATCH)) return SkosConstants.MAPPING_RELATION;
            if (right.equals(SkosConstants.CLOSE_MATCH)) return SkosConstants.MAPPING_RELATION;
            if (right.equals(SkosConstants.MAPPING_RELATION)) return SkosConstants.MAPPING_RELATION;
            if (right.equals(SkosConstants.NARROW_MATCH)) return SkosConstants.MAPPING_RELATION;
            if (right.equals(SkosConstants.BROAD_MATCH)) return SkosConstants.MAPPING_RELATION;    
        }
        throw new RDFHandlerException("unable to combine " + left + " with " + right);
    }
    
}
