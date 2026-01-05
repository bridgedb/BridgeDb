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

	AcerPseudoplatanus("Acer pseudoplatanus","Ap",4026),
	AcetobacteriumWoodii("Acetobacterium woodii", "Aw", 33952),
	AcetobacterSubgenAcetobacter("Acetobacter subgen. Acetobacter","As",151157),
	AcinetobacterBaylyi("Acinetobacter baylyi","Ab",202950),
	ActinidiaChinensis("Actinidia chinensis","Acc",3625),
	ActinidiaDeliciosa("Actinidia deliciosa","Ad",3627),
	ActinidiaEriantha("Actinidia eriantha","Ae",165200),
	AdiantumCapillusveneris("Adiantum capillus-veneris","Ac",13818),
	AdonisAestivalis("Adonis aestivalis","Ada",113211),
	AdonisAnnua("Adonis annua","Aan",212759),
	AegilopsTauschii("Aegilops tauschii","Ata",37682),
	AlbiziaJulibrissin("Albizia julibrissin","Aj",3813),
	AlliumAflatunense("Allium aflatunense","Aaf",70752),
	AlliumAltaicum("Allium altaicum","Aal",48666),
	AlliumAltyncolicum("Allium altyncolicum","Ala",165602),
	AlliumAmpeloprasum("Allium ampeloprasum","Aam",4681),
	AlliumAscalonicum("Allium ascalonicum","Aas",1476995),
	AlliumCepa("Allium cepa","Ace",4679),
	AlliumChinense("Allium chinense","Ach",130426),
	AlliumFistulosum("Allium fistulosum","Af",35875),
	AlliumNutans("Allium nutans","Anu",138328),
	AlliumOchotense("Allium ochotense","Ao",669879),
	AlliumSativum("Allium sativum","Asa",4682),
	AlliumSchoenoprasum("Allium schoenoprasum","Asc",74900),
	AlliumTuberosum("Allium tuberosum","Atu",4683),
	AlliumUrsinum("Allium ursinum","Au",4684),
	AlliumVictorialis("Allium victorialis","Av",88845),
	AloeArborescens("Aloe arborescens","Aar",45385),
	AloeFerox("Aloe ferox","Afe",117798),
	AmaranthusCruentus("Amaranthus cruentus","Acr",117272),
	AmaranthusHypochondriacus("Amaranthus hypochondriacus","Ah",28502),
	AmorphaFruticosa("Amorpha fruticosa","Afr",48131),
	AnaerotignumPropionicum("Anaerotignum propionicum","Apr",28446),
	AnchusaOfficinalis("Anchusa officinalis","Ano",89630),
	AnethumFoeniculum("Anethum foeniculum","Afo",2849586),
	AnigozanthosPreissii("Anigozanthos preissii","Anp",95948),
	AnisodusAcutangulus("Anisodus acutangulus","Aac",402998),
	AnophelesGambiae("Anopheles gambiae", "Ag", "Mosquito", 7165),
	AnthriscusSylvestris("Anthriscus sylvestris","Asy",48027),
	AntirrhinumMajus("Antirrhinum majus","Am",4151),
	AphelandraSquarrosa("Aphelandra squarrosa","Asq",103766),
	ApiumGraveolens("Apium graveolens","Agr",4045),
	AquifexAeolicus("Aquifex aeolicus","Aae",63363),
	AquilariaCrassna("Aquilaria crassna","Aqc",223751),
	AquilegiaVulgaris("Aquilegia vulgaris","Avu",3451),
	ArabidopsisLyrata("Arabidopsis lyrata","Aly",59689),
	ArabidopsisThaliana("Arabidopsis thaliana", "At", 3702),
	ArachisHypogaea("Arachis hypogaea","Ahy",3818),
	ArchaeoglobusFulgidus("Archaeoglobus fulgidus","Afu",2234),
	AsclepiasSyriaca("Asclepias syriaca","Ass",48545),
	AsparagusOfficinalis("Asparagus officinalis","Aof",4686),
	Aspergillusniger("Aspergillus niger", "An", "Black mold", 5061),
	AspergillusTerricola("Aspergillus terricola","Ate",36642),
	AstragalusBisulcatus("Astragalus bisulcatus","Abi",20406),
	AtractylodesLancea("Atractylodes lancea","Atl",41486),
	AtropaBelladonna("Atropa belladonna","Abe",33113),
	AuxenochlorellaPyrenoidosa("Auxenochlorella pyrenoidosa","Apy",3078),
	AvenaClauda("Avena clauda","Acl",83523),
	AvenaLongiglumis("Avena longiglumis","Alo",4500),
	AvenaProstrata("Avena prostrata","Avp",279683),
	AvenaSativa("Avena sativa","Avs",4498),
	AvenaStrigosa("Avena strigosa","Ast",38783),
	AvenaVentricosa("Avena ventricosa","Ave",146535),
	BacillusAnthracis("Bacillus anthracis","Ba",1392),
	BacillusSubtilis("Bacillus subtilis", "Bs", 1423),
	BarnadesiaSpinosa("Barnadesia spinosa","Bsp",171760),
	BatisMaritima("Batis maritima","Bm",4436),
	BerberisStolonifera("Berberis stolonifera","Bst",33814),
	BetaVulgaris("Beta vulgaris", "Bv", "Sugar Beet", 3555),
	BetulaPubescens("Betula pubescens","Bp",38787),
	BifidobacteriumLongum("Bifidobacterium longum","Bl",206672),
	BixaOrellana("Bixa orellana","Bor",66672),
	BosTaurus("Bos taurus", "Bt", "Cow", 9913),
	BotryococcusBraunii("Botryococcus braunii","Bb",1202541),
	BrachypodiumDistachyon("Brachypodium distachyon", "Bd", 15368),
	BrassicaJuncea("Brassica juncea","Bj",3707),
	BrassicaNapus("Brassica napus", "Bn", 3708),
	BrassicaNigra("Brassica nigra","Bni",3710),
	BrassicaRapa("Brassica rapa","Br",3711),
	BromusInermis("Bromus inermis","Bi",15371),
	BruguieraGymnorhiza("Bruguiera gymnorhiza","Bg",39984),
	CaenorhabditisElegans("Caenorhabditis elegans", "Ce", "Worm", 6239),
	CamelliaIrrawadiensis("Camellia irrawadiensis","Cir",153142),
	CamelliaPtilophylla("Camellia ptilophylla","Cpt",319931),
	CamelliaTaliensis("Camellia taliensis","Cta",182317),
	CamptothecaAcuminata("Camptotheca acuminata","Cac",16922),
	CanavaliaEnsiformis("Canavalia ensiformis","Cen",3823),
	CanavaliaLineata("Canavalia lineata","Cli",28957),
	CanisFamiliaris("Canis familiaris", "Cf", "Dog", 9615),
	CannabisSativa("Cannabis sativa","Csa",3483),
	CapsicumBaccatum("Capsicum baccatum","Cb",33114),
	CapsicumChinense("Capsicum chinense","Cch",80379),
	CapsicumFrutescens("Capsicum frutescens","Cfr",4073),
	CarapicheaIpecacuanha("Carapichea ipecacuanha","Cip",77880),
	CaricaPapaya("Carica papaya","Cp",3649),
	CarpobrotusAcinaciformis("Carpobrotus acinaciformis","Caa",1053334),
	CarthamusTinctorius("Carthamus tinctorius","Cti",4222),
	CarumCarvi("Carum carvi","Cc",48032),
	Caulobactervibrioides("Caulobacter vibrioides", "Cv", 155892),
	CelosiaCristata("Celosia cristata","Ccr",124768),
	CentauriumErythraea("Centaurium erythraea","Cer",172057),
	CephalocereusSenilis("Cephalocereus senilis","Cse",223054),
	CerastiumArvense("Cerastium arvense","Cea",271558),
	CeratodonPurpureus("Ceratodon purpureus","Cpu",3225),
	CereibacterSphaeroides("Cereibacter sphaeroides","Csp",1063),
	CestrumElegans("Cestrum elegans","Cel",103475),
	ChelidoniumMajus("Chelidonium majus","Cm",71251),
	ChlamydiaTrachomatis("Chlamydia trachomatis","Ctr",759363),
	ChrysanthemumxMorifolium("Chrysanthemum x morifolium","Cxm",41568),
	ChrysospleniumAmericanum("Chrysosplenium americanum","Cam",36749),
	CichoriumIntybus("Cichorium intybus","Cin",13427),
	CinchonaCalisaya("Cinchona calisaya","Cca",153742),
	CinchonaMacrocalyx("Cinchona macrocalyx","Cma",273779),
	CinchonaMutisii("Cinchona mutisii","Cmu",273780),
	CinchonaOfficinalis("Cinchona officinalis","Co",273781),
	CinchonaPitayensis("Cinchona pitayensis","Cpi",128294),
	CinchonaPubescens("Cinchona pubescens","Cpu",50278),
	CinnamomumTenuipile("Cinnamomum tenuipile","Cte",192326),
	CionaIntestinalis("Ciona intestinalis", "Ci", "Sea Squirt", 7719),
	CitrullusLanatus("Citrullus lanatus","Cl",3654),
	CitrusxAurantium("Citrus × aurantium","Cxa",43166),
	CitrusxClementina("Citrus × clementina","Cxc ",85681),
	CitrusxMicrocarpa("Citrus × microcarpa","Cxm",164113),
	CitrusxParadisi("Citrus × paradisi","Cxp",37656),
	CitrusHanaju("Citrus hanaju","Ch",481547),
	CitrusJaponica("Citrus japonica","Cj",76966),
	CitrusJunos("Citrus junos","Cju",135197),
	CitrusMaxima("Citrus maxima","Cim",37334),
	CitrusSinensis("Citrus sinensis", "Cs", "Sweet orange", 2711),
	CitrusTrifoliata("Citrus trifoliata","Cit",37690),
	CitrusUnshiu("Citrus unshiu","Cun",55188),
	ClarkiaBreweri("Clarkia breweri","Cbr",36903),
	CleretumBellidiforme("Cleretum bellidiforme","Cbe",90527),
	ClitoriaTernatea("Clitoria ternatea","Clt",43366),
	ClostridiumAcetobutylicum("Clostridium acetobutylicum","Cla",1488),
	ClostridiumBotulinum("Clostridium botulinum","Cbo",1491),
	ClostridiumKluyveri("Clostridium kluyveri","Ck",1534),
	Clostridiumthermocellum("Clostridium thermocellum", "Ct", "Cthe", 1515),
	CoffeaAbeokutae("Coffea abeokutae","Cab",213304),
	CoffeaArabica("Coffea arabica", "Ca", "Coffee", 13443),
	CoffeaCanephora("Coffea canephora","Coc",49390),
	CoffeaEugenioides("Coffea eugenioides","Ceu",49369),
	CoffeaLiberica("Coffea liberica","Col",49373),
	ColeusScutellarioides("Coleus scutellarioides","Csc",4142),
	ConsolidaOrientalis("Consolida orientalis","Cor",565971),
	CoptisJaponica("Coptis japonica","Coj",3442),
	CoptisTeeta("Coptis teeta","Cot",261448),
	CorallococcusCoralloides("Corallococcus coralloides","Cco",184914),
	CoreopsisGrandiflora("Coreopsis grandiflora","Cg",13449),
	CorydalisVaginans("Corydalis vaginans","Cva",3044017),
	CrambeHispanica("Crambe hispanica","Chi",70124),
	CrepisPalaestina("Crepis palaestina","Cpa",72611),
	CrocusSativus("Crocus sativus","Crs",82528),
	CrotonStellatopilosus("Croton stellatopilosus","Cst",431156),
	CrotonSublyratus("Croton sublyratus","Csu",107238),
	CryptomeriaJaponica("Cryptomeria japonica","Cja",3369),
	CucurbitaMaxima("Cucurbita maxima","Cum",3661),
	CucurbitaPepo("Cucurbita pepo","Cpe",3663),
	CurcumaLonga("Curcuma longa","Clo",136217),
	CyanidioschyzonMerolae("Cyanidioschyzon merolae","Cym",45157),
	CystobacterFuscus("Cystobacter fuscus","Cfu",43),
	CytophagaHutchinsonii("Cytophaga hutchinsonii","Chu",985),
	DahliaPinnata("Dahlia pinnata","Dpi",101596),
	DanioRerio("Danio rerio", "Dr", "Zebra fish", 7955),
	DaphneOdora("Daphne odora","Do",329675),
	DaphniaMagna("Daphnia magna", "Da", 35525),
	DaphniaPulex("Daphnia pulex", "Dp", 6669),
	DasypusNovemcinctus("Dasypus novemcinctus", "Dn", "Armadillo", 9361),
	DaturaInoxia("Datura inoxia","Di",4075),
	DaturaStramonium("Datura stramonium","Dst",4076),
	DaucusCarota("Daucus carota","Dca",4039),
	DavalliaTrichomanoides("Davallia trichomanoides","Dt",328206),
	DelftiaAcidovorans("Delftia acidovorans","Dac",80866),
	DelphiniumGrandiflorum("Delphinium grandiflorum","Dg",85439),
	DerrisElliptica("Derris elliptica","De",56063),
	DesmodiumUncinatum("Desmodium uncinatum","Dun",225101),
	DianthusCaryophyllus("Dianthus caryophyllus","Dc",3570),
	DicranumScoparium("Dicranum scoparium","Ds",3222),
	DictyosteliumDiscoideum("Dictyostelium discoideum","Dd",44689),
	DigitalisLanata("Digitalis lanata","Dl",49450),
	DigitalisPurpurea("Digitalis purpurea","Dpu",4164),
	DiospyrosKaki("Diospyros kaki","Dk",35925),
	DolichandraUnguiscati("Dolichandra unguis-cati","Du",73871),
	DrosophilaMelanogaster("Drosophila melanogaster", "Dm", "Fruit fly", 7227),
	EchinposTelfairi ("Echinops telfairi", "Et", "Hedgehog", 9371),
	EnterococcusFaecalis("Enterococcus faecalis","Ef",1351),
	EquisetumArvense("Equisetum arvense","Ea",3258),
	EquusCaballus("Equus caballus", "Qc", "Horse", 9796),
	ErwiniaAmylovora("Erwinia amylovora","Eam",552),
	EscherichiaColi("Escherichia coli", "Ec", 562),
	EschscholziaCalifornica("Eschscholzia californica","Eca",3467),
	EucalyptusPiperita("Eucalyptus piperita","Ep",87677),
	EuglenaGracilis("Euglena gracilis","Eg",3039),
	EuonymusAlatus("Euonymus alatus","Eal",4307),
	EuphorbiaLagascae("Euphorbia lagascae","El",54672),
	EustomaGrandiflorum("Eustoma grandiflorum","Egr",52518),
	FagopyrumEsculentum("Fagopyrum esculentum","Fe",3617),
	FagopyrumTataricum("Fagopyrum tataricum","Ft",62330),
	FagusCrenata("Fagus crenata","Fc",28929),
	FelisCatus("Felis catus","Fca",9685),
	FlaveriaBidentis("Flaveria bidentis","Fb",4224),
	FlaveriaChlorifolia("Flaveria chlorifolia","Fch",4228),
	FluviicolaTaffensis("Fluviicola taffensis","Fta",191579),
	ForsythiaxIntermedia("Forsythia × intermedia","Fxi",55183),
	ForsythiaKoreana("Forsythia koreana","Fk",205692),
	FragariaxAnanassa("Fragaria × ananassa","Fxa",3747),
	GalanthusElwesii("Galanthus elwesii","Ge",82232),
	GaliumMollugo("Galium mollugo","Gmo",254777),
	GallusGallus("Gallus gallus", "Gg", "Chicken", 9031),
	GardeniaJasminoides("Gardenia jasminoides","Gj",114476),
	GemmataObscuriglobus("Gemmata obscuriglobus","Go",114),
	GentianaStraminea("Gentiana straminea","Gs",50768),
	GentianaTriflora("Gentiana triflora","Gt",55190),
	GerberaHybrid("Gerbera hybrid","Ghy",18101),
	GibberellaZeae("Gibberella zeae", "Gz", "Fusarium graminearum", 5518),
	GinkgoBiloba("Ginkgo biloba","Gb",3311),
	GlandulariaxHybrida("Glandularia × hybrida","Gxh",76714),
	GlebionisSegetum("Glebionis segetum","Gse",118509),
	GlycineMax("Glycine max", "Gm", "Soybean", 3847),
	GlycyrrhizaEchinata("Glycyrrhiza echinata","Gec",46348),
	GossypiumArboreum("Gossypium arboreum","Ga",29729),
	GossypiumBarbadense("Gossypium barbadense","Gba",3634),
	GossypiumHirsutum("Gossypium hirsutum","Ghi",3635),
	GuatteriaBlepharophylla("Guatteria blepharophylla","Gbl",402568),
	GuatteriaFriesiana("Guatteria friesiana","Gf",402569),
	GuatteriaHispida("Guatteria hispida","Gh",402570),
	HaematococcusLacustris("Haematococcus lacustris","Hl",44745),
	HalobacteriumSalinarum("Halobacterium salinarum","Hsa",2242),
	HaloferaxVolcanii("Haloferax volcanii","Hvo",2246),
	HelianthusAnnuus("Helianthus annuus","Ha",4232),
	HelianthusTuberosus("Helianthus tuberosus","Ht",4233),
	HelicobacterPylori("Helicobacter pylori","Hp",210),
	HeveaBrasiliensis("Hevea brasiliensis","Hb",3981),
	HomoSapiens("Homo sapiens", "Hs", "Human", 9606),
	HordeumLechleri("Hordeum lechleri","Hle",38856),
	HordeumVulgare("Hordeum vulgare", "Hv", "Barley", 4513),
	HydrangeaMacrophylla("Hydrangea macrophylla","Hm",23110),
	HyoscyamusAlbus("Hyoscyamus albus","Hal",310458),
	HyoscyamusMuticus("Hyoscyamus muticus","Hmu",35626),
	HyoscyamusNiger("Hyoscyamus niger","Hn",4079),
	HypericumAndrosaemum("Hypericum androsaemum","Han",140968),
	HypericumCalycinum("Hypericum calycinum","Hc",55963),
	HypericumPerforatum("Hypericum perforatum","Hpe",65561),
	HyphomicrobiumZavarzinii("Hyphomicrobium zavarzinii","Hz",48292),
	IlexParaguariensis ("Ilex paraguariensis", "Ip", "Yerba mate", 185542),
	ImpatiensBalsamina("Impatiens balsamina","Ib",63779),
	IpomoeaBatatas("Ipomoea batatas","Iba",4120),
	IpomoeaNil("Ipomoea nil","In",35883),
	IpomoeaPurpurea("Ipomoea purpurea","Ipu",4121),
	JuglansRegia("Juglans regia","Jr",51240),
	KandeliaCandel("Kandelia candel","Kc",61147),
	KlebsiellaOxytoca("Klebsiella oxytoca","Ko",571),
	KlebsiellaPneumoniae("Klebsiella pneumoniae","Kp",1284798),
	LacticaseibacillusCasei("Lacticaseibacillus casei","Lc",1312920),
	LactococcusLactis("Lactococcus lactis","Ll",1358),
	LactucaSativa("Lactuca sativa","Ls",4236),
	LamiumGaleobdolon("Lamium galeobdolon","Lg",53161),
	LathyrusOdoratus("Lathyrus odoratus","Lo",3859),
	LavandulaAngustifolia("Lavandula angustifolia","Laa",39329),
	LawsoniaInermis("Lawsonia inermis","Li",141191),
	LemnaAequinoctialis("Lemna aequinoctialis","Lae",89585),
	LemnaMinor("Lemna minor","Lm",4472),
	LensCulinaris("Lens culinaris","Lcu",3864),
	LeucaenaLeucocephala("Leucaena leucocephala","Lle",3866),
	LiliumLongiflorum("Lilium longiflorum","Llo",4690),
	LimnanthesAlba("Limnanthes alba","Lal",42439),
	LimnanthesDouglasii("Limnanthes douglasii","Ld",28973),
	LimoniumLatifolium("Limonium latifolium","Lla",227291),
	LinumFlavum("Linum flavum","Lf",407263),
	LinumNodiflorum("Linum nodiflorum","Ln",407264),
	LinumPerenne("Linum perenne","Lp",35941),
	LinumUsitatissimum("Linum usitatissimum","Lu",4006),
	LithospermumErythrorhizon("Lithospermum erythrorhizon","Le",34254),
	LotusCorniculatus("Lotus corniculatus","Lco",47247),
	LoxodontaAfricana ("Loxodonta africana", "La", "Elephant", 9785),
	LunariaAnnua("Lunaria annua","Lan",153659),
	LupinusAlbus("Lupinus albus","Lua",3870),
	LupinusPolyphyllus("Lupinus polyphyllus","Lpo",3874),
	LygodiumCircinatum("Lygodium circinatum","Lci",84615),
	MacacaMulatta ("Macaca mulatta", "Ml", "Rhesus Monkey", 9544),
	MagnoliaGrandiflora("Magnolia grandiflora","Mg",3406),
	MagnoliaObovata("Magnolia obovata","Mo",349509),
	MalusHupehensis("Malus hupehensis","Mh",106556),
	MalusPumila("Malus pumila","Mp",283210),
	MatthiolaIncana("Matthiola incana","Mi",3724),
	MegathyrsusMaximus("Megathyrsus maximus","Mma",59788),
	MelilotusAlbus("Melilotus albus","Mal",47082),
	MenthaxGracilis("Mentha × gracilis","Mxg",241069),
	MenthaxPiperita("Mentha × piperita","Mxp",34256),
	MenthaAquatica("Mentha aquatica","Maq",190902),
	MenthaSpicata("Mentha spicata","Ms",29719),
	MethanocaldococcusJannaschii("Methanocaldococcus jannaschii","Mj",2190),
	MethanosarcinaMazei("Methanosarcina mazei","Mem",1434114),
	MethanosarcinaThermophila("Methanosarcina thermophila","Mt",2210),
	MethanothermobacterMarburgensis("Methanothermobacter marburgensis","Mmb",145263),
	MethyloceanibacterCaenitepidi("Methyloceanibacter caenitepidi","Mc",1384459),
	MethylococcusCapsulatus("Methylococcus capsulatus","Mca",414),
	MethylorubrumExtorquens("Methylorubrum extorquens","Mex",408),
	MethylosphaeraHansonii("Methylosphaera hansonii","Mha",51353),
	MicrococcusLuteus("Micrococcus luteus","Mlu",1270),
	MirabilisJalapa("Mirabilis jalapa","Mja",3538),
	MomordicaCharantia("Momordica charantia","Mch",3673),
	MonodelphisDomestica  ("Monodelphis domestica", "Md", "Opossum", 13616),
	MusaAcuminata("Musa acuminata","Mac",4641),
	MusMusculus("Mus musculus", "Mm", "Mouse", 10090),
	MycobacteriumAvium("Mycobacterium avium","Mav",1764),
	MycobacteriumKansasii("Mycobacterium kansasii","Mk",1768),
	MycobacteriumTuberculosis ("Mycobacterium tuberculosis", "Mx", "Tuberculosis", 1773),
	MycolicibacteriumFortuitum("Mycolicibacterium fortuitum","Mf",1766),
	MycolicibacteriumPhlei("Mycolicibacterium phlei","Mph",1771),
	MycoplasmoidesPneumoniae("Mycoplasmoides pneumoniae","Mpn",1263835),
	NannocystisExedens("Nannocystis exedens","Ne",54),
	NarcissusPseudonarcissus("Narcissus pseudonarcissus","Nps",39639),
	NepenthesAlata("Nepenthes alata","Na",4376),
	NepenthesGracilis("Nepenthes gracilis","Ng",150966),
	NepenthesMirabilis("Nepenthes mirabilis","Nm",150983),
	NepenthesRafflesiana("Nepenthes rafflesiana","Nra",150990),
	NerineBowdenii("Nerine bowdenii","Nb",59042),
	NeurosporaCrassa("Neurospora crassa","Nc",5141),
	NicotianaAttenuata("Nicotiana attenuata","Nat",49451),
	NicotianaBenthamiana("Nicotiana benthamiana","Nbe",4100),
	NicotianaGlutinosa("Nicotiana glutinosa","Ngl",35889),
	NicotianaPlumbaginifolia("Nicotiana plumbaginifolia","Np",4092),
	NicotianaRustica("Nicotiana rustica","Nr",4093),
	NicotianaSylvestris("Nicotiana sylvestris","Ns",4096),
	NitrosopumilusMaritimus("Nitrosopumilus maritimus","Nma",338192),
	NothapodytesNimmoniana("Nothapodytes nimmoniana","Nn",159386),
	OleaEuropaea("Olea europaea","Oe",4146),
	OphiorrhizaJaponica("Ophiorrhiza japonica","Oj",367363),
	OphiorrhizaPumila("Ophiorrhiza pumila","Op",157934),
	OrnithorhynchusAnatinus	("Ornithorhynchus anatinus", "Oa", "Platypus", 9258),
	OryctolagusCuniculus  ("Oryctolagus cuniculus", "Oc", "Rabbit", 9986),
	OryzaIndica("Oryza indica", "Oi", "Indian Rice"),
	OryzaJaponica("Oryza japonica", "Oj", "Rice"),
	OryzaSativa("Oryza sativa", "Os", "Rice", 4530),
	OryzaSativaIndica("Oryza sativa Indica Group", "Osi", "Indian Rice", 39946),
	OryzaSativaJaponica("Oryza sativa Japonica Group", "Osj", "Rice", 39947),
	OryziasLatipes ("Oryzias latipes", "Ol", "Medaka Fish", 8090),
	OvisAries("Ovis aries", "Ova", "Sheep", 9940),
	OxybasisRubra("Oxybasis rubra","Or",3560),
	PanaxGinseng("Panax ginseng","Pgi",4054),
	PanaxNotoginseng("Panax notoginseng","Pno",44586),
	PanicumMiliaceum("Panicum miliaceum","Pm",4540),
	PanicumVirgatum("Panicum virgatum","Pvi",38727),
	PantoeaAgglomerans("Pantoea agglomerans","Pag",549),
	PantoeaAnanatis("Pantoea ananatis","Paa",553),
	PanTroglodytes("Pan troglodytes", "Pt", "Chimpanzee", 9598),
	PassifloraEdulis("Passiflora edulis","Ped",78168),
	PaulliniaCupana("Paullinia cupana", "Pc", "Guarana", 392747),
	PelargoniumCrispum("Pelargonium crispum","Pcr",1417776),
	PericallisCruenta("Pericallis cruenta","Pec",98709),
	PerillaFrutescens("Perilla frutescens", "Pe", "Beefsteak plant", 48386),
	PerseaAmericana("Persea americana","Pa",3435),
	PersicariaTinctoria("Persicaria tinctoria","Pti",96455),
	PetiveriaAlliacea("Petiveria alliacea","Pal",46142),
	PetroselinumCrispum("Petroselinum crispum","Pcs",4043),
	PhaseolusCoccineus("Phaseolus coccineus","Pco",3886),
	PhaseolusLunatus("Phaseolus lunatus","Plu",3884),
	PhlebodiumAureum("Phlebodium aureum","Pau",218620),
	PhleumPratense("Phleum pratense","Ppr",15957),
	PhragmitesAustralis("Phragmites australis","Phu",29695),
	PhysariaFendleri("Physaria fendleri","Pfe",63442),
	PhysariaLindheimeri("Physaria lindheimeri","Pl",439687),
	PiceaGlauca("Picea glauca","Pg",3330),
	PimpinellaAnisum("Pimpinella anisum","Pan",271192),
	PinusBanksiana("Pinus banksiana","Pb",3353),
	PinusContorta("Pinus contorta","Pic",3339),
	PinusDensiflora("Pinus densiflora","Pde",77912),
	PinusPonderosa("Pinus ponderosa","Ppo",55062),
	PinusSabiniana("Pinus sabiniana","Psa",268869),
	PinusStrobus("Pinus strobus","Pst",3348),
	PinusSylvestris("Pinus sylvestris","Psy",3349),
	PlantagoMajor("Plantago major","Pma",29818),
	PlasmodiumFalciparum("Plasmodium falciparum", "Pf", "Malaria Parasite", 5833),
	PlectranthusBarbatus("Plectranthus barbatus","Pba",41228),
	PlumbagoEuropaea("Plumbago europaea","Peu",114226),
	PlumbagoIndica("Plumbago indica","Pin",122308),
	PodophyllumPeltatum("Podophyllum peltatum","Ppe",35933),
	PogostemonCablin("Pogostemon cablin","Pca",28511),
	PolaribacterFilamentus("Polaribacter filamentus","Pfi",53483),
	PopulusAlba("Populus alba","Poa",43335),
	PopulusDeltoides("Populus deltoides","Pod",3696),
	PopulusNigra("Populus nigra","Pni",3691),
	PopulusTrichocarpa("Populus trichocarpa", "Pi", "Western Balsam Poplar", 3694),
	PortulacaGrandiflora("Portulaca grandiflora","Pog",3583),
	PrunusDulcis("Prunus dulcis","Pd",3755),
	PrunusMume("Prunus mume","Pmu",102107),
	PrymnesiumParvum("Prymnesium parvum", "Pp", 97485),
	PseudomonasAeruginosa("Pseudomonas aeruginosa","Pae",1009714),
	PseudomonasFluorescens("Pseudomonas fluorescens","Pfl",294),
	PsilotumNudum("Psilotum nudum","Pn",3240),
	PterisVittata("Pteris vittata","Ptv",13821),
	PuerariaMontana("Pueraria montana","Pmo",132459),
	PunicaGranatum("Punica granatum","Pgr",22663),
	PyrusPyrifolia("Pyrus pyrifolia","Ppy",3767),
	QuercusRobur("Quercus robur","Qro",38942),
	QuercusRubra("Quercus rubra","Qr",3512),
	RaphanusSativus("Raphanus sativus","Rsa",3726),
	RattusNorvegicus("Rattus norvegicus", "Rn", "Rat", 10116),
	RheumPalmatum("Rheum palmatum","Rp",137221),
	RheumTataricum("Rheum tataricum","Rt",205071),
	RhizophoraApiculata("Rhizophora apiculata","Ra",106626),
	RhizophoraMangle("Rhizophora mangle","Rm",40031),
	RhizophoraStylosa("Rhizophora stylosa","Rst",98588),
	RhodiolaRosea("Rhodiola rosea","Rr",203015),
	RhodiolaSachalinensis("Rhodiola sachalinensis","Rs",265354),
	RhodobacterCapsulatus("Rhodobacter capsulatus","Rca",1061),
	RhodotorulaGlutinis("Rhodotorula glutinis","Rgl",5535),
	RhusTyphina("Rhus typhina","Rty",255348),
	RobiniaPseudoacacia("Robinia pseudoacacia","Rps",35938),
	RosaChinensis("Rosa chinensis","Rc",74649),
	RosaHybrid("Rosa hybrid","Rhy",128735),
	RubiaTinctorum("Rubia tinctorum","Rti",29802),
	RubusIdaeus("Rubus idaeus","Rid",32247),
	RudbeckiaHirta("Rudbeckia hirta","Rh",52299),
	RutaGraveolens("Ruta graveolens","Rg",37565),
	SaccharomycesCerevisiae("Saccharomyces cerevisiae", "Sc", "Yeast", 4932),
	SaccharopolysporaSpinosa("Saccharopolyspora spinosa","Ssp",60894),
	SaccharumOfficinarum("Saccharum officinarum","Sof",4547),
	SalmonellaEnterica("Salmonella enterica","Se",28901),
	SalviaFruticosa("Salvia fruticosa", "Sf", "Greek sage", 268906),
	SalviaOfficinalis("Salvia officinalis","Sao",38868),
	SalviaRosmarinus("Salvia rosmarinus","Sr",39367),
	SalviaSplendens("Salvia splendens","Spl",180675),
	SanguinariaCanadensis("Sanguinaria canadensis","Sca",3472),
	SantalumAlbum("Santalum album","Saa",35974),
	SantalumAustrocaledonicum("Santalum austrocaledonicum","Sau",293154),
	SantalumSpicatum("Santalum spicatum","Sas",453088),
	SarcinaVentriculi("Sarcina ventriculi","Sv",1267),
	SaussureaMedusa("Saussurea medusa","Sme",137893),
	SaxifragaStolonifera("Saxifraga stolonifera","Sst",182070),
	SchizonepetaTenuifolia("Schizonepeta tenuifolia","Ste",2849020),
	SchizosaccharomycesPombe("Schizosaccharomyces pombe","Sp",4896),
	ScutellariaBaicalensis("Scutellaria baicalensis","Sba",65409),
	ScutellariaViscidula("Scutellaria viscidula","Svi",512023),
	SecaleCereale("Secale cereale","Sce",4550),
	SelaginellaLepidophylla("Selaginella lepidophylla","Sle",59777),
	SenecioVernalis("Senecio vernalis","Sve",93496),
	SenecioVulgaris("Senecio vulgaris","Svu",76276),
	SerratiaMarcescens("Serratia marcescens","Sma",1401254),
	SesamumAlatum("Sesamum alatum","Sal",300844),
	SesamumIndicum("Sesamum indicum","Si",4182),
	SesamumRadiatum("Sesamum radiatum","Sra",300843),
	SesbaniaRostrata("Sesbania rostrata","Sro",3895),
	SetariaItalica("Setaria italica","Sit",4555),
	SileneDioica("Silene dioica","Sd",39879),
	SileneLatifolia("Silene latifolia","Sla",37657),
	SimmondsiaChinensis("Simmondsia chinensis","Sch",3999),
	SinningiaCardinalis("Sinningia cardinalis","Sic",189007),
	SinopodophyllumHexandrum("Sinopodophyllum hexandrum","She",93608),
	SkeletonemaMarinoi("Skeletonema marinoi", "Sm", 267567),
	SolanumAculeatissimum("Solanum aculeatissimum","Scl",267265),
	SolanumLycopersicum("Solanum lycopersicum", "Sl", "Tomato", 4081),
	SolanumMelongena("Solanum melongena","Som",4111),
	SolanumPennellii("Solanum pennellii","Spe",28526),
	SolanumTuberosum("Solanum tuberosum", "St", "Potato", 4113),
	SolidagoCanadensis("Solidago canadensis","Soc",59297),
	SorbusAucuparia("Sorbus aucuparia","Soa",36599),
	SorexAraneus ("Sorex araneus", "Sa", "Shrew", 42254),
	SorghumBicolor ("Sorghum bicolor", "Sb", "Sorghum", 4558),
	SpirodelaPolyrhiza("Spirodela polyrhiza","Spo",29656),
	SporobolusAlterniflorus("Sporobolus alterniflorus","Spa",29706),
	StellariaMedia("Stellaria media","Stm",13274),
	StigmatellaAurantiaca("Stigmatella aurantiaca","Sta",41),
	StreptococcusMutans("Streptococcus mutans","Smu",1309),
	StreptococcusPneumoniae("Streptococcus pneumoniae","Spn",1001746),
	StreptomycesAntibioticus("Streptomyces antibioticus","San",1890),
	StreptomycesGriseus("Streptomyces griseus","Sg",1911),
	StrobilanthesCusia("Strobilanthes cusia","Scu",222567),
	SusScrofa("Sus scrofa", "Ss", "Pig", 9823),
	SyntrophotaleaAcetylenica("Syntrophotalea acetylenica","Sac",29542),
	SyzygiumAromaticum("Syzygium aromaticum","Sar",219868),
	TagetesErecta("Tagetes erecta","Te",13708),
	TagetesPatula("Tagetes patula","Tp",55843),
	TanacetumBalsamita("Tanacetum balsamita","Tb",301877),
	TanacetumVulgare("Tanacetum vulgare","Tv",128002),
	TaxusBaccata("Taxus baccata","Tba",25629),
	TaxusBrevifolia("Taxus brevifolia","Tbr",46220),
	TaxusChinensis("Taxus chinensis","Tch",29808),
	TaxusCuspidata("Taxus cuspidata","Tcu",99806),
	TellimaGrandiflora("Tellima grandiflora","Tg",29775),
	TetradesmusObliquus("Tetradesmus obliquus","To",3088),
	TetraodonNigroviridis ("Tetraodon nigroviridis", "Tn", "Pufferfish", 99883),
	ThalassiosiraPseudonana("Thalassiosira pseudonana","Tps",35128),
	ThalictrumFlavum("Thalictrum flavum","Tf",150094),
	ThalictrumTuberosum("Thalictrum tuberosum","Tt",79802),
	TheobromaCacao("Theobroma cacao", "Tc", "Cocoa", 3641),
	ThermococcusKodakarensis("Thermococcus kodakarensis","Tk",311400),
	ThermotogaMaritima("Thermotoga maritima","Tma",2336),
	ThujaPlicata("Thuja plicata","Tpl",3316),
	TrichosanthesKirilowii("Trichosanthes kirilowii","Tki",3677),
	TrifoliumPratense("Trifolium pratense","Tpr",57577),
	TrifoliumRepens("Trifolium repens","Tr",3899),
	TriticumAestivum ("Triticum aestivum", "Ta", "Wheat", 4565),
	TriticumSpelta("Triticum spelta","Ts",58933),
	TriticumUrartu("Triticum urartu","Tu",4572),
	UlvaIntestinalis("Ulva intestinalis","Ui",3116),
	UlvaLactuca("Ulva lactuca","Ul",63410),
	UrochloaPanicoides("Urochloa panicoides","Up",37563),
	VacciniumMyrtillus("Vaccinium myrtillus","Vm",180763),
	VanillaPlanifolia("Vanilla planifolia","Vp",51239),
	VerniciaFordii("Vernicia fordii","Vf",73154),
	VibrioCholerae("Vibrio cholerae","Vc",1225783),
	VibrioFurnissii("Vibrio furnissii","Vfu",29494),
	ViciaFaba("Vicia faba","Vfa",3906),
	ViciaSativa("Vicia sativa","Vs",3908),
	VignaAconitifolia("Vigna aconitifolia","Va",3918),
	VignaAngularis("Vigna angularis","Van",3914),
	VignaUnguiculata("Vigna unguiculata","Vu",3917),
	VitisVinifera ("Vitis vinifera", "Vv", "Wine Grape", 29760),
	WachendorfiaThyrsiflora("Wachendorfia thyrsiflora","Wt",95970),
	XanthomonasArboricola("Xanthomonas arboricola","Xa",56448),
	XanthomonasAxonopodis("Xanthomonas axonopodis","Xax",53413),
	XenopusTropicalis("Xenopus tropicalis", "Xt", "Frog", 8364),
	ZeaLuxurians("Zea luxurians","Zl",15945),
	ZeaMays ("Zea mays", "Zm", "Maize", 4577),
	ZingiberOfficinale("Zingiber officinale","Zo",94328),
	ZingiberZerumbet("Zingiber zerumbet","Zz",311405),
	ZymomonasMobilis("Zymomonas mobilis","Zmo",542),
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
