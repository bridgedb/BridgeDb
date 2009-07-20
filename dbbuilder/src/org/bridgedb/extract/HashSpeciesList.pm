# A.Pico July 2009

## getSpeciesTable ##
# 
# This subroutine opens a local file called 'SpeciesList' by default. Alternatively
# you can pass in a custom file name. If no name is provided and the file is not
# found, the routine will attempt to download the file from the BridgeDb repository.
# The SpeciesFile will then be read and parsed into a hash which is returned by
# the routine. USAGE: call this method and assign it to a hash variable. Use the
# hash to keep track of all the different ways to refer to a species.
##
# RETURNS A HASH OF ARRAYS
#
# KEY: common name
# [0]: genus species
# [1]: taxonomy ID
# [2]: three-letter unigene code
# [3]: two-letter code
#
##
sub getSpeciesTable {
my $speciesFile = shift;
if (!$speciesFile) {
	$speciesFile = 'SpeciesList';
}
my %speciesTable = ();                # Hash of Arrays for storing species table in Perl

unless( open( SPECIES, $speciesFile)){
	# if attempting with custom file, then quit
	if ($speciesFile != 'SpeciesList'){
		print "Could not open file $speciesFile: $!\n";
                exit; 
	} else {
		# download the file from BridgeDb
		`wget http://svn.bigcat.unimaas.nl/bridgedb/trunk/dbbuilder/src/org/bridgedb/extract/SpeciesList`;

		# now try again
		unless( open( SPECIES, $speciesFile)){
			print "Could not open file $speciesFile: $!\n";
			exit;
		} 
	}
}


foreach (<SPECIES>){
    my $line = $_;
    if ($line =~ /\*/){next; } # Header line
    chomp $line;
    my @fields = split(/\t/, $line);
    $speciesTable{$fields[1]} = [$fields[0], $fields[2], $fields[3], $fields[4]]; 
}

close(SPECIES);

return %speciesTable;

}

1;

