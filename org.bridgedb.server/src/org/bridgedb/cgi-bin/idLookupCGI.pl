#!/usr/bin/perl
use strict;

use CGI;

my $query= CGI->new;
print "Content-Type: text/html;charset=us-ascii\n\n";

my $speciesCode = $query->param( "species_code" );
my $inputIDSystem = $query->param( "input_id_system" );
my $outputIDSystem = $query->param( "output_id_system" ); 
my $inputIDList = $query->param( "input_id_list" );

# check for valid query
if ($inputIDSystem =~ $outputIDSystem){
	print "<br>Invalid query!!!  Select different input and output ID systems.<br>";
	exit;
}

print "code: ".$speciesCode."\n";
print "input sys: ".$inputIDSystem."\n";
print "output sys: ".$outputIDSystem."\n";
print "Idlist: [".$inputIDList."]\n";
# lookup the db name
my $dbNamesList = `echo "show databases" | mysql -u genmapp -pfun4genmapp`;
my @dbNames = split( /\s+/, $dbNamesList );
my $mostRecentDate = 0; 
my $mostRecentDbName = "";
foreach my $tempDbName ( @dbNames )
{
  my $pattern = $speciesCode."_Derby_(\\d+)";
  if ( $tempDbName =~ /$pattern/ )
  {
    chomp $tempDbName;
    my $thisDate=$1;
    if ( $thisDate > $mostRecentDate ) { $mostRecentDate = $thisDate; $mostRecentDbName = $tempDbName }
  } 
}

die "Could not find any datestamped CS db for spcies code $speciesCode" unless $mostRecentDate > 0 && !( $mostRecentDbName eq "" );

# choose the most recent db for the species
my $dbName = $mostRecentDbName;

print "<BR>dbName: $dbName<BR>";

use DBI;
my $dbh = DBI->connect( "dbi:mysql:$dbName", "genmapp", "fun4genmapp" );

my $getRHSID = "";
## symbol lookups via link join attr tables
if ($inputIDSystem =~ "Symbol"){
        $getRHSID = $dbh->prepare("select idRight from link left join attribute on idLeft = id where code = \"En\" and attrValue = ? and attrName = ? and codeLeft = \"En\" and codeRight = ?" );

}
elsif ($outputIDSystem =~ "Symbol"){
        $getRHSID = $dbh->prepare("select attrValue from link left join attribute on idLeft = id where code = \"En\" and idRight = ? and codeRight = ? and attrName = ? and codeLeft = \"En\"" );

}
else {
	## default lookup in link table
	#$getRHSID = $dbh->prepare("select idRight from link where idLeft = ? and codeLeft = ? and codeRight = ?" );
	$getRHSID = $dbh->prepare("select t1.idRight from link AS t1 join link AS t2 using(idLeft, codeLeft) where t2.idRight = ? and t2.codeRight = ? and t1.codeRight = ?");
}

print "<BR>";
print "<table cols=4 border=1>";
print "<tr>";
print "<th>InputID</th>";
print "<th>InputSys</th>";
print "<th>OutputID</th>";
print "<th>OutputSys</th>";
print "</tr>";

foreach my $LHSID ( split( " ", $inputIDList ) )
{
  $getRHSID->execute( $LHSID, $inputIDSystem, $outputIDSystem );

  my $r = $getRHSID->fetchrow_arrayref();
  print "<tr>";
  if ( defined( $r ) )
  {
    print "<td>".$LHSID."</td>";
    print "<td>".$inputIDSystem."</td>";
    print "<td>".$r->[0]."</td>";
    print "<td>".$outputIDSystem."</td>";
  }
  else
  {
    print "<td>".$LHSID."</td>";
    print "<td>".$inputIDSystem."</td>";
    print "<td>"."---"."</td>";
    print "<td>".$outputIDSystem."</td>";
  }
  print "</tr>";
}
print "</table>";
print "</html>\n";
