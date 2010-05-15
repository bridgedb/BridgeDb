## Beef up a MySQL Database for hosted access
## A.Pico 20100209

## Beef up 'datanode'
## Use 'Name' for GO-Slim additions
INSERT INTO datanode
SELECT genmapp_XXXXXX_CS_YYYYYY.gene.Name, genmapp_XXXXXX_CS_YYYYYY.gene.Code
FROM genmapp_XXXXXX_CS_YYYYYY.gene
WHERE genmapp_XXXXXX_CS_YYYYYY.gene.Name != ''
AND genmapp_XXXXXX_CS_YYYYYY.gene.Code IN ("Tb", "Tc", "Tm");

## Beef up 'link'
## Use 'gene.Name' for GO-Slim additions
INSERT INTO link
SELECT DISTINCT id_left, code_left, Name, code_right
FROM genmapp_XXXXXX_CS_YYYYYY.link left join genmapp_XXXXXX_CS_YYYYYY.gene on genmapp_XXXXXX_CS_YYYYYY.link.id_right = genmapp_XXXXXX_CS_YYYYYY.gene.id
WHERE genmapp_XXXXXX_CS_YYYYYY.link.Code_Right IN ("Tb", "Tc", "Tm");


