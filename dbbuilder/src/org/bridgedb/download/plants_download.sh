##!!!!!!!!!!!
# Update release-#
##!!!!!!!!!!!

release='5'

ftp_site=ftp.ensemblgenomes.org
species_list=${1?"Usage: $0 species_list"}

mysql='mysql --host=mysql-dev.cgl.ucsf.edu --port=13308'
mysqlimport='mysqlimport  --host=mysql-dev.cgl.ucsf.edu --port=13308'

cat $species_list | while read species_name
do

 # check if commented out
 if [[ $species_name =~ '#' ]]
 then
	echo "Skip!: $species_name commented out in file: $species_list"
	continue
 fi

  export species_name="$species_name"; var=`echo "ls -l /pub/plants/release-${release}/mysql/" | ncftp ${ftp_site} | perl -ane 'if ( $_ =~ /$Logged in to/ ) { $go=1; next } if ( $go ) { @a=split; $fileName=$a[ scalar( @a ) - 1]; $compareStr=$ENV{ species_name }."_core"; if ( $fileName =~/^$compareStr/) { print $fileName."\n"; } }'`; set -- $var; mysql_db_name=$1

  # check variable before proceeding
  if [[ $mysql_db_name =~ 'species_list' || $mysql_db_name == '' ]]
  then
	echo "Skip!:  $species_name not found at $ftp_site. Please fix file: $species_list"
  	continue 
  fi

  echo "** $mysql_db_name **"
  mkdir ./${mysql_db_name}

  # get raw tables from ftp site
  ncftpget ${ftp_site} ./${mysql_db_name} /pub/plants/release-${release}/mysql/${mysql_db_name}/*.txt.gz
  for q in ./${mysql_db_name}/*.txt.gz
  do
    gunzip $q
  done

  ncftpget ${ftp_site} ./ /pub/plants/release-${release}/mysql/${mysql_db_name}/${mysql_db_name}.sql.gz
  gunzip ./${mysql_db_name}.sql.gz

  #create local mysql db
  echo "create database if not exists ${mysql_db_name}" | ${mysql} -u genmapp -pfun4genmapp
  #create schema
  ${mysql} -u genmapp -pfun4genmapp ${mysql_db_name} < ./${mysql_db_name}.sql
   # place in own scope, due to "cd" command
  (
    cd ./${mysql_db_name}; for table_name in *.txt
    do
      ${mysqlimport} -u genmapp -pfun4genmapp ${mysql_db_name} `pwd`/${table_name}
    done
  )

  # zip, archive and clean up
  gzip ./${mysql_db_name}.sql
  tar cvzf ./${mysql_db_name}.txt.tgz ./${mysql_db_name}/*
  rm -R ./${mysql_db_name}/

done

