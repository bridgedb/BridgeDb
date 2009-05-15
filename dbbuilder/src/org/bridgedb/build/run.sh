cat runList | while read x y
do
 # check if commented out
 if [[ $x =~ '#' ]]
 then
        echo "Skip!: $x $y commented out in file: runList"
        continue
 fi

  echo $x $y
  bash -x scripts/buildPathVisio.sh $x $y > temp.$x.$y 2> temp.$x.$y.err
done

# report from temp.err files
grep -n ERROR temp.*.err


