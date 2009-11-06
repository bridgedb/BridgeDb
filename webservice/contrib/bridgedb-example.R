## If you want to use a local bridgedb idmapper in R, setup a local service
## and change the url below. See http://bridgedb.org/wiki/LocalService
## for information on how to run a local service.
webservice = "http://webservice.bridgedb.org"

organism = "Human"

mapID = function(id, code, targetCode = "") {
	url = paste(webservice, organism, "xrefs", code, id, sep="/")
	if(targetCode != "") {
		url = paste(url, "?dataSource=", targetCode, sep="")
	}
	message("Downloading xrefs from ", url)
	read.table(url, sep="\t")
}

# Map Entrez Gene id 1234 to all other datasources
xrefs = mapID("1234", "L")
xrefs
# Map Entrez Gene id 1234 to Affymetrix only
xrefs.affy = mapID("1234", "L", "X")
xrefs.affy
