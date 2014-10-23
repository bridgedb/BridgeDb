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

    public static Set<String> CHEMICAL_LENS = new HashSet<String>(Arrays.asList(
            //Child-Child
            ChemInf.isIsotopologueOf,
            ChemInf.isStereoisomerOf,
            OboConstants.IS_TAUTOMER_OF,
            ChemInf.isCounterpartWithDifferentChargeOf,
            ChemInf.sharesOPSNormalizedParentWith,
            //Parent-Child
            ChemInf.hasChemAxonCanonicalisedTautomer,
            ChemInf.isChemAxonCanonicalisedTtautomerOf,
            ChemInf.hasOPSNormalizedCounterpart, 
            ChemInf.isOPSNormalizedCounterpartOf,
            ChemInf.hasStereoundefinedParent, 
            ChemInf.isStereoundefinedParentOf,
            ChemInf.hasIsotopicallyUnspecifiedParent,
            ChemInf.isIsotopicallyUnspecifiedParentOf,
            ChemInf.hasUnchargedCounterpart,
            ChemInf.isUnchargedCounterpartOf,
            ChemInf.hasComponentWithUnchargedCounterpart,
            ChemInf.isComponentWithUnchargedCounterpartOf,
            //Other not in version 1_4_2
            ChemInf.hasMajorTautomerAtpH7point4,
            OboConstants.HAS_PART,
            OboConstants.HAS_FUNCTIONAL_PARENT
            ));

    private static final String CW_GENE_HACK = "http://example.com/ConceptWikiGene";
    private static final String CW_PROTEIN_HACK = "http://example.com/ConceptWikiProtein";
    private static final String ENEMBL_BASED_PROTIEN_GENE_HACK = "http://example.com/EnsemblBasedProteinGene";
    public static final String  PROTEIN_CODING_GENE = "http://semanticscience.org/resource/SIO_000985";
    
    public static Set<String> CROSS_TYPE = new HashSet<String>(Arrays.asList(
            ChemInf.PROTEIN_CODING_GENE,
            ChemInf.FUNCTIONAL_RNA_CODING_GENE,
            ENEMBL_BASED_PROTIEN_GENE_HACK,
            ChemInf.DATABASE_CROSS_REFERENCE
            ));
    
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
            } else if (CHEMICAL_LENS.contains(right)){
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
            } else if (right.equals(ENEMBL_BASED_PROTIEN_GENE_HACK)) {
                return ENEMBL_BASED_PROTIEN_GENE_HACK;    
            } else {
                return null;
            }
        } else if (left.equals(ChemInf.INCHI_KEY)) {
            if (right.equals(ChemInf.CHEMICAL_ENTITY)) {
                return ChemInf.CHEMICAL_ENTITY;
            } else if (CHEMICAL_LENS.contains(right)){
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
            } else if (right.equals(ENEMBL_BASED_PROTIEN_GENE_HACK)) {
                return ENEMBL_BASED_PROTIEN_GENE_HACK;      
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
            //NOT as we dont link two protein coding gene ENEMBL_BASED_PROTIEN_GENE_HACK
        } else if (left.equals(ENEMBL_BASED_PROTIEN_GENE_HACK)) {
             if (right.equals(ChemInf.PROTEIN)) {
                return ENEMBL_BASED_PROTIEN_GENE_HACK;
            } else if (right.equals(ChemInf.GENE)) {
                return ENEMBL_BASED_PROTIEN_GENE_HACK;
            } else {
                return null;
            }
            //Not CW HACK as we don't link CW and Enemble based transitively
         } else if (CHEMICAL_LENS.contains(left)) {
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
            //Not ENEMBL_BASED_PROTIEN_GENE_HACK as we don't link CW and Enemble based transitively
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
        System.out.println("not found " + left);
        return null;
    }
    
    public static String getInverse(String justification) {
        if (justification.equals(ChemInf.isUnchargedCounterpartOf)){
            return ChemInf.hasUnchargedCounterpart;
        }
        if (justification.equals(ChemInf.hasUnchargedCounterpart)){
            return ChemInf.isUnchargedCounterpartOf;
        }
        if (justification.equals(ChemInf.hasIsotopicallyUnspecifiedParent)){
            return ChemInf.isIsotopicallyUnspecifiedParentOf;
        }
        if (justification.equals(ChemInf.isIsotopicallyUnspecifiedParentOf)){
            return ChemInf.hasIsotopicallyUnspecifiedParent;
        }
        if (justification.equals(ChemInf.hasStereoundefinedParent)){
            return ChemInf.isStereoundefinedParentOf;
        }
        if (justification.equals(ChemInf.isStereoundefinedParentOf)){
            return ChemInf.hasStereoundefinedParent;
        }
        if (justification.equals(ChemInf.hasOPSNormalizedCounterpart)){
            return ChemInf.isOPSNormalizedCounterpartOf;
        }
        if (justification.equals(ChemInf.isOPSNormalizedCounterpartOf)){
            return ChemInf.hasOPSNormalizedCounterpart;
        }
        if (justification.equals(OboConstants.PART_OF)){
            return OboConstants.HAS_PART;
        }
        if (justification.equals(OboConstants.HAS_PART)){
            return OboConstants.PART_OF;
        }
        //if (justification.equals("http://example.com/ConceptWikiGene")){
        //    return PROTEIN_CODING_GENE;
        //}
        //if (justification.equals("http://example.com/ConceptWikiProtein")){
        //    return PROTEIN_CODING_GENE;
        //}
        if (justification.equals(Lens.getTestJustifictaion() + "Forward")){
            return Lens.getTestJustifictaion() + "BackWard";
        }
        if (justification.equals(Lens.getTestJustifictaion() + "BackWard")){
            return Lens.getTestJustifictaion() + "Forward";
        }
        return justification;
    }
    
    public static String getForward(String justification) {
        if (justification.equals("http://example.com/ConceptWikiGene")){
            return PROTEIN_CODING_GENE;
        }
        if (justification.equals("http://example.com/ConceptWikiProtein")){
            return PROTEIN_CODING_GENE;
        }
        return justification;
    }

}
