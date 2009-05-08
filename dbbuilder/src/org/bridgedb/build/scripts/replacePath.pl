use strict;

# this script is a hack designed to get around vagaries in bash<->sed/command-line perl interaction

# I couldn't get the command-line version to the following properly, so I wrote it as a standalone script to protect it from bash munging.  Now it works fine

# the goal is to replace the template parameter "YYYYYY" with a path; the stumbling block was in the interpretation of escape sequences for the character "\"

my $dir=$ARGV[0];

while( <STDIN> )
{
  $_ =~ s/YYYYYY/$dir/g;
  print $_;
}
