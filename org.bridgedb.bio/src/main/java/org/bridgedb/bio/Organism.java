// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
// Copyright 2006-2009 BridgeDb developers
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
package org.bridgedb.bio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;

/**
 * enum representing organisms understood by PathVisio.
 * Handles conversion from full bionominal name to common name and short code.
 * Still work in progress, currently not used everywhere it could be used.
 * <p>
 * TODO: make extensible
 * TODO: use static initializer - static {...} - instead of multiple initMappings calls...
 * TODO: link to taxonomy, e.g., using int constructor arg.; and new method: public Xref getTaxonomy(){...}
 */
public enum Organism {

	Unspecified("Unspecified", "Uns", 0),

	AcetobacteriumWoodii("Acetobacterium woodii", "Aw", 33952),
	AnophelesGambiae("Anopheles gambiae", "Ag", "Mosquito", 7165),
	ArabidopsisThaliana("Arabidopsis thaliana", "At", 3702),
	Aspergillusniger("Aspergillus niger", "An", "Black mold", 5061),
	BacillusSubtilis("Bacillus subtilis", "Bs", 1423),
	BetaVulgaris("Beta vulgaris", "Bv", "Sugar Beet", 3555),
	BrachypodiumDistachyon("Brachypodium distachyon", "Bd", 15368),
	BrassicaNapus("Brassica napus", "Bn", 3708),
	BosTaurus("Bos taurus", "Bt", "Cow", 9913),
	CaenorhabditisElegans("Caenorhabditis elegans", "Ce", "Worm", 6239),
	CanisFamiliaris("Canis familiaris", "Cf", "Dog", 9615),
	CionaIntestinalis("Ciona intestinalis", "Ci", "Sea Squirt", 7719),
	Clostridiumthermocellum("Clostridium thermocellum", "Ct", "Cthe", 1515),
	Caulobactervibrioides("Caulobacter vibrioides", "Cv", 155892),
	DanioRerio("Danio rerio", "Dr", "Zebra fish", 7955),
	DaphniaMagna("Daphnia magna", "Da", 35525),
	DaphniaPulex("Daphnia pulex", "Dp", 6669),
	DasypusNovemcinctus("Dasypus novemcinctus", "Dn", "Armadillo", 9361),
	DrosophilaMelanogaster("Drosophila melanogaster", "Dm", "Fruit fly", 7227),
	EscherichiaColi("Escherichia coli", "Ec", 562),	
	EchinposTelfairi ("Echinops telfairi", "Et", "Hedgehog", 9371),

	//NB: two-letter code is Qc to disambiguate from E. coli	
	EquusCaballus("Equus caballus", "Qc", "Horse", 9796),
	
	GallusGallus("Gallus gallus", "Gg", "Chicken", 9031),
	GibberellaZeae("Gibberella zeae", "Gz", "Fusarium graminearum", 5518),
	GlycineMax("Glycine max", "Gm", "Soybean", 3847),
	HomoSapiens("Homo sapiens", "Hs", "Human", 9606),
	HordeumVulgare("Hordeum vulgare", "Hv", "Barley", 4513),
	LoxodontaAfricana ("Loxodonta africana", "La", "Elephant", 9785),
	MacacaMulatta ("Macaca mulatta", "Ml", "Rhesus Monkey", 9544),
	MonodelphisDomestica  ("Monodelphis domestica", "Md", "Opossum", 13616),
	MusMusculus("Mus musculus", "Mm", "Mouse", 10090),
	MycobacteriumTuberculosis ("Mycobacterium tuberculosis", "Mx", "Tuberculosis", 1773),
	OrnithorhynchusAnatinus	("Ornithorhynchus anatinus", "Oa", "Platypus", 9258),
	OryctolagusCuniculus  ("Oryctolagus cuniculus", "Oc", "Rabbit", 9986),
	OryzaSativa("Oryza sativa", "Os", "Rice", 4530),
	OryzaJaponica("Oryza japonica", "Oj", "Rice"),
	OryzaSativaJaponica("Oryza sativa Japonica Group", "Osj", "Rice", 39947),
	OryzaIndica("Oryza indica", "Oi", "Indian Rice"),
	OryzaSativaIndica("Oryza sativa Indica Group", "Osi", "Indian Rice", 39946),
	OryziasLatipes ("Oryzias latipes", "Ol", "Medaka Fish", 8090),
	OvisAries("Ovis aries", "Ova", "Sheep", 9940),
	PanTroglodytes("Pan troglodytes", "Pt", "Chimpanzee", 9598),
	PlasmodiumFalciparum("Plasmodium falciparum", "Pf", "Malaria Parasite", 5833),
	
	//NB: two-letter code is Pi to disambiguate from Pan troglodytes	
	PopulusTrichocarpa("Populus trichocarpa", "Pi", "Western Balsam Poplar", 3694),

	RattusNorvegicus("Rattus norvegicus", "Rn", "Rat", 10116),
	SaccharomycesCerevisiae("Saccharomyces cerevisiae", "Sc", "Yeast", 4932),
	SolanumLycopersicum("Solanum lycopersicum", "Sl", "Tomato", 4081),
	SorexAraneus ("Sorex araneus", "Sa", "Shrew", 42254),
	SorghumBicolor ("Sorghum bicolor", "Sb", "Sorghum", 4558),
	SusScrofa("Sus scrofa", "Ss", "Pig", 9823),
	TetraodonNigroviridis ("Tetraodon nigroviridis", "Tn", "Pufferfish", 99883),
	TriticumAestivum ("Triticum aestivum", "Ta", "Wheat", 4565),
	VitisVinifera ("Vitis vinifera", "Vv", "Wine Grape", 29760),
	XenopusTropicalis("Xenopus tropicalis", "Xt", "Frog", 8364),
	ZeaMays ("Zea mays", "Zm", "Maize", 4577),
	;
	
	private String latinName;
	private String code;
	private String shortName;
	private Xref   taxonomyID;

	Organism(String latinName, String code) {
		this(latinName, code, latinName);
	}

	Organism(String latinName, String code, String shortName) {
		this(latinName, code, shortName, -1);
	}

	Organism(String latinName, String code, int taxonomyRef) {
		this(latinName, code, latinName, taxonomyRef);
	}

	Organism(String latinName, String code, String shortName, int taxonomyRef) {
		this.latinName = latinName;
		this.code = code;
		this.shortName = shortName;
		if (taxonomyRef > 0) {
            DataSource taxonomyDS;
            if (DataSource.fullNameExists("NCBI Taxonomy Database")){
                taxonomyDS = DataSource.getExistingByFullName("NCBI Taxonomy Database");
            } else {
				taxonomyDS = DataSource.register(
					"Tn", "NCBI Taxonomy Database"
				).asDataSource();
			}
			this.taxonomyID = new Xref("" + taxonomyRef, taxonomyDS);
		}
	}

	public String code() { return code; }
	public String latinName() { return latinName; }
	public String shortName() { return shortName; }
	public Xref taxonomyID() { return taxonomyID; }

	private static Map<Integer, Organism> byTaxonomyID;
	private static Map<String, Organism> byCode;
	private static Map<String, Organism> byLatinName;
	private static Map<String, Organism> byShortName;
	private static List<String> latinNames;
	private static String[] codes;

	public static Organism fromTaxonomyId(int taxid) {
		if(byTaxonomyID == null) initMappings();
		return byTaxonomyID.get(taxid);
	};

	public static Organism fromCode(String code) {
		if(byCode == null) initMappings();
		return byCode.get(code);
	}
	
	public static Organism fromShortName(String shortName) {
		if(byShortName == null) initMappings();
		return byShortName.get(shortName);
	}
	
	public static List<String> latinNames() {
		if(latinNames == null) initMappings();
		return latinNames;
	}
	
	public static String[] codes() {
		if(codes == null) initMappings();
		return codes;
	}
	
	public static String[] latinNamesArray() {
		String[] nms = new String[latinNames().size()];
		return latinNames.toArray(nms);
	}
	
	public static Organism fromLatinName(String latinName) {
		if(byLatinName == null) initMappings();
		return byLatinName.get(latinName);
	}
	
	private static void initMappings() {
		byTaxonomyID = new HashMap<Integer, Organism>();
		byCode = new HashMap<String, Organism>();
		byLatinName = new HashMap<String, Organism>();
		byShortName = new HashMap<String, Organism>();
		for(Organism o : values()) {
			if (o.taxonomyID() != null) {
				byTaxonomyID.put(Integer.valueOf(o.taxonomyID().getId()), o);
			}
			byCode.put(o.code, o);
			byLatinName.put(o.latinName, o);
			byShortName.put(o.shortName, o);
		}
		
		latinNames = new ArrayList<String>(byLatinName.keySet());
		Collections.sort(latinNames);
		
		codes = new String[latinNames().size()];
		String[] latinNames = latinNamesArray();
		for(int i = 0; i < latinNames.length; i++) {
			codes[i] = fromLatinName(latinNames[i]).code;
		}
	}
}
