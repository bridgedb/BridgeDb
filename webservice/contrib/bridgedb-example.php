<table>
<FORM action="example.php" method = "post">
<tr><td>Id:</td><td>  <INPUT type="text" name="id"></td></tr>
<tr><td>Code:</td><td> <INPUT type="text" name="code"></td></tr>
<tr><td>Species: </td><td>  <SELECT name="species">
      <OPTION value="Human">Human</OPTION>
      <OPTION value="Mouse">Mus Musculus</OPTION>
  </SELECT></td></tr>
<tr><td></td><td>  <INPUT type="submit"></td></tr>
</form>
</table>

<?php
   $url = "http://localhost:8183";
   if (isset($_POST["species"])){
      $lines =  file($url."/model/".urlencode($_POST["species"])."/".urlencode($_POST["code"])."/".urlencode($_POST["id"])."/xrefs");
      print "<table><tr><th>Source</th><th>Code</th></tr>\n";
      foreach ($lines as $line){
         list($code, $source) = split("\t", $line);
         print "<tr><td>$source</td><td>$code</td></tr>\n";
      }
      print "</table>";
   }
?>
