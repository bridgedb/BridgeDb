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
package org.bridgedb.uri.loader.transative;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.bridgedb.uri.lens.Lens;
import org.bridgedb.uri.loader.transative.constant.ChemInf;
import org.bridgedb.uri.loader.transative.constant.OboConstants;
import org.bridgedb.utils.BridgeDBException;

/**
 *
 * @author Christian
 */
public class JustificationMaker {

    public static Set<String> PARENT_CHILD = new HashSet<String>(Arrays.asList(
            ChemInf.hasStereoundefinedParent, 
            ChemInf.hasOPSNormalizedCounterpart, 
            ChemInf.hasIsotopicallyUnspecifiedParent,
            ChemInf.hasUnchargedCounterpart,
            ChemInf.hasComponentWithUnchargedCounterpart,
            ChemInf.hasMajorTautomerAtpH7point4,
            OboConstants.HAS_PART,
            OboConstants.IS_TAUTOMER_OF,
            OboConstants.HAS_FUNCTIONAL_PARENT
            ));

    public static Set<String> CROSS_TYPE = new HashSet<String>(Arrays.asList(
            ChemInf.PROTEIN_CODING_GENE,
            ChemInf.FUNCTIONAL_RNA_CODING_GENE
            ));
    
    private static String CW_GENE_HACK = "http://example.com/ConceptWikiGene";
    private static String CW_PROTEIN_HACK = "http://example.com/ConceptWikiProtein";

    public static String combine(String left, String right) throws BridgeDBException{
        String result = possibleCombine(left, right);
        if (result != null){
            return result;
        }
        throw new BridgeDBException("unable to combine " + left + " with " + right);
    }
    
    public static String possibleCombine(String left, String right) {
        if (left.equals(right)){
             if (CROSS_TYPE.contains(left)){
                 return null; //We don't want to tranitive with two cross type even if they are the same.
             }
             return left;
        } else if (left.equals(ChemInf.CHEMICAL_ENTITY)) {
            if (right.equals(ChemInf.INCHI_KEY)) {
                return ChemInf.CHEMICAL_ENTITY;
            } else if (PARENT_CHILD.contains(right)){
                return right;
            } else {
                return null;
            }
        } else if (left.equals(ChemInf.FUNCTIONAL_RNA_CODING_GENE)) {
            if (right.equals(ChemInf.PROTEIN)) {
                return ChemInf.FUNCTIONAL_RNA_CODING_GENE;
            } else if (right.equals(ChemInf.GENE)) {
                return ChemInf.FUNCTIONAL_RNA_CODING_GENE;
            } else {
                return null;
            }
        } else if (left.equals(ChemInf.GENE)) {
           if (right.equals(ChemInf.PROTEIN_CODING_GENE)) {
                return ChemInf.PROTEIN_CODING_GENE;
            } else if (right.equals(ChemInf.FUNCTIONAL_RNA_CODING_GENE)) {
                return ChemInf.FUNCTIONAL_RNA_CODING_GENE;
            } else {
                return null;
            }
        } else if (left.equals(ChemInf.INCHI_KEY)) {
            if (right.equals(ChemInf.CHEMICAL_ENTITY)) {
                return ChemInf.CHEMICAL_ENTITY;
            } else if (PARENT_CHILD.contains(right)){
                return right;
            } else {
                return null;
            }
        } else if (left.equals(ChemInf.PROTEIN)) {
            if (right.equals(ChemInf.PROTEIN_CODING_GENE)) {
                return ChemInf.PROTEIN_CODING_GENE;
            } else if (right.equals(ChemInf.FUNCTIONAL_RNA_CODING_GENE)) {
                return ChemInf.FUNCTIONAL_RNA_CODING_GENE;
            } else if (right.equals(CW_GENE_HACK)) {
                return CW_GENE_HACK;
            } else if (right.equals(CW_PROTEIN_HACK)) {
                return CW_PROTEIN_HACK;
            } else {
                return null;
            }
        } else if (left.equals(ChemInf.PROTEIN_CODING_GENE)) {
            if (right.equals(ChemInf.PROTEIN)) {
                return ChemInf.PROTEIN_CODING_GENE;
            } else if (right.equals(ChemInf.GENE)) {
                return ChemInf.PROTEIN_CODING_GENE;
            } else if (right.equals(CW_GENE_HACK)) {
                return CW_GENE_HACK;
            } else if (right.equals(CW_PROTEIN_HACK)) {
                return CW_PROTEIN_HACK;
            } else {
                return null;
            }
         } else if (PARENT_CHILD.contains(left)) {
         	if (right.equals(ChemInf.INCHI_KEY)) {
                return left;
            } else if (right.equals(ChemInf.CHEMICAL_ENTITY)) {
                return left;
            } else {
                return null;
            }
        } else if (left.equals(CW_GENE_HACK)){
            if (right.equals(ChemInf.PROTEIN)) {
                return CW_GENE_HACK;
            } else if (right.equals(ChemInf.PROTEIN_CODING_GENE)) {
                return CW_GENE_HACK;
            } else {
                return null;
            }
        } else if (left.equals(CW_PROTEIN_HACK)){
            if (right.equals(ChemInf.PROTEIN)) {
                return CW_PROTEIN_HACK;
            } else if (right.equals(ChemInf.PROTEIN_CODING_GENE)) {
                return CW_PROTEIN_HACK;
            } else {
                return null;
            }
        } else if (left.equals(Lens.getTestJustifictaion())){
            if (right.startsWith(Lens.getTestJustifictaion())){
                return right;
            } else {
                return null;
            }
        } else if (left.startsWith(Lens.getTestJustifictaion())){
            if (right.equals(Lens.getTestJustifictaion())){
                return left;
            } else {
                return null;
            }
        }
        return null;
    }
}
