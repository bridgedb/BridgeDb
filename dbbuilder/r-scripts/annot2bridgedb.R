#####################################################
## Extract mappings from a bioconductor annotation ##
## package and write to IDMapperText file.         ##
#####################################################
outPath = "~/data/bridgedb/"

asBridgeDb = function(annotList, outFile, dsLeft, dsRight = "Entrez Gene") {
  if(sum(sapply(annotList, length) > 1) == 0) { #Easy and fast mapping to table
    tbl = cbind(names(annotList), annotList)
    tbl = tbl[!is.na(tbl[,2]), ]
  } else { #one to many mapping, use slow way
    tbl = cbind("", "")
    for(n in names(annotList)) {
      mapping = annotList[[n]]
      rows = cbind(rep(n, length(mapping)), mapping)
      tbl = rbind(tbl, rows)
    }
    tbl = tbl[2:nrow(tbl),]
  }
  colnames(tbl) = c(dsLeft, dsRight)
  write.table(tbl, outFile, row.names=F, quote=F, sep="\t")
}

## Illumina mouse
library("illuminaMousev2.db")
asBridgeDb(as.list(illuminaMousev2ENTREZID), paste(outPath, "illumina_mouse.txt", sep=""), "Illumina")

## EC to Entrez Gene mouse
library("org.Mm.eg.db")
asBridgeDb(as.list(org.Mm.egENZYME2EG), paste(outPath, "ec_eg_mouse.txt", sep=""), "EC Number")