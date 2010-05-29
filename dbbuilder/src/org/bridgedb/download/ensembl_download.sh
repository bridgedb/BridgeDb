ftp_site=ftp.ensembl.org
species_list=${1?"Usage: $0 species_list"}

mysql='mysql  --host=mysql-dev.cgl.ucsf.edu --port=13308'
mysqlimport='mysqlimport  --host=mysql-dev.cgl.ucsf.edu --port=13308'

cat $species_list | while read species_name
do

 # check if commented out
 if [[ $species_name =~ '#' ]]
 then
        echo "Skip!: $species_name commented out in file: $species_list"
        continue
 fi

# check if flag for ontology table
if [[ $species_name =~ 'ontology' ]]
then
  echo "Retrieving generic ontology table..."

  # grab ontology file
  export var=`echo "ls -l /pub/current_mysql/" | ncftp ${ftp_site} | perl -ane 'if ( $_ =~ /$Logged in to/ ) { $go=1; next } if ( $go ) { @a=split; $fileName=$a[ scalar( @a ) - 1]; $compareStr="ensembl_ontology_"; if ( $fileName =~/^$compareStr/) { print $fileName."\n"; } }'`; set -- $var; mysql_go_name=$1 

  echo "** $mysql_go_name **"
  mkdir ./${mysql_go_name}

  # get raw tables from ftp site
  ncftpget ${ftp_site} ./${mysql_go_name} /pub/current_mysql/${mysql_go_name}/*.txt.gz
  for q in ./${mysql_go_name}/*.txt.gz
  do
	gunzip $q
  done 

  ncftpget ${ftp_site} ./ /pub/current_mysql/${mysql_go_name}/${mysql_go_name}.sql.gz
  gunzip ./${mysql_go_name}.sql.gz

  #create local mysql db
  echo "create database if not exists genmapp_${mysql_go_name}" | ${mysql} -u genmapp -pfun4genmapp
  #create schema
  ${mysql} -u genmapp -pfun4genmapp genmapp_${mysql_go_name} < ./${mysql_go_name}.sql
  #place in own scope due to "cd" command
  (
   cd ./${mysql_go_name}; for table_name in *.txt
   do
    ${mysqlimport} -u genmapp -pfun4genmapp genmapp_${mysql_go_name} `pwd`/${table_name}
  done
 )

  # zip, archive and clean up
  gzip ./${mysql_go_name}.sql
  tar cvzf ./${mysql_go_name}.txt.tgz ./${mysql_go_name}/*
  rm -R ./${mysql_go_name}/

fi  # end ontology grab

  # grab core tables
  export species_name="$species_name"; var=`echo "ls -l /pub/current_mysql/" | ncftp ${ftp_site} | perl -ane 'if ( $_ =~ /$Logged in to/ ) { $go=1; next } if ( $go ) { @a=split; $fileName=$a[ scalar( @a ) - 1]; $compareStr=$ENV{ species_name }."_core"; if ( $fileName =~/^$compareStr/) { print $fileName."\n"; } }'`; set -- $var; mysql_core_name=$1 

  # check variable before proceeding
  if [[ $mysql_core_name =~ 'species_list' || $mysql_core_name == '' ]]
  then
        echo "Skip!:  $species_name _core not found at $ftp_site. Please fix file: $species_list"
        continue
  fi

  echo "** $mysql_core_name **"
  mkdir ./${mysql_core_name}
 
  # get raw tables from ftp site
  ncftpget ${ftp_site} ./${mysql_core_name} /pub/current_mysql/${mysql_core_name}/*.txt.gz
  for q in ./${mysql_core_name}/*.txt.gz
  do
    # skip huge, unused dna and protein tables
    if [[ $q =~ 'dna' || $q =~ 'protein' ]]
    then
	echo "Skip!: large, unused dna and protein tables: $q"
	continue
    fi
    gunzip $q
  done
 
  ncftpget ${ftp_site} ./ /pub/current_mysql/${mysql_core_name}/${mysql_core_name}.sql.gz
  gunzip ./${mysql_core_name}.sql.gz

  #create local mysql db
  echo "create database if not exists genmapp_${mysql_core_name}" | ${mysql} -u genmapp -pfun4genmapp
  #create schema
  ${mysql} -u genmapp -pfun4genmapp genmapp_${mysql_core_name} < ./${mysql_core_name}.sql
  #place in own scope due to "cd" command
  (
   cd ./${mysql_core_name}; for table_name in *.txt
   do
    #import data
    ${mysqlimport} -u genmapp -pfun4genmapp genmapp_${mysql_core_name} `pwd`/${table_name}
  done
 )

# zip, archive and clean up
gzip ./${mysql_core_name}.sql
tar cvzf ./${mysql_core_name}.txt.tgz ./${mysql_core_name}/*
rm -R ./${mysql_core_name}/

  # grab funcgen tables
  export species_name="$species_name"; var=`echo "ls -l /pub/current_mysql/" | ncftp ${ftp_site} | perl -ane 'if ( $_ =~ /$Logged in to/ ) { $go=1; next } if ( $go ) { @a=split; $fileName=$a[ scalar( @a ) - 1]; $compareStr=$ENV{ species_name }."_funcgen"; if ( $fileName =~/^$compareStr/) { print $fileName."\n"; } }'`; set -- $var; mysql_efg_name=$1

  # check variable before proceeding
  if [[ $mysql_efg_name =~ 'species_list' || $mysql_efg_name == '' ]]
  then
        echo "Skip!:  $species_name _funcgen not found at $ftp_site. Please fix file: $species_list"
        continue
  fi

  echo "** $mysql_efg_name **"
  mkdir ./${mysql_efg_name}

  # get raw tables from ftp site
  ncftpget ${ftp_site} ./${mysql_efg_name} /pub/current_mysql/${mysql_efg_name}/*.txt.gz
  for q in ./${mysql_efg_name}/*.txt.gz
  do
    # skip huge, unused result table
    if [[ $q =~ 'result' ]]
    then
        echo "Skip!: large, unused result table: $q"
        continue
    fi
    gunzip $q
  done

  ncftpget ${ftp_site} ./ /pub/current_mysql/${mysql_efg_name}/${mysql_efg_name}.sql.gz
  gunzip ./${mysql_efg_name}.sql.gz

  #create local mysql db
  echo "create database if not exists genmapp_${mysql_efg_name}" | ${mysql} -u genmapp -pfun4genmapp
  #create schema
  ${mysql} -u genmapp -pfun4genmapp genmapp_${mysql_efg_name} < ./${mysql_efg_name}.sql
  #place in own scope due to "cd" command
  (
   cd ./${mysql_efg_name}; for table_name in *.txt
   do
    #import data
    ${mysqlimport} -u genmapp -pfun4genmapp genmapp_${mysql_efg_name} `pwd`/${table_name}
  done
 )
 # apply NathJohnsonPatch to alter xref and object_xref tables to support
 # gene-level access to array probe annotations
 #cat NathJohnson_DB_patch.sql | sed s/+++CORE+++/${mysql_core_name}/g > NathJohnson_DB_patch_TEMP.sql
 #${mysql} -u genmapp -pfun4genmapp -D ${mysql_efg_name} -e "source NathJohnson_DB_patch_TEMP.sql;"
 #rm NathJohnson_DB_patch_TEMP.sql

# zip, archive and clean up
gzip ./${mysql_efg_name}.sql
tar cvzf ./${mysql_efg_name}.txt.tgz ./${mysql_efg_name}/*
rm -R ./${mysql_efg_name}/

done
 
