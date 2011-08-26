##############################################
## Create IDMapperText files for ec -> gene ##
## mappings for several species             ##
##############################################
source("annot2bridgedb.R")

installed = function(pkg) {
  is.element(pkg, installed.packages()[,1])
}

codes = c(
  "Ag", "Bt", "Ce", "Cf", "Dm", "Dr", "Gg", "Hs", "Mm",
  "Pt", "Rn"
)

datestamp = format(Sys.time(), "%Y%m%d")

for(code in codes) {
  pkg = paste("org.", code, ".eg.db", sep="")
  if(!installed(pkg)) {
    source("http://www.bioconductor.org/biocLite.R")
    biocLite(pkg)
  }
  library(pkg, character.only = T)
  expr = paste("as.list(org.", code, ".egENZYME2EG)", sep="")
  annot = eval(parse(text=expr))
  asBridgeDb(annot, 
    paste(outPath, "ec_to_entrezgene_", code, "_", datestamp, ".txt", sep=""), "EC Number")
}