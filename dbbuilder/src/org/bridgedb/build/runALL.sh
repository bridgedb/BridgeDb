cat runList | while read x y z w v
do
 # check if commented out
 if [[ $x =~ '#' ]]
 then
        echo "Skip!: $x $y commented out in file: runList"
        continue
 fi

  echo $x $y $z $w
  (
  cd /home/socr/c/users2/apico/EnsemblAPI
  perl Ensembl_ETL_Device_v18_local.pl $x $y $z $w 
  ) 
  bash -x scripts/buildPathVisio.sh $x $y $v > temp.$x.$y 2> temp.$x.$y.err
done

# report from temp.err files
grep -n ERROR temp.*.err


