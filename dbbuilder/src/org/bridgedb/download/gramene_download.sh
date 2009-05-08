ftp_site=ftp.gramene.org
species_list=${1?"Usage: $0 species_list"}

mysql=mysql
mysqlimport=mysqlimport

cat $species_list | while read species_name
do

 # check if commented out
 if [[ $species_name =~ '#' ]]
 then
        echo "Skip!: $species_name commented out in file: $species_list"
        continue
 fi

  export species_name="$species_name"; var=`echo "ls -l /pub/gramene/CURRENT_RELEASE/data/database_dump/mysql-dumps/" | ncftp ${ftp_site} | perl -ane 'if ( $_ =~ /$Logged in to/ ) { $go=1; next } if ( $go ) { @a=split; $fileName=$a[ scalar( @a ) - 1]; $compareStr=$ENV{ species_name }."_core"; if ( $fileName =~/^$compareStr/) { print $fileName."\n"; } }'`; set -- $var; mysql_db_name=$1 
  
  # check variable before proceeding
  if [[ $mysql_db_name =~ 'species_list' || $mysql_db_name == '' ]]
  then
        echo "Skip!:  $species_name not found at $ftp_site. Please fix file: $species_list"
        continue
  fi

  # trim variable to fit pattern from other sources
  echo "* $mysql_db_name *"
  mysql_db_name=${mysql_db_name/\.mysqldump\.gz/} 
  echo "** $mysql_db_name **"

  # get raw tables from ftp site
  ncftpget ${ftp_site} ./ /pub/gramene/CURRENT_RELEASE/data/database_dump/mysql-dumps/${mysql_db_name}.mysqldump.gz
  gunzip ./${mysql_db_name}.mysqldump.gz

  #rename 3-name databases
  mysql_db_name_local=${mysql_db_name/oryza_sativa/oryza}

  #create local mysql db
  echo "create database if not exists ${mysql_db_name_local}" | ${mysql} -u genmapp -pfun4genmapp
  # load dump file
  ${mysql} -u genmapp -pfun4genmapp --max_allowed_packet=16M ${mysql_db_name_local} < ./${mysql_db_name}.mysqldump

# zip, archive and clean up
gzip ./${mysql_db_name}.mysqldump

done
 
