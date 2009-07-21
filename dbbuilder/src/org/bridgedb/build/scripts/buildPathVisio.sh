## Substitute species intials and dates below
## Then cut and paste commands

DatabaseSpecies=$1
DatabaseDate=$2
Database=$1_Derby_$2

ScriptsDir=/home/apico/Derby/scripts

cat ${ScriptsDir}/PathVisioMySQL_BUILD.sql.template | sed "s/XXXXXX/$DatabaseSpecies/g" | sed "s/YYYYYY/$DatabaseDate/g" > PathVisioMySQL_BUILD.sql

## Build a MySQL Database for PathVisio: 
mysql -u genmapp -pfun4genmapp < PathVisioMySQL_BUILD.sql 


## Clean up MySQL-PathVisio Database
# Replace semicolons from backpageText fields
#  Otherwise, derby build will crash when semicolons 
#  happen to precede key words (e.g., "negative")
#mysql -u genmapp -pfun4genmapp -e "update ${Database}.datanode set backpageText =replace(backpageText, \";\", \".\")";  

# remove pipes from around species name in Info table
mysql -u genmapp -pfun4genmapp -e "update ${Database}.info set species =replace(species, \"|\", \"\")"; 

## Dump MySQL-PathVisio Database to .sql
# --net_buffer_length=30K (used to limit size of extended-inserts) 
# --skip-extended-insert (used to force line-by-line inserts)
mysqldump --compatible=db2 --no-create-info --skip-add-locks --skip-disable-keys --skip-quote-names --net_buffer_length=30K -u genmapp -pfun4genmapp ${Database} > ${Database}.db2.sql.temp

## Clean up dump file
# Replace in emacs using esc-% (! = all occurances)
# Replace in vi using :%s/old/new/g (use vi if file is too large for emacs)
cat ${Database}.db2.sql.temp | sed "s/\/\*/--/g" | sed "s/\\\'/\'\'/g" > ${Database}.db2.sql
 	
## Create & Fill Derby via ij tool 
#Setup Environment
export DERBY_INSTALL=/home/apico/Derby/db-derby-10.4.2.0-bin/
export CLASSPATH=$DERBY_INSTALL/lib/derby.jar:$DERBY_INSTALL/lib/derbytools.jar:.
# Run ij Tool
cat ${ScriptsDir}/PathVisioDerby_BUILD.ij.template | sed "s/XXXXXX/${Database}/g" | perl ${ScriptsDir}/replacePath.pl ${ScriptsDir} > PathVisioDerby_BUILD.ij 

java -XX:MaxPermSize=128m -Xmx1024m org.apache.derby.tools.ij PathVisioDerby_BUILD.ij

# Package it up
mv ${Database} database
zip -r -0 ${Database}.bridge database/
mv database ${Database}

# report on products (check size > 1.9M)
ls -lh ${Database}.bridge | echo

# BELOW: Not yet automated
# - ssh-key authentication not working
#scp ${Database}.bridge genmappftp@conklinwolf.ucsf.edu: (password=genmappftp)
#scp ${Database}.bridge jeff@conklinwolf.ucsf.edu:/home2/GenMAPP2_DataTreeRoot/Derby/ (1357jeff)

# Install at WikiPathways
#scp Gramene_Derby_20081109.bridge wikipathways@www.wikipathways.org:/home/wikipathways/database/Gramene_Derby_20081109.bridge
#ssh wikipathways@www.wikipathways.org
#cd /home/wikipathways/database
#unzip Gramene_Derby_20081109.bridge
#mv database Gramene_Derby_20081109
#rm <symbolic link> (e.g., Homo\ sapiens)
#ln -s Gramene_Derby_20081109 <symbolic link> (e.g., Homo\ sapiens)
#  Check: ls -l
