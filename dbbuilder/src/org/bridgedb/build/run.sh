cat runList | while read x y
do
  echo $x $y
  bash -x scripts/buildPathVisio.sh $x $y > temp.$x.$y 2> temp.$x.$y.err
done
