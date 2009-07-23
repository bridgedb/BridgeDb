# A.Pico July 2009

## Link_Databases.pl reads the list of .bridge databases in the current directory and 
## creates symbolic links to the uncompressed folders, using the centralized SpeciesList 
## to get species names, etc. Pre-existing symbolic links are removed.


use strict;
use HashSpeciesList;

my $altSpeciesFile = $ARGV[0];
my %speciesTable = getSpeciesTable($altSpeciesFile);

my @bridgeFiles;
while (<*.bridge>) {
	push (@bridgeFiles, $_);
}

if ($#bridgeFiles == -1) {
	print "No BridgeDb files detected!\n" and exit;
}

print "The following BridgeDb files have been detected...\n\n";
foreach my $file (@bridgeFiles){
	print "\t $file\n";
}
print "\nShall I go ahead and link ALL detected databases or would you prefer INTERACTIVE mode? (a/i) --> ";
my $answer = <STDIN>;
chomp $answer;
until ($answer =~ /(a|i)/i) {
	print "\n\nInvalid response!\n\tPlease type \"a\" for ALL or \"i\" for INTERACTIVE --> ";
	$answer = <STDIN>;
	chomp $answer;
}

## Begin Processing List

foreach my $db (@bridgeFiles) {
	if ($answer =~ /i/i) {
		print "\nShall I try to link $db ? (y/n) --> ";
		my $go = <STDIN>;
		chomp $go;
		until ($go =~ /(Y|Yes|N|No)/i) {
			print "\n\nInvalid response!\n\tPlease type \"y\" or \"n\" --> ";
			$go = <STDIN>;
			chomp $go;
		}
		if ($go =~ /(N|No)/i) {
			next;
		}
	}
	my @prefix = split(/_/, $db);
	my @dirName = split(/\./, $db);
	my @existingLn = `ls -ln`;
	my $linked = 0;	
	for my $key (keys %speciesTable) {
		if ($prefix[0] =~ $speciesTable{$key}[3]){
			print "...linking $key\n";
			
			#first remove any pre-existing directories
			foreach my $ln (@existingLn) {
				if ($ln =~ /$speciesTable{$key}[0] -> (.*)/) {
					my @args = ('rm', '-r', $1);
					system(@args);
				}
			}			
                        #unzip BridgeDb file
                        my @args = ('/usr/bin/unzip', $db);
                        system(@args);
 			#rename directory
			rename ("database", $dirName[0]);
			#first remove any pre-existing symlinks
			unlink($speciesTable{$key}[0]);
			# then create new symlinks
			$linked = symlink($dirName[0], $speciesTable{$key}[0]);;
		}	
	}
	if ($linked) {
		print "\n\n $db successfully linked\n\n";
	} else {
		print "\n\n $db NOT LINKED!\n\n";
	}
}
print "DONE!\n";


