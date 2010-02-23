<table>
<FORM action="bridgedb-example.php" method = "post">
<tr><td>Id:</td><td>  <INPUT type="text" name="id"></td></tr>
<tr><td>Code:</td><td> <INPUT type="text" name="code"></td></tr>
<tr><td>Species: </td><td>  <SELECT name="species">
      <OPTION value="Human">Homo sapiens</OPTION>
      <OPTION value="Mouse">Mus Musculus</OPTION>
  </SELECT></td></tr>
<tr><td></td><td>  <INPUT type="submit"></td></tr>
</form>
</table>

<?php
/*
 * If you want to use a local bridgedb idmapper in php, setup a local service
 * and change the url below. See http://bridgedb.org/wiki/LocalService
 * for information on how to run a local service.
 */
$url = "http://webservice.bridgedb.org";
if (isset($_POST["species"])){
	$species = urlencode($_POST["species"]);
	$code = urlencode($_POST["code"]);
	$id = urlencode($_POST["id"]);
	$url .= "/$species/xrefs/$code/$id";
	$lines = file($url);
	print "<h3>Results for $id, $code, in $species</h3>";
		print "<table><tr><th>Source</th><th>Code</th></tr>\n";
		foreach ($lines as $line){
		list($code, $source) = split("\t", $line);
		print "<tr><td>$source</td><td>$code</td></tr>\n";
	}
	print "</table>";
}
?>
