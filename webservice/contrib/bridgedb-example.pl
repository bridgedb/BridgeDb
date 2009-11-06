#!/usr/bin/perl

use warnings;
use strict;
use LWP::Simple;

## If you want to use a local bridgedb idmapper in perl, setup a local service
## and change the url below. See http://bridgedb.org/wiki/LocalService
## for information on how to run a local service.
my $webservice = "http://webservice.bridgedb.org";

sub mapID
{
	my $id = shift;
	my $code = shift;
	my $species = "Human";
	my $cmdUrl = "$webservice/$species/xrefs/$code/$id";
	my $content = get $cmdUrl;
	die "Couldn't get it!" unless defined $content;
	
	my @lines = split /\n/, $content;
	for my $line (@lines)
	{
		#~ my ($id, $ds) = split /\t/, $line;
		my @fields = split /\t/, $line;
		my $id = $fields[0];
		my $ds = $fields[1];
		print $ds . "::$id\n";
	}
}

mapID ("1234", "L");
