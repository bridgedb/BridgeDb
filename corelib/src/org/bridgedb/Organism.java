// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2009 BiGCaT Bioinformatics
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

package org.bridgedb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * enum representing organisms understood by PathVisio.
 * Handles conversion from full bionominal name to common name and short code.
 * Still work in progress, currently not used everywhere it could be used.
 * <p>
 * TODO: this class will move to the org.bridgedb.bio package in the future.
 * <p> 
 * TODO: make extensible
 */
public enum Organism 
{
	AnophelesGambiae("Anopheles gambiae", "Ag", "Mosquito"),
	ArabidopsisThaliana("Arabidopsis thaliana", "At"),
	BacillusSubtilis("Bacillus subtilis", "Bs"),
	BosTaurus("Bos taurus", "Bt", "Cow"),
	CaenorhabditisElegans("Caenorhabditis elegans", "Ce", "Worm"),
	CanisFamiliaris("Canis familiaris", "Cf", "Dog"),
	CionaIntestinalis("Ciona intestinalis", "Ci", "Sea Squirt"),
	DanioRerio("Danio rerio", "Dr", "Zebra fish"),
	DasypusNovemcinctus("Dasypus novemcinctus", "Dn", "Armadillo"),
	DrosophilaMelanogaster("Drosophila melanogaster", "Dm", "Fruit fly"),
	EscherichiaColi("Escherichia coli", "Ec"),	
	EchinposTelfairi ("Echinops telfairi", "Et", "Hedgehog"),

	//NB: two-letter code is Qc to disambiguate from E. coli	
	EquusCaballus("Equus caballus", "Qc", "Horse"),
	
	GallusGallus("Gallus gallus", "Gg", "Chicken"),
	HomoSapiens("Homo sapiens", "Hs", "Human"),
	LoxodontaAfricana ("Loxodonta africana", "La", "Elephant"),
	MacacaMulatta ("Macaca mulatta", "Ml", "Rhesus Monkey"),
	MusMusculus("Mus musculus", "Mm", "Mouse"),
	MonodelphisDomestica  ("Monodelphis domestica", "Md", "Opossum"),
	OrnithorhynchusAnatinus	("Ornithorhynchus anatinus", "Oa", "Platypus"),
	OryzaSativa("Oryza sativa", "Os", "Rice"),
	OryziasLatipes ("Oryzias latipes", "Ol", "Medaka Fish"),
	OryctolagusCuniculus  ("Oryctolagus cuniculus", "Oc", "Rabbit"),
	PanTroglodytes("Pan troglodytes", "Pt", "Chimpanzee"),
	
	//NB: two-letter code is Pi to disambiguate from Pan troglodytes	
	PopulusTrichocarpa("Populus trichocarpa", "Pi", "Western Balsam Poplar"),
	RattusNorvegicus("Rattus norvegicus", "Rn", "Rat"),
	SaccharomycesCerevisiae("Saccharomyces cerevisiae", "Sc", "Yeast"),
	SorexAraneus ("Sorex araneus", "Sa", "Shrew"),
	SorghumBicolor ("Sorghum bicolor", "Sb", "Sorghum"),
	TetraodonNigroviridis ("Tetraodon nigroviridis", "Tn", "Pufferfish"),
	TriticumAestivum ("Triticum aestivum", "Ta", "Wheat"),
	XenopusTropicalis("Xenopus tropicalis", "Xt", "Frog"),
	VitisVinifera ("Vitis vinifera", "Vv", "Wine Grape"),
	ZeaMays ("Zea mays", "Zm", "Maize"),
	;
	
	private String latinName;
	private String code;
	private String shortName;
	
	Organism(String latinName, String code) {
		this(latinName, code, latinName);
	}
	
	Organism(String latinName, String code, String shortName) {
		this.latinName = latinName;
		this.code = code;
		this.shortName = shortName;
	}
	
	public String code() { return code; }
	public String latinName() { return latinName; }
	public String shortName() { return shortName; }
	
	private static Map<String, Organism> byCode;
	private static Map<String, Organism> byLatinName;
	private static Map<String, Organism> byShortName;
	private static List<String> latinNames;
	private static String[] codes;
	
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
		byCode = new HashMap<String, Organism>();
		byLatinName = new HashMap<String, Organism>();
		byShortName = new HashMap<String, Organism>();
		for(Organism o : values()) {
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
