webservice = "http://localhost:8183"
organism = "Human"

mapID = function(id, code, targetCode = "") {
	url = paste(webservice, "model", organism, code, id, "xrefs", sep="/")
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
