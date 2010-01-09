## Substitute species intials and dates below
## Then cut and paste commands

DatabaseSpecies=$1
DatabaseDate=$2
Database=$1_Derby_$2
DatabaseCS=$1_CS_$2

ScriptsDir=/home/apico/Derby/scripts

## Special modification for Yeast database: swapping in SGD identifiers from external file
if [[ $DatabaseSpecies == Sc ]]; then
	rm ${ScriptsDir}/registry.genenames.tab
	wget http://downloads.yeastgenome.org/gene_registry/registry.genenames.tab
	mv registry.genenames.tab ${ScriptsDir}/.
	mysql -u genmapp -pfun4genmapp -e "create table ${DatabaseCS}.sgd (a varchar(31), b varchar(31), c varchar(31), d varchar(31), e varchar(31), f varchar(31), g varchar(31))";
	mysql -u genmapp -pfun4genmapp -e "load data local infile '${ScriptsDir}/registry.genenames.tab' into table ${DatabaseCS}.sgd fields terminated by '\t' lines terminated by '\n'";
	mysql -u genmapp -pfun4genmapp -e "create index i_a on ${DatabaseCS}.sgd (a)";
	mysql -u genmapp -pfun4genmapp -e "update ${DatabaseCS}.gene, ${DatabaseCS}.sgd set ${DatabaseCS}.gene.ID = ${DatabaseCS}.sgd.g where ${DatabaseCS}.gene.Symbol = ${DatabaseCS}.sgd.a and ${DatabaseCS}.gene.Code = 'D'";
	mysql -u genmapp -pfun4genmapp -e "update ${DatabaseCS}.attr, ${DatabaseCS}.sgd set ${DatabaseCS}.attr.ID = ${DatabaseCS}.sgd.g where ${DatabaseCS}.attr.Value = ${DatabaseCS}.sgd.a and ${DatabaseCS}.attr.Code = 'D'";
	mysql -u genmapp -pfun4genmapp -e "alter table ${DatabaseCS}.link add column (ID_Right2 varchar(31))";
	mysql -u genmapp -pfun4genmapp -e "update ${DatabaseCS}.link set ID_Right2 = ID_Right where Code_Right = 'D'";
	mysql -u genmapp -pfun4genmapp -e "update ${DatabaseCS}.link, ${DatabaseCS}.sgd set ${DatabaseCS}.link.ID_Right = ${DatabaseCS}.sgd.g where ${DatabaseCS}.link.ID_Right2 = ${DatabaseCS}.sgd.a and ${DatabaseCS}.link.Code_Right = 'D'";
	mysql -u genmapp -pfun4genmapp -e "alter table ${DatabaseCS}.link drop column ID_Right2";
fi


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
## Dump table creation statements separately for 2-step recontruction later (e.g., for bridgedb rest server)
mysqldump --no-data -u genmapp -pfun4genmapp ${Database} > ${Database}.db2.sql.tables

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
