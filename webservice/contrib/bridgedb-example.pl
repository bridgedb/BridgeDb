#!/usr/bin/perl

use warnings;
use strict;
use LWP::Simple;

my $webservice = "http://localhost:8183";

sub mapID
{
	my $id = shift;
	my $code = shift;
	my $species = "Human";
	my $cmdUrl = "$webservice/model/$species/$code/$id/xrefs";
	my $content = get $cmdUrl;
	die "Couldn't get it!" unless defined $content;
	
	my @lines = split /\n/, $content;
	for my $line (@lines)
	{
		print "$line\n";
		
		#~ my ($id, $ds) = split /\t/, $line;
		my @fields = split /\t/, $line;
		my $id = $fields[0];
		$ds = $fields[1];
		print "$ds::$id\n";
	}
}

mapID ("1234", "L");