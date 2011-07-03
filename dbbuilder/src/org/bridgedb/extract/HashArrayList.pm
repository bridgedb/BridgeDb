# A.Pico July 2011

## getArrayTable ##
# 
# This subroutine opens a local file called 'ArrayList'.
# The file will then be read and parsed into a hash which is returned by
# the routine. USAGE: call this method and assign it to a hash variable. Use the
# hash to keep track of all the different ways to refer to a species.
##
# RETURNS A HASH OF ARRAYS
#
# KEY: genus_species (all lowercase) 
# [n]: array of microarray names 
#
##
sub getArrayTable {
$arrayFile = 'ArrayList';
my %arrayTable = ();                # Hash of Arrays for storing species table in Perl

unless( open( ARRAY, $arrayFile)){
	print "Could not open file $arrayFile: $!\n";
        exit; 
}


foreach (<ARRAY>){
    my $line = $_;
    if ($line =~ /\*/){next; } # Header line
    chomp $line;
    my @fields = split(',', $line);
    for (my $i=1; $i<=$#fields; $i++){
        push(@{$arrayTable{$fields[0]}}, $fields[$i]);
    }
}

close(ARRAY);

return %arrayTable;

}

1;

