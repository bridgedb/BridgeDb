
#use Devel::Size qw(total_size);
use strict;
use DBI;
use HashSpeciesList;
use HashArrayList;
use lib '/home/socr/c/users2/apico/src/ensembl/modules';
use lib '/home/socr/c/users2/apico/src/ensembl-functgenomics/modules';
use lib '/home/socr/c/users2/apico/bioperl-live'; 
use Bio::EnsEMBL::Registry;
use Bio::EnsEMBL::DBSQL::DBConnection;
use Bio::EnsEMBL::Funcgen::ProbeFeature;

## Under script control
## E.g., see Derby/runAll.sh and runList
my $scriptmode = 0;
my $speciesArg = 0;
my $dateArg = 0;
my $funcgen = 'Y';
my $gs = 0;

if ($#ARGV == 3) { # if 4 args passed in, then use them
	$speciesArg = $ARGV[0];
	$dateArg = $ARGV[1];
	$funcgen = $ARGV[2];
	$gs = $ARGV[3];

	#trigger script mode
	$scriptmode = 1;
}


## GET TABLE OF SUPPORTED SPECIES
# TODO: store species table in mysql database instead of flatfile
my %speciesTable = ();                # Hash of Arrays for storing species table in Perl
my @speciesList = ();                 # list of species names to display in menu
my $arrayPick = 'null';		      # menu selection

%speciesTable = getSpeciesTable();

for my $key (keys %speciesTable){
    push(@speciesList, "$key\t($speciesTable{$key}[0])");       # (VALUE:common name \t (genus species))
    if ($scriptmode) {
        if ($speciesTable{$key}[3] =~ /($speciesArg)/){
                $arrayPick = $key;
        }
    }

}

## MENU OF SUPPORTED SPECIES
unless ($scriptmode) {
	print "\n\nSTEP 1: Please select a species.\n\n";
	$arrayPick = pickFromArray(@speciesList);             # menu selection
}
if ($arrayPick eq 'null') {
	print "\n\nSpecies $speciesArg was not found!\n\n";	
	exit;
}
my @splitPick = split(/\t/, $arrayPick);                      # split: [0]=common name, [1]=(genus species)
my $speciesPick = $splitPick[0];                              # store common name, e.g., Mouse
my $twoLetterSpecies = $speciesTable{$speciesPick}[3];	      # two-letter code, e.g., Mm
#my $EnSpeciesCode = "En".$twoLetterSpecies;		      # Ensembl species-specific codes, e.g., EnMm
my $species = $speciesTable{$speciesPick}[0];                 # store genus species, e.g., Mus musculus 
my @split_species = split(/\s/, $species);   		      # split: [0]=genus, [1]=species, [2]=extra
my $genus = $split_species[0];
my $species_extra = $split_species[1];
if ($split_species[2]) {$species_extra .= "_".$split_species[2];}   # append "extra" to species (e.g., coli-K12)
my $genus_species = $genus."_".$species_extra;                      # store genus_species (with underscore)
$genus_species =~ tr/A-Z/a-z/;                                # all lowercase, e.g., mus_musculus 
my $genus_species_abv = substr($genus, 0, 1);      	      # store Ensembl-style abreviated genus species
$genus_species_abv .= $species_extra;                      	      # concatentate genus initial with species name
$genus_species_abv =~ tr/A-Z/a-z/;                            # all lowercase, e.g., mmusculus
my $mod_system = 'Ensembl';                                   # store Model Organism Database (MOD) identifier system
if ($species eq 'Wacky'){
    $mod_system = 'Something Else';                           # use this loop for custom species-MOD pairs
}


############################################################################
## ACCESS ENSEMBL DATABASE USING API ##
#######################################

## API: REGISTRY
# Use Registry to Access Latest Species Database (matching the API branch version) 
my $registry = 'Bio::EnsEMBL::Registry';

$registry->load_registry_from_db(
        -host => 'mysql-dev.cgl.ucsf.edu',
	-port => '13308',
        -user => 'genmapp',
	-pass => 'fun4genmapp',
        -verbose => "0");

# switch per database convention
my $temp_species = $species;
if ($gs =~ /(Y|Yes)/i){
	$species = $genus_species;
}
	
## API: GET ADAPTORS 
# get gene adaptor to query gene information
# get slice adaptor to load 'top-level' regions into SeqRegionCache
# get database adaptors to identify the name of latest species database
my $array_adaptor = $registry->get_adaptor($species, "funcgen", "array");  
my @dbas = @{Bio::EnsEMBL::Registry->get_all_DBAdaptors(-species => $species)};
my $dbname = $dbas[0]->dbc->dbname();        # e.g., core_mus_musculus_42_36c
my @split_dbname = split(/_/, $dbname);
if ($split_dbname[2] eq "collection"){	     # shift array elements for "collections"
	splice(@split_dbname,1,1);
}
my $build = $split_dbname[4];                # e.g., 42
my $build_nums = $build.$split_dbname[5];    # e.g., 42_36c

# switch back
$species = $temp_species;


############################################################################
## ALL SYSTEMS GO! ##
#####################


                if ($funcgen !~ /(N|No)/i ){
		  if (defined($array_adaptor)){
                    parse_AvailableArrays();
		  }
		  else {
			print "!!!ERROR!!! Funcgen called but no adaptor found. Check for funcgen db and populated tables.\n";
		  }
                }
		else {
		  if (defined($array_adaptor)){
			print "!!!WARNING!!! Funcgen db avaiable, but not called for. Suggest collecting funcgen info next time.\n"; 
		  }
		}


#################################################################################################
sub parse_AvailableArrays {

# Filename for ArrayList
 my $output = "test_ArrayList";
 open ARRAYS, ">>$output" or die $!;

 my %arraysBySpecies = ();

#my @arrayList = (@{$arrayTable{$genus_species}});  
#foreach my $array_name (@arrayList){

print "fetching arrays...\n";
    my @alist = ();
    my @arrays = @{$array_adaptor->fetch_all()};
    foreach my $a (@arrays){
	push(@alist, $a->name()."(".$a->probe_count().")");
    }

$" = ",";
print ARRAYS "$genus_species,@alist\n";

close ARRAYS;
} 
## PICK FROM ARRAY ##############################################################################
# IN: An Array
# OUT: A string selected from the Array
# FUNCTION: To display a list of options from the Array passed into the routine and allow the
#  user to select from the list.  The selection is returned.
#################################################################################################
sub pickFromArray {
    my @List = @_;

    my $f = 1;
    foreach my $name (@List) {
	print "$f\. $name\n";
	$f++;
    }
    my $max = $f - 1;
    print "\nChoose a number: ";
    my $Number = <STDIN>;
    chomp $Number;

    until ( $Number >= 1 && $Number <= $max ) {
	print "\n\nInvalid Entry!!!\nPlease Pick a number between 1 and $max: ";
	$Number = <STDIN>;
	chomp $Number;
    }

    my $Pick = $List[ $Number - 1 ];
    return $Pick;
}
# END: subroutines
